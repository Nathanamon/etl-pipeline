case class Movies(
                  id: Int,
                  title: String,
                  year: Int,
                  runtime: Int,
                  genres: List[String],
                  director: Option[String],
                  cast: List[String],
                  rating: Double,
                  votes: Int,
                  revenue: Option[Long],
                  budget: Option[Long],
                  language: String
                )

case class MoviesStats(
                        totalMoviesParsed: Int,
                        totalMoviesValid: Int,
                        parsingErrors: Int,
                        duplicatesRemoved: Int
                      )

case class TopMovies(
                      title: String,
                      year: Int,
                      rating: Double,
                      votes: Int
                    )
case class MoneyMovies(
                      title: String,
                      year: Int,
                      amount: Double
                      )
case class ActorStat(
                    actor: String,
                    count: Int
                    )
case class DirectorStat(
                       director: String,
                       count:Int
                       )
case class Profitability(
                        count : Int,
                        average_roi: Double
                        )
