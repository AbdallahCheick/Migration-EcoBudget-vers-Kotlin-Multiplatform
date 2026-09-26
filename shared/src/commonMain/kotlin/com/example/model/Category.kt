package com.example.model

/**
 * Représente les catégories obligatoires pour la classification des dépenses dans EcoBudget.
 *
 * Entité purement métier : elle ne référence plus d'identifiant de ressource Android (`R.string.*`).
 * Le libellé localisé est résolu par la couche présentation via [com.example.viewmodel.labelRes].
 */
enum class Category(
    val emoji: String
) {
    TRANSPORT("🚌"),
    ALIMENTATION("🍱"),
    LOISIRS("🎾"),
    LOGEMENT("🏠")
}
