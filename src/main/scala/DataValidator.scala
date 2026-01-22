object DataValidator {

  /**
   * Valide un restaurant selon les règles métier
   */
  def isValid(movie: Movie): Boolean = {
    (movie.rating >= 0 && movie.rating <= 10) && (movie.year >= 1895 && movie.year <= 2025) && (movie.runtime > 0) && movie.genres.nonEmpty
  }

  /**
   * Filtre les restaurants valides
   */
  def filterValid(movies: List[Movie]): List[Movie] = {
    movies.filter(isValid).distinctBy(_.id)
  }
}
