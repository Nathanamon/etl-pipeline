object Main extends App {

  // 📂 Configuration : Choisis le fichier à traiter
  // Commence par "data_clean.json" (tout devrait être vert)
  // Puis passe à "data_dirty.json" pour voir le filtre en action
  val filename = "data/data_large.json"

  println(s"🚀 DÉMARRAGE DU PIPELINE ETL SUR : $filename")
  println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

  // ---------------------------------------------------------
  // ÉTAPE 1 : EXTRACTION (Parsing JSON)
  // ---------------------------------------------------------
  println("\n[1/3] Chargement des données brutes...")

  DataLoader.loadMovies(filename) match {
    case Left(criticalError) =>
      println("❌ ERREUR CRITIQUE : Impossible de lire le fichier.")
      println(s"   Raison : $criticalError")

    case Right((parsedMovies, parsingErrors)) =>
      println(s"   ✅ Fichier lu avec succès.")
      println(s"   📊 Films structurellement valides : ${parsedMovies.size}")
      println(s"   🗑️  Echecs de parsing (JSON invalide) : $parsingErrors")

      // ---------------------------------------------------------
      // ÉTAPE 2 : TRANSFORMATION & VALIDATION (Règles métier)
      // ---------------------------------------------------------
      println("\n[2/3] Application des règles métier...")

      // On passe la liste "brute" à ton validateur
      val finalMovies = DataValidator.filterValid(parsedMovies)

      // Calcul des statistiques de validation
      val rejectedCount = parsedMovies.size - finalMovies.size

      println(s"   ✅ Validation terminée.")
      println(s"   🛡️  Films rejetés (règles métier / doublons) : $rejectedCount")
      println(s"   💎 FILMS FINAUX CONSERVÉS : ${finalMovies.size}")

      // ---------------------------------------------------------
      // ÉTAPE 3 : APERÇU (Pour vérifier)
      // ---------------------------------------------------------
      if (finalMovies.nonEmpty) {
        println("\n[3/3] Aperçu des résultats (Top 3) :")
        println("------------------------------------")
        finalMovies.take(3).foreach { movie =>
          println(s"🎬 [${movie.year}] ${movie.title} (Note: ${movie.rating})")
        }
      } else {
        println("\n⚠️  ATTENTION : Aucun film n'a survécu au filtrage !")
      }

      // Petit récapitulatif total
      println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
      println(s"Total lignes lues (estimé) : ${parsedMovies.size + parsingErrors}")
      println(s"Taux de qualité            : ${if (parsedMovies.size + parsingErrors > 0) (finalMovies.size.toDouble / (parsedMovies.size + parsingErrors) * 100).toInt else 0}%")
  }
}