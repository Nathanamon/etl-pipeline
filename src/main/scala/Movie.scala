case class Movie(
                  id: Int,
                  title: String,
                  year: Int,
                  runtime: Int,
                  genres: List[String],
                  director: Option[String],
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
                        averageRoi: Double,
                        bestRoi : Double
                        )
