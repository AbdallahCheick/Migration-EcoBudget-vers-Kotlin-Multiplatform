package com.example.data.repository

import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Interface du dépôt pour la gestion des transactions d'EcoBudget.
 * Définit le contrat d'accès asynchrone et réactif aux données.
 */
interface TransactionRepository {

    /**
     * Récupère le flux asynchrone et réactif de l'ensemble des transactions.
     *
     * @return [Flow] émettant la liste mise à jour des [Transaction].
     */
    fun getTransactions(): Flow<List<Transaction>>

    /**
     * Enregistre une nouvelle transaction au sein du dépôt.
     *
     * @param transaction La transaction immuable à ajouter.
     */
    suspend fun addTransaction(transaction: Transaction)

    /**
     * Met à jour une transaction existante au sein du dépôt.
     *
     * @param transaction La transaction modifiée.
     */
    suspend fun updateTransaction(transaction: Transaction)

    /**
     * Supprime une transaction existante par son identifiant unique.
     *
     * @param id Identifiant de la transaction à supprimer.
     */
    suspend fun deleteTransaction(id: String)
}
