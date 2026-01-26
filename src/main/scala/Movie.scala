case class Movie(
                  id: Int,
                  title: String,
                  year: Int,
                  runtime: Int,
                  genres: List[String],
                  director: String,
                  cast: List[String],
                  rating: Double,
                  votes: Int,
                  revenue: Option[Double],
                  budget: Option[Double],
                  language: String
                )

case class MoviesStats(
                        totalMoviesParsed: Int,
                        totalMoviesValid: Int,
                        parsingErrors: Int,
                        validationErrors: Int,
                        duplicatesRemoved: Int
                      )

case class GlobalReport(
                         statistics: MoviesStats,
                         top10Rated: List[TopMovies],
                         top10ByVotes: List[TopMovies],
                         top10BoxOffice: List[Movie],
                         top10Budget: List[Movie],
                         moviesByDecade: Map[String, Int],
                         moviesByGenre: Map[String, Int],
                         avgRatingByGenre: Map[String, Double],
                         avgRuntimeByGenre: Map[String, Double],
                         mostProlificDirectors: List[DirectorStat],
                         mostFrequentActors: List[ActorStat],
                         profitability: Profitability
                       )

case class JsonReport(
                       statistics: JsonStats,
                       top_10_rated: List[TopMovies],
                       top_10_by_votes: List[TopMovies],
                       highest_grossing: List[Movie],
                       most_expensive: List[Movie],
                       movies_by_decade: Map[String, Int],
                       movies_by_genre: Map[String, Int],
                       average_rating_by_genre: Map[String, Double],
                       average_runtime_by_genre: Map[String, Double],
                       most_prolific_directors: List[DirectorStat],
                       most_frequent_actors: List[ActorStat],
                       profitable_movies: JsonProfitability
                     )

case class JsonStats(
                      total_movies_parsed: Int,
                      total_movies_valid: Int,
                      parsing_errors: Int,
                      duplicates_removed: Int
                    )

case class JsonProfitability(
                              count: Int,
                              average_roi: Double
                            )

case class TopMovies(
                      title: String,
                      year: Int,
                      rating: Double,
                      votes: Int
                    )

case class ActorStat(
                      actor: String,
                      count: Int
                    )

case class DirectorStat(
                         director: String,
                         count: Int
                       )

case class Profitability(
                          count: Int,
                          averageRoi: Double,
                          bestRoi: Double // Nécessaire pour le TXT, mais exclu du JSON via le mapping
                        )