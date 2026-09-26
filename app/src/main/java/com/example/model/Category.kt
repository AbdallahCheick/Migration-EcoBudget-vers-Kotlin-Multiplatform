package com.example.model

import com.example.R

/**
 * Représente les catégories obligatoires pour la classification des dépenses dans EcoBudget.
 */
enum class Category(
    val labelResId: Int,
    val emoji: String
) {
    TRANSPORT(R.string.category_transport, "🚌"),
    ALIMENTATION(R.string.category_alimentation, "🍱"),
    LOISIRS(R.string.category_loisirs, "🎾"),
    LOGEMENT(R.string.category_logement, "🏠")
}
