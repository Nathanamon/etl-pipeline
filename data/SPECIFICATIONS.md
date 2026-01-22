# 🎬 Dataset 3 : Movies - Films et séries

## 📋 Description

Base de données de films et séries type IMDb avec notes, acteurs et métadonnées.

## 📊 Structure des données

### Fichiers fournis

- **data_clean.json** : 100 films, données parfaites
- **data_dirty.json** : 500 films avec :
  - Champs manquants (director, revenue, budget)
  - Notes invalides (< 0 ou > 10)
  - Années impossibles (< 1895 ou > 2025)
  - Durées négatives ou nulles
  - Genres vides
  - Doublons (même ID)
- **data_large.json** : 15 000 entrées (films + séries)

## 🎯 Outputs attendus

### 1. Statistiques générales

**Fichier** : `results.json`

```json
{
  "statistics": {
    "total_movies_parsed": ...,
    "total_movies_valid": ...,
    "parsing_errors": ...,
    "duplicates_removed": ...
  },
  "top_10_rated": [
    {
      "title": "...",
      "year": ...,
      "rating": ...,
      "votes": ...
    }
  ],
  "top_10_by_votes": [...],
  "highest_grossing": [...],
  "most_expensive": [...],
  "movies_by_decade": {
    "1990s": ...,
    "2000s": ...,
    "2010s": ...,
    "2020s": ...
  },
  "movies_by_genre": {
    "Action": ...,
    "Drama": ...,
    "Comedy": ...,
    "Thriller": ...,
    "Sci-Fi": ...
  },
  "average_rating_by_genre": {
    "Drama": ...,
    "Thriller": ...,
    "Action": ...,
    "Comedy": ...,
    "Sci-Fi": ...
  },
  "average_runtime_by_genre": {
    "Drama": ...,
    "Action": ...,
    "Comedy": ...,
    "Thriller": ...,
    "Sci-Fi": ...
  },
  "most_prolific_directors": [
    {"director": "...", "count": ...}
  ],
  "most_frequent_actors": [
    {"actor": "...", "count": ...}
  ],
  "profitable_movies": {
    "count": ...,
    "average_roi": ...
  }
}
```

### 2. Rapport texte

**Fichier** : `report.txt`

```
===============================================
     RAPPORT D'ANALYSE - FILMS & SÉRIES
===============================================

📊 STATISTIQUES DE PARSING
---------------------------
- Entrées totales lues      : ...
- Entrées valides           : ...
- Erreurs de parsing        : ...
- Doublons supprimés        : ...

⭐ TOP 10 - MEILLEURS FILMS
----------------------------
1. ...                      : .../10 (... votes)
2. ...                      : .../10 (... votes)
...

📊 TOP 10 - PLUS VOTÉS
-----------------------
1. ...                      : ... votes
2. ...                      : ... votes
...

💰 TOP 10 - BOX-OFFICE
-----------------------
1. ...                      : ... M$
2. ...                      : ... M$
...

💸 TOP 10 - BUDGETS
-------------------
1. ...                      : ... M$
2. ...                      : ... M$
...

📅 RÉPARTITION PAR DÉCENNIE
----------------------------
- 1990s                     : ... films
- 2000s                     : ... films
- 2010s                     : ... films
- 2020s                     : ... films

🎭 RÉPARTITION PAR GENRE
-------------------------
- Drama                     : ... films
- Action                    : ... films
- Comedy                    : ... films
- Thriller                  : ... films
- Sci-Fi                    : ... films

📈 MOYENNES PAR GENRE
----------------------
NOTE MOYENNE :
- Drama                     : .../10
- Thriller                  : .../10
- Sci-Fi                    : .../10
- Action                    : .../10
- Comedy                    : .../10

DURÉE MOYENNE :
- Drama                     : ... minutes
- Sci-Fi                    : ... minutes
- Action                    : ... minutes
- Thriller                  : ... minutes
- Comedy                    : ... minutes

🎬 TOP 5 - RÉALISATEURS
------------------------
1. ...                      : ... films
2. ...                      : ... films
...

🎭 TOP 5 - ACTEURS
-------------------
1. ...                      : ... films
2. ...                      : ... films
...

💵 RENTABILITÉ
--------------
- Films rentables           : ... films
- ROI moyen                 : ...x
- Meilleur ROI              : ...x

⏱️  PERFORMANCE
---------------
- Temps de traitement       : ... secondes
- Entrées/seconde           : ...

===============================================
```

## 🔧 Transformations à implémenter

### Obligatoires

1. **Parsing et validation**
   - Parser les 3 fichiers
   - Valider : year entre 1895 et 2025, rating entre 0 et 10, runtime > 0
   - Filtrer les genres vides
   - Supprimer les doublons (par ID)

2. **Top 10**
   - Top 10 films les mieux notés (minimum 10 000 votes pour éviter les biais)
   - Top 10 films les plus votés
   - Top 10 box-office (filtrer ceux qui ont un revenue)
   - Top 10 budgets (filtrer ceux qui ont un budget)

3. **Agrégations**
   - Compter films par décennie
   - Compter films par genre
   - Note moyenne par genre
   - Durée moyenne par genre

4. **Personnalités**
   - Top 5 réalisateurs les plus prolifiques
   - Top 5 acteurs qui apparaissent le plus

### Bonus (optionnel)

5. **Rentabilité**
   - Calculer ROI = revenue / budget
   - Top 10 films les plus rentables
   - Nombre de films rentables (revenue > budget)

6. **Analyse temporelle**
   - Évolution de la note moyenne par décennie
   - Évolution de la durée moyenne par décennie

7. **Genres multiples**
   - Films qui combinent 3+ genres
   - Combinaisons de genres les plus fréquentes

## 💡 Conseil - Mesure de performance

```scala
val start = System.currentTimeMillis()
// ... traitement
val duration = (System.currentTimeMillis() - start) / 1000.0
println(f"Traitement effectué en $duration%.3f secondes")
```

## ✅ Critères de réussite

- ✅ Les 3 fichiers sont parsés sans erreur fatale
- ✅ Les films avec données invalides sont filtrés
- ✅ Le filtrage par nombre de votes est appliqué pour le top rated
- ✅ Les 10 statistiques obligatoires sont présentes
- ✅ `results.json` est valide et bien formaté
- ✅ `report.txt` est lisible et structuré
- ✅ Le traitement de `data_large.json` prend < 10 secondes



