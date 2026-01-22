import io.circe._
import io.circe.generic.auto.*
import io.circe.parser.*
import scala.io.Source
import scala.util.{Failure, Success, Try}

object DataLoader {

  /**
   * Lit un fichier JSON et parse les restaurants
   */
  def loadMovies(filename: String): Either[String, List[Movies]] = {
    // TODO: Utiliser Try pour lire le fichier
    //   1. Créer un Source.fromFile(filename)
    //   2. Lire le contenu avec source.mkString
    //   3. Fermer le fichier avec source.close() - IMPORTANT !
    //   4. Parser avec decode[List[Restaurant]](content)
    //   5. Gérer les erreurs avec pattern matching
    Try {
      val source = Source.fromFile(filename)
      val content = source.mkString
      source.close()
      content
    } match {
      case Success(content) =>
        decode[List[Movies]](content) match {
          case Right(movies) => Right(movies)
          case Left(error) => Left(s"Parsing error: ${error.getMessage}")
        }
      case Failure(exception) =>
        Left(s"File error: ${exception.getMessage}")
    }
  }
}