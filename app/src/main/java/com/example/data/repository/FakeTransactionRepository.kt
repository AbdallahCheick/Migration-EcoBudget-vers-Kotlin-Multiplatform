package com.example.data.repository

import com.example.model.Category
import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.UUID

/**
 * Implémentation factice (Mock/In-Memory) de [TransactionRepository] pour simuler l'accès
 * aux données sans base de données réelle.
 *
 * Initialise un jeu de données diversifié de dépenses réparties sur plusieurs mois pour tester la navigation mensuelle.
 */
class FakeTransactionRepository : TransactionRepository {

    private val _transactionsFlow: MutableStateFlow<List<Transaction>>

    init {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) // 0-based

        fun getTimeForMonth(monthOffset: Int, day: Int, hour: Int): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, currentYear)
            cal.set(Calendar.MONTH, currentMonth + monthOffset)
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        val initialList = listOf(
            // Mois actuel (0)
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Supermarché Bio",
                amount = 45000.0,
                date = getTimeForMonth(0, 22, 14),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Session Tennis",
                amount = 12000.0,
                date = getTimeForMonth(0, 20, 10),
                category = Category.LOISIRS
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Ticket de Bus Express",
                amount = 2500.0,
                date = getTimeForMonth(0, 18, 8),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Loyer Mensuel",
                amount = 250000.0,
                date = getTimeForMonth(0, 5, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Boulangerie & Pâtisserie",
                amount = 4800.0,
                date = getTimeForMonth(0, 15, 16),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Recharge Vélo Électrique",
                amount = 3500.0,
                date = getTimeForMonth(0, 12, 11),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Facture Électricité",
                amount = 48000.0,
                date = getTimeForMonth(0, 8, 15),
                category = Category.LOGEMENT
            ),

            // Mois précédent (-1)
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Loyer Mois Précédent",
                amount = 250000.0,
                date = getTimeForMonth(-1, 5, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Courses du mois",
                amount = 65000.0,
                date = getTimeForMonth(-1, 10, 15),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Abonnement Transport",
                amount = 35000.0,
                date = getTimeForMonth(-1, 2, 8),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Sortie Restaurant",
                amount = 22000.0,
                date = getTimeForMonth(-1, 20, 20),
                category = Category.LOISIRS
            ),

            // Mois suivant (+1)
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Avance Loyer Prévue",
                amount = 250000.0,
                date = getTimeForMonth(1, 1, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = UUID.randomUUID().toString(),
                title = "Abonnement Salle de Sport",
                amount = 20000.0,
                date = getTimeForMonth(1, 3, 10),
                category = Category.LOISIRS
            )
        )

        _transactionsFlow = MutableStateFlow(initialList)
    }

    /**
     * Expose la liste des transactions sous forme de flux réactif asynchrone [Flow].
     */
    override fun getTransactions(): Flow<List<Transaction>> {
        return _transactionsFlow.asStateFlow()
    }

    /**
     * Enregistre une nouvelle dépense dans le flux réactif.
     */
    override suspend fun addTransaction(transaction: Transaction) {
        _transactionsFlow.update { currentList ->
            listOf(transaction) + currentList
        }
    }

    /**
     * Met à jour une dépense existante dans le flux réactif.
     */
    override suspend fun updateTransaction(transaction: Transaction) {
        _transactionsFlow.update { currentList ->
            currentList.map { if (it.id == transaction.id) transaction else it }
        }
    }

    /**
     * Supprime une dépense par son identifiant unique dans le flux réactif.
     */
    override suspend fun deleteTransaction(id: String) {
        _transactionsFlow.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }
}
