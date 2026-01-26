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
   * Écrit le rapport en JSON avec la structure exacte demandée (snake_case).
   */
  def writeJsonReport(report: GlobalReport, path: String): Either[String, Unit] = {
    Try {
      // Mapping vers la structure JSON spécifique (DTO)
      val jsonReport = JsonReport(
        statistics = JsonStats(
          total_movies_parsed = report.statistics.totalMoviesParsed,
          total_movies_valid = report.statistics.totalMoviesValid,
          parsing_errors = report.statistics.parsingErrors,
          duplicates_removed = report.statistics.duplicatesRemoved
        ),
        top_10_rated = report.top10Rated,
        top_10_by_votes = report.top10ByVotes,
        highest_grossing = report.top10BoxOffice,
        most_expensive = report.top10Budget,
        movies_by_decade = report.moviesByDecade,
        movies_by_genre = report.moviesByGenre,
        average_rating_by_genre = report.avgRatingByGenre,
        average_runtime_by_genre = report.avgRuntimeByGenre,
        most_prolific_directors = report.mostProlificDirectors,
        most_frequent_actors = report.mostFrequentActors,
        profitable_movies = JsonProfitability(
          count = report.profitability.count,
          average_roi = report.profitability.averageRoi
        )
      )

      val jsonString = jsonReport.asJson.spaces2
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

      sb.append("===============================================\n")
      sb.append("     RAPPORT D'ANALYSE - FILMS & SÉRIES\n")
      sb.append("===============================================\n\n")

      sb.append("📊 STATISTIQUES DE PARSING\n")
      sb.append("---------------------------\n")
      sb.append(f"- Entrées totales lues      : ${stat.totalMoviesParsed}\n")
      sb.append(f"- Entrées valides           : ${stat.totalMoviesValid}\n")
      sb.append(f"- Erreurs de parsing        : ${stat.parsingErrors}\n")
      sb.append(f"- Erreurs de validation     : ${stat.validationErrors}\n")
      sb.append(f"- Doublons supprimés        : ${stat.duplicatesRemoved}\n\n")

      sb.append("⭐ TOP 10 - MEILLEURS FILMS\n")
      sb.append("----------------------------\n")
      report.top10Rated.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} (${m.year}) : ${m.rating} (${m.votes} votes)\n")
      }
      sb.append("\n")

      sb.append("📊 TOP 10 - PLUS VOTÉS\n")
      sb.append("-----------------------\n")
      report.top10ByVotes.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} : ${m.votes} votes\n")
      }
      sb.append("\n")

      sb.append("💰 TOP 10 - BOX-OFFICE\n")
      sb.append("-----------------------\n")
      report.top10BoxOffice.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} : ${m.revenue.getOrElse(0.0)}%.0f M$$\n")
      }
      sb.append("\n")

      sb.append("💸 TOP 10 - BUDGETS\n")
      sb.append("-------------------\n")
      report.top10Budget.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i + 1}. ${m.title} : ${m.budget.getOrElse(0.0)}%.0f M$$\n")
      }
      sb.append("\n")

      sb.append("📅 RÉPARTITION PAR DÉCENNIE\n")
      sb.append("----------------------------\n")
      report.moviesByDecade.toList.sortBy(_._1).foreach { case (decade, count) =>
        sb.append(f"- $decade%-25s : $count films\n")
      }
      sb.append("\n")

      sb.append("🎭 RÉPARTITION PAR GENRE\n")
      sb.append("-------------------------\n")
      report.moviesByGenre.toList.sortBy(-_._2).foreach { case (genre, count) =>
        sb.append(f"- $genre%-25s : $count films\n")
      }
      sb.append("\n")

      sb.append("📈 MOYENNES PAR GENRE\n")
      sb.append("----------------------\n")
      sb.append("NOTE MOYENNE :\n")
      report.avgRatingByGenre.toList.sortBy(-_._2).foreach { case (genre, rating) =>
        sb.append(f"- $genre%-25s : $rating%.1f/10\n")
      }

      sb.append("\nDURÉE MOYENNE :\n")
      report.avgRuntimeByGenre.toList.sortBy(-_._2).foreach { case (genre, runtime) =>
        sb.append(f"- $genre%-25s : $runtime%.0f minutes\n")
      }
      sb.append("\n")

      sb.append("🎬 TOP 5 - RÉALISATEURS\n")
      sb.append("------------------------\n")
      report.mostProlificDirectors.zipWithIndex.foreach { case (stat, i) =>
        sb.append(f"${i + 1}. ${stat.director}%-25s : ${stat.count} films\n")
      }
      sb.append("\n")

      sb.append("🎭 TOP 5 - ACTEURS\n")
      sb.append("-------------------\n")
      report.mostFrequentActors.zipWithIndex.foreach { case (stat, i) =>
        sb.append(f"${i + 1}. ${stat.actor}%-25s : ${stat.count} films\n")
      }
      sb.append("\n")

      sb.append("💵 RENTABILITÉ\n")
      sb.append("--------------\n")
      sb.append(f"- Films rentables           : ${report.profitability.count}\n")
      sb.append(f"- ROI moyen                 : ${report.profitability.averageRoi}%.2fx\n")
      sb.append(f"- Meilleur ROI              : ${report.profitability.bestRoi}%.2fx\n")
      sb.append("\n")

      sb.append("⏱️  PERFORMANCE\n")
      sb.append("---------------\n")
      sb.append(f"- Temps de traitement       : $processingTimeSec%.3f secondes\n")
      val entriesPerSec = if (processingTimeSec > 0) stat.totalMoviesParsed / processingTimeSec else 0.0
      sb.append(f"- Entrées/seconde           : $entriesPerSec%.0f\n")

      sb.append("\n===============================================\n")

      val filePath = Paths.get(path)
      Option(filePath.getParent).foreach(Files.createDirectories(_))
      Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8))

    } match {
      case Success(_) => Right(())
      case Failure(ex) => Left(s"Erreur d'écriture TXT : ${ex.getMessage}")
    }
  }
}