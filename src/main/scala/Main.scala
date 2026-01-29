object Main extends App {

  println("🚀 Mini-ETL : Analyse Multi-fichiers\n")

  // Liste des fichiers à traiter définie dans les spécifications
  val datasets = List(
    "data/data_clean.json",
    "data/data_dirty.json",
    "data/data_large.json"
  )

  // Traitement de chaque dataset
  datasets.foreach { filename =>
    processFile(filename)
  }

  /**
   * Logique complète pour traiter un fichier, générer les stats et sauvegarder les rapports
   */
  def processFile(filename: String): Unit = {
    val baseName = filename.split("/").last.stripSuffix(".json")
    println(s"--- TRAITEMENT DE : $filename ---")

    val startTime = System.nanoTime()

    val result = for {
      // 1. EXTRACT
      loadedData <- DataLoader.loadMovies(filename)
      (rawMovies, parsingErrors) = loadedData

      // 2. TRANSFORM
      validMovies = {
        val valid = DataValidator.filterValid(rawMovies)
        valid
      }

      // 3. CALCUL DES STATS DE PARSING
      statsParsing = {
        val validCount = validMovies.size
        val validationErr = rawMovies.count(m => !DataValidator.isValid(m))
        val duplicates = rawMovies.count(DataValidator.isValid) - validCount

        MoviesStats(
          totalMoviesParsed = rawMovies.size + parsingErrors,
          totalMoviesValid = validCount,
          parsingErrors = parsingErrors,
          validationErrors = validationErr,
          duplicatesRemoved = duplicates
        )
      }

      // 4. REPORTING
      report = ReportGenerator.generateReport(validMovies, statsParsing)

      // Calcul du temps de traitement
      durationSec = (System.nanoTime() - startTime) / 1e9

      // 5. LOAD (Sauvegarde avec noms de fichiers distincts)
      _ <- ReportGenerator.writeJsonReport(report, s"output/results_$baseName.json")
      _ <- ReportGenerator.writeTextReport(report, s"output/report_$baseName.txt", durationSec)

    } yield (rawMovies.size, validMovies.size, parsingErrors, durationSec)

    // Affichage du résultat dans la console
    result match {
      case Right((total, valid, errors, time)) =>
        println(s"✅ Terminé : $total lus, $valid valides, $errors erreurs.")
        println(f"⏱️  Temps : $time%.3f secondes\n")
      case Left(err) =>
        println(s"❌ Erreur sur $filename : $err\n")
    }
  }
}