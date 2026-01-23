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


  def writeTextReport(report: GlobalReport, path: String): Either[String, Unit] = {
    Try {
      val sb = new StringBuilder
      sb.append("===============================================\n")
      sb.append("     RAPPORT D'ANALYSE - FILMS & SÉRIES\n")
      sb.append("===============================================\n\n")

      sb.append("📊 STATISTIQUES DE PARSING\n")
      sb.append("----------------------------\n")
      sb.append(f"- Entrées totales lues      : ${report.statistics.totalMoviesParsed}\n")
      sb.append(f"- Entrées valides           : ${report.statistics.totalMoviesValid}\n")
      sb.append(f"- Erreurs de parsing        : ${report.statistics.parsingErrors}\n")
      sb.append(f"- Doublons supprimés        : ${report.statistics.duplicatesRemoved}\n\n")

      sb.append("⭐ TOP 10 - MEILLEURS FILMS\n")
      sb.append("----------------------------\n")
      report.top10Rated.zipWithIndex.foreach { case (m, i) =>
        sb.append(f"${i+1}. ${m.title} (${m.year}) : ${m.rating} (${m.votes} votes)\n")
      }
      sb.append("\n")


      sb.append("💰 RENTABILITÉ\n")
      sb.append("---------------\n")
      sb.append(f"- Films rentables           : ${report.profitability.count}\n")
      sb.append(f"- ROI moyen                 : ${report.profitability.averageRoi}x\n")

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