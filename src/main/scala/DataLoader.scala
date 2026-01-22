import io.circe._
import io.circe.generic.auto.*
import io.circe.parser.*
import scala.io.Source
import scala.util.{Failure, Success, Try}

object DataLoader {

  /**
   * Lit un fichier JSON et parse les movies
   */
  def loadMovies(filename: String): Either[String, (List[Movie], Int)] = {
    Try {
      val source = Source.fromFile(filename)
      val content = source.mkString
      source.close()
      content
    } match {
      case Success(content) =>
        decode[List[Json]](content) match {
          case Left(error) =>
            Left(s"Erreur de syntaxe JSON : ${error.getMessage}")

          case Right(jsonList) =>
            val results = jsonList.map(_.as[Movie])
            val validMovies = results.collect { case Right(movies) => movies }

            val parsingErrorsCount = results.count(_.isLeft)
            Right((validMovies, parsingErrorsCount))
        }
      case Failure(exception) =>
        Left(s"File error: ${exception.getMessage}")
    }
  }
}
