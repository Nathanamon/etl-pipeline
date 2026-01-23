object Main extends App {

  println("🚀 Mini-ETL : Analyse de Films\n")
  // Chrono départ
  val startTime = System.nanoTime()

  // Choix du fichier (commence par dirty pour voir la magie opérer)
  val filename = "data/data_dirty.json"

  // =====================================================================================
  // LE PIPELINE ETL (Extract - Transform - Load)
  // =====================================================================================
  val result = for {
    // 1️⃣ EXTRACT : Chargement des données
    // DataLoader renvoie Either[String, (List[Movie], Int)]
    loadedData <- DataLoader.loadMovies(filename)
    (rawMovies, parsingErrors) = loadedData // Décomposition du tuple
    _ = println(s"✅ EXTRACT   : ${rawMovies.size} films lus (et $parsingErrors erreurs de parsing)")

    // 2️⃣ TRANSFORM : Validation & Nettoyage
    // DataValidator renvoie une List[Movie] simple
    validMovies = {
      val valid = DataValidator.filterValid(rawMovies)
      println(s"✅ TRANSFORM : ${valid.size} films valides conservés")
      valid
    }

    // Calcul des statistiques de nettoyage (pour le rapport)
    statsParsing = MoviesStats(
      totalMoviesParsed = rawMovies.size + parsingErrors,
      totalMoviesValid = validMovies.size,
      parsingErrors = parsingErrors,
      validationErrors = rawMovies.size - validMovies.size, // Approximation : ceux qui ont sauté à la validation
      duplicatesRemoved = rawMovies.filter(DataValidator.isValid).size - validMovies.size // Diff entre valides avec et sans doublons
    )

    // 3️⃣ REPORTING : Génération du rapport global en mémoire
    // ReportGenerator renvoie un GlobalReport
    report = ReportGenerator.generateReport(validMovies, statsParsing)
    _ = println(s"✅ REPORTING : Rapport statistique généré en mémoire")

    // 4️⃣ LOAD : Écriture sur disque (JSON & TXT)
    _ <- ReportGenerator.writeJsonReport(report, "output/results.json")
    _ = println(s"✅ LOAD      : JSON sauvegardé dans output/results.json")
    _ <- ReportGenerator.writeTextReport(report, "output/report.txt")
    _ = println(s"✅ LOAD      : Rapport texte sauvegardé dans output/report.txt")

  } yield report
/*


  // =====================================================================================
  // GESTION DU RÉSULTAT FINAL & AFFICHAGE CONSOLE
  // =====================================================================================
  // Chrono fin
  val endTime = System.nanoTime()
  val duration = (endTime - startTime) / 1e9

  result match {
    case Right(report) =>
      println("\n📊 APERÇU DES STATISTIQUES")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(f"Films analysés      : ${report.statistics.totalMoviesParsed}")
      println(f"Films valides       : ${report.statistics.totalMoviesValid}")
      println(f"Note moyenne globale: ${if(report.top10Rated.nonEmpty) report.top10Rated.map(_.rating).sum / report.top10Rated.size else 0.0}%.2f (sur le Top 10)")

      println("\n🏆 TOP 3 FILMS MIEUX NOTÉS")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      report.top10Rated.take(3).zipWithIndex.foreach { case (m, i) =>
        println(f"${i + 1}. ${m.title} (${m.year}) - ⭐ ${m.rating} (${m.votes} votes)")
      }

      println("\n🎭 TOP 3 GENRES POPULAIRES")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      report.moviesByGenre.toList
        .sortBy(-_._2) // Tri par nombre décroissant
        .take(3)
        .foreach { case (genre, count) =>
          println(f"- $genre%-12s : $count films")
        }
      println("\n💰 RENTABILITÉ")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(f"Films rentables : ${report.profitability.count}")
      println(f"ROI moyen       : ${report.profitability.averageRoi}%.2f x")
      println(f"Meilleur ROI    : ${report.profitability.bestRoi}%.2f x")

      println("\n⏱️ PERFORMANCE")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(f"Temps d'exécution : $duration%.3f secondes")
      println(f"Débit             : ${report.statistics.totalMoviesParsed / duration}%.0f films/sec")

      println("\n✅ Pipeline terminé avec succès !")

    case Left(error) =>
      println("\n❌ ÉCHEC DU PIPELINE")
      println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(s"Erreur rencontrée : $error")
      sys.exit(1)
  }
*/
}