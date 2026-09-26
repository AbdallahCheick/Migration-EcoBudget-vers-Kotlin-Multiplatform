package com.example.model

/**
 * Modèle de données immuable représentant une transaction / dépense au sein d'EcoBudget.
 *
 * @property id Identifiant unique de la transaction.
 * @property title Intitulé ou description de la dépense.
 * @property amount Montant financier de la transaction.
 * @property date Horodatage (timestamp en millisecondes) de l'enregistrement de la dépense.
 * @property category Catégorie associée à la dépense (TRANSPORT, ALIMENTATION, LOISIRS, LOGEMENT).
 */
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val category: Category
)
