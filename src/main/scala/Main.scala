object Main extends App {

  println("🚀 Mini-ETL : Analyse de Films\n")
  val startTime = System.nanoTime()
  val filename = "data/data_dirty.json"
  
  val result = for {
    loadedData <- DataLoader.loadMovies(filename)
    (rawMovies, parsingErrors) = loadedData // Décomposition du tuple
    _ = println(s"EXTRACT   : ${rawMovies.size} films lus (et $parsingErrors erreurs de parsing)")
    
    validMovies = {
      val valid = DataValidator.filterValid(rawMovies)
      println(s"TRANSFORM : ${valid.size} films valides conservés")
      valid
    }

    statsParsing = {
      val validCount = validMovies.size
      val parsingErr = parsingErrors
      val validationErr = rawMovies.count(m => !DataValidator.isValid(m))
      val duplicates = rawMovies.count(DataValidator.isValid) - validCount

      MoviesStats(
        totalMoviesParsed = rawMovies.size + parsingErr,
        totalMoviesValid = validCount,
        parsingErrors = parsingErr,
        validationErrors = validationErr,
        duplicatesRemoved = duplicates
      )
    }
    
    report = ReportGenerator.generateReport(validMovies, statsParsing)
    _ = println(s"REPORTING : Rapport statistique généré en mémoire")
    
    _ <- ReportGenerator.writeJsonReport(report, "output/results.json")
    _ = println(s"LOAD      : JSON sauvegardé dans output/results.json")
    _ <- ReportGenerator.writeTextReport(report, "output/report.txt")
    _ = println(s"LOAD      : Rapport texte sauvegardé dans output/report.txt")

  } yield report
}