import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.util.{Try, Success, Failure}

object ReportGenerator {

  /**
   * Génère le rapport complet en mémoire.
   */
  def generateReport(movies: List[Movie], parsingStats: MoviesStats): GlobalReport = {
    GlobalReport(
      statistics = parsingStats,
      top10Rated = StatsCalculator.top10Rated(movies),
      top10ByVotes = StatsCalculator.top10ByVotes(movies),
      top10BoxOffice = StatsCalculator.top10BoxOffice(movies),
      top10Budget = StatsCalculator.top10Budget(movies),
      moviesByDecade = StatsCalculator.moviesByDecade(movies),
      moviesByGenre = StatsCalculator.moviesByGenre(movies),
      avgRatingByGenre = StatsCalculator.avgRatingByGenre(movies),
      avgRuntimeByGenre = StatsCalculator.avgRuntimeByGenre(movies),
      mostProlificDirectors = StatsCalculator.mostProlificDirectors(movies),
      mostFrequentActors = StatsCalculator.mostFrequentActors(movies),
      profitability = StatsCalculator.calculateProfitability(movies)
    )
  }

  /**
   * Écrit le rapport en JSON sur le disque (results.json)
   */
  def writeJsonReport(report: GlobalReport, path: String): Either[String, Unit] = {
    Try {
      val jsonString = report.asJson.spaces2
      val filePath = Paths.get(path)
      Option(filePath.getParent).foreach(Files.createDirectories(_))


      Files.write(filePath, jsonString.getBytes(StandardCharsets.UTF_8))
    } match {
      case Success(_) => Right(())
      case Failure(ex) => Left(s"Erreur d'écriture JSON : ${ex.getMessage}")
    }
  }


  def writeTextReport(report: GlobalReport, path: String, processingTimeSec: Double = 0.0): Either[String, Unit] = {
    Try {
      val sb = new StringBuilder
      val stat = report.statistics

      // --- En-tête ---
      sb.append("===============================================\n")
      sb.append("     RAPPORT D'ANALYSE - FILMS & SÉRIES\n")
      sb.append("===============================================\n\n")

      // --- Statistiques de Parsing ---
      sb.append("📊 STATISTIQUES DE PARSING\n")
      sb.append("---------------------------\n")
      sb.append(f"- Entrées totales lues      : ${stat.totalMoviesParsed}%,d\n") // %,d ajoute un séparateur de milliers
      sb.append(f"- Entrées valides           : ${stat.totalMoviesValid}%,d\n")
      sb.append(f"- Erreurs de parsing        : ${stat.parsingErrors}%,d\n")
      sb.append(f"- Doublons supprimés        : ${stat.duplicatesRemoved}%,d\n\n")

      // --- Top 10 Meilleurs Films ---
      sb.append("⭐ TOP 10 - MEILLEURS FILMS\n")
      sb.append("----------------------------\n")
      report.top10Rated.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} (${m.year}) : ${m.rating}%.1f/10 (${m.votes}%,d votes)\n")
      }
      sb.append("\n")

      // --- Top 10 Plus Votés ---
      sb.append("📊 TOP 10 - PLUS VOTÉS\n")
      sb.append("-----------------------\n")
      report.top10ByVotes.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} : ${m.votes}%,d votes\n")
      }
      sb.append("\n")

      // --- Top 10 Box-Office ---
      sb.append("💰 TOP 10 - BOX-OFFICE\n")
      sb.append("-----------------------\n")
      report.top10BoxOffice.zipWithIndex.foreach { case (m, i) =>
        val revenueM = m.revenue.getOrElse(0.0) / 1000000
        sb.append(f"${i + 1}. ${m.title} : $revenueM%.1f M$$\n")
      }
      sb.append("\n")

      // --- Top 10 Budgets ---
      sb.append("💸 TOP 10 - BUDGETS\n")
      sb.append("-------------------\n")
      report.top10Budget.zipWithIndex.foreach { case (m, i) =>
        val budgetM = m.budget.getOrElse(0.0) / 1000000
        sb.append(f"${i + 1}. ${m.title} : $budgetM%.1f M$$\n")
      }
      sb.append("\n")

      // --- Répartition par Décennie (Tri chronologique) ---
      sb.append("📅 RÉPARTITION PAR DÉCENNIE\n")
      sb.append("----------------------------\n")
      report.moviesByDecade.toList.sortBy(_._1).foreach { case (decade, count) =>
        sb.append(f"- $decade%-25s : $count%,d films\n")
      }
      sb.append("\n")

      // --- Répartition par Genre (Tri par nombre de films décroissant) ---
      sb.append("🎭 RÉPARTITION PAR GENRE\n")
      sb.append("-------------------------\n")
      report.moviesByGenre.toList.sortBy(-_._2).foreach { case (genre, count) =>
        sb.append(f"- $genre%-25s : $count%,d films\n")
      }
      sb.append("\n")

      // --- Moyennes par Genre ---
      sb.append("📈 MOYENNES PAR GENRE\n")
      sb.append("----------------------\n")

      sb.append("NOTE MOYENNE :\n")
      report.avgRatingByGenre.toList.sortBy(-_._2).take(5).foreach { case (genre, rating) => // Limité au Top 5 ou affichage complet selon préférence, ici complet trié
        sb.append(f"- $genre%-25s : $rating%.1f/10\n")
      }


      sb.append("\nDURÉE MOYENNE :\n")
      report.avgRuntimeByGenre.toList.sortBy(-_._2).take(5).foreach { case (genre, runtime) =>
        sb.append(f"- $genre%-25s : $runtime%.0f minutes\n")
      }


      // --- Top 5 Réalisateurs ---
      sb.append("🎬 TOP 5 - RÉALISATEURS\n")
      sb.append("------------------------\n")
      /*report.mostProlificDirectors.foreach { stat =>
      }*/

      report.mostProlificDirectors.zipWithIndex.foreach { case (stat, i) =>
        sb.append(f"${i + 1}. ${stat.director}%-20s : ${stat.count} films\n")
      }
      sb.append("\n")

      // --- Top 5 Acteurs ---
      sb.append("🎭 TOP 5 - ACTEURS\n")
      sb.append("-------------------\n")
      report.mostFrequentActors.zipWithIndex.foreach { case (stat, i) =>
        sb.append(f"${i + 1}. ${stat.actor}%-20s : ${stat.count} films\n")
      }
      sb.append("\n")

      // --- Rentabilité ---
      sb.append("💵 RENTABILITÉ\n")
      sb.append("--------------\n")
      sb.append(f"- Films rentables           : ${report.profitability.count}%,d films\n")
      sb.append(f"- ROI moyen                 : ${report.profitability.averageRoi}%.2fx\n")
      sb.append(f"- Meilleur ROI              : ${report.profitability.bestRoi}%.2fx\n\n")

      // --- Performance ---
      sb.append("⏱️  PERFORMANCE\n")
      sb.append("---------------\n")
      sb.append(f"- Temps de traitement       : $processingTimeSec%.2f secondes\n")

      val entriesPerSec = if (processingTimeSec > 0) stat.totalMoviesParsed / processingTimeSec else 0.0
      sb.append(f"- Entrées/seconde           : $entriesPerSec%.0f\n")

      sb.append("\n===============================================\n")

      // Écriture fichier
      val filePath = Paths.get(path)
      Option(filePath.getParent).foreach(Files.createDirectories(_))
      Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8))

    } match {
      case Success(_) => Right(())
      case Failure(ex) => Left(s"Erreur d'écriture TXT : ${ex.getMessage}")
    }
  }
}