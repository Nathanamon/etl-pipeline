object StatsCalculator {

  /**
   * Top 10 des films les mieux notés
   */
  def top10Rated(movies: List[Movie]): List[TopMovies] = {
    movies
      .filter(m => m.votes >= 10000)
      .sortBy(m => -m.rating)
      .take(10)
      .map(m => TopMovies(m.title, m.year, m.rating, m.votes))
  }

  /**
   * Top 10 des films les plus votés
   */
  def top10ByVotes(movies: List[Movie]): List[TopMovies] = {
    movies
      .sortBy(m => -m.votes)
      .take(10)
      .map(m => TopMovies(m.title, m.year, m.rating, m.votes))
  }

  /**
   * Top 10 Box-Office (Revenue)
   */
  def top10BoxOffice(movies: List[Movie]): List[Movie] = {
    movies
      .filter(_.revenue.exists(_ > 0))
      .sortBy(m => -m.revenue.getOrElse(0.0))
      .take(10)
  }

  /**
   * Top 10 Budgets
   */
  def top10Budget(movies: List[Movie]): List[Movie] = {
    movies
      .filter(_.budget.exists(_ > 0))
      .sortBy(m => -m.budget.getOrElse(0.0))
      .take(10)
  }

  /**
   * Compte par décennie
   */
  def moviesByDecade(movies: List[Movie]): Map[String, Int] = {
    movies
      .groupBy(m => (m.year / 10) * 10 + "s")
      .map { case (decade, list) => (decade, list.length) }
  }

  /**
   * Compte par Genre
   */
  def moviesByGenre(movies: List[Movie]): Map[String, Int] = {
    movies
      .flatMap(_.genres) 
      .groupBy(identity) 
      .map { case (genre, list) => (genre, list.length) }
  }

  /**
   * Note moyenne par Genre
   */
  def avgRatingByGenre(movies: List[Movie]): Map[String, Double] = {
    movies
      .flatMap(m => m.genres.map(g => (g, m.rating)))
      .groupBy { case (genre, _) => genre }
      .map { case (genre, list) =>
        val ratings = list.map(_._2)
        val avg = if (ratings.nonEmpty) ratings.sum / ratings.size else 0.0
        (genre, avg)
      }
  }

  /**
   * Durée moyenne par Genre
   */
  def avgRuntimeByGenre(movies: List[Movie]): Map[String, Double] = {
    movies
      .flatMap(m => m.genres.map(g => (g, m.runtime)))
      .groupBy { case (genre, _) => genre }
      .map { case (genre, list) =>
        val runtimes = list.map(_._2)
        val avg = if (runtimes.nonEmpty) runtimes.sum.toDouble / runtimes.size else 0.0
        (genre, avg)
      }
  }

  /**
   * Top 5 Réalisateurs les plus prolifiques
   */
  def mostProlificDirectors(movies: List[Movie]): List[DirectorStat] = {
    movies
      .flatMap(_.director)
      .groupBy(identity)
      .map { case (dir, list) => DirectorStat(dir, list.length) }
      .toList
      .sortBy(stat => -stat.count)
      .take(5)
  }

  /**
   * Top 5 Acteurs les plus fréquents
   */
  def mostFrequentActors(movies: List[Movie]): List[ActorStat] = {
    movies
      .flatMap(_.cast)
      .groupBy(identity)
      .map { case (actor, list) => ActorStat(actor, list.length) }
      .toList
      .sortBy(stat => -stat.count)
      .take(5)
  }

  /**
   * Statistiques de Rentabilité (Bonus)
   */
  def calculateProfitability(movies: List[Movie]): Profitability = {
    val financials = movies.filter(m => m.revenue.exists(_ > 0) && m.budget.exists(_ > 0))

    // Films rentables
    val profitableCount = financials.count(m => m.revenue.get > m.budget.get)

    // Calcul du ROI moyen
    val rois = financials.map { m =>
      val r = m.revenue.get
      val b = m.budget.get
      (r - b) / b
    }
    val avgRoi = if (rois.nonEmpty) rois.sum / rois.size else 0.0
    val maxRoi = if (rois.nonEmpty) rois.max else 0.0

    Profitability(profitableCount, avgRoi, maxRoi)
  }
}