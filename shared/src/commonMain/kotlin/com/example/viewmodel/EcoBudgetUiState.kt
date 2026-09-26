package com.example.viewmodel

import com.example.model.Category
import com.example.model.Transaction
import com.example.model.YearMonth
import androidx.compose.runtime.Immutable

/**
 * Classe de données immuable représentant l'état complet de l'interface pour EcoBudget.
 *
 * @property currentMonth Mois actuellement sélectionné dans le navigateur.
 * @property filteredTransactions Liste des transactions filtrées selon le mois actif et les catégories sélectionnées.
 * @property monthTransactions Liste des transactions du mois actif.
 * @property allTransactions Liste globale de l'ensemble des dépenses enregistrées.
 * @property selectedCategories Ensemble immuable des catégories sélectionnées (vide = toutes les catégories).
 * @property monthlyBudget Budget mensuel alloué pour le mois.
 * @property totalSpent Montant cumulé calculé des dépenses du mois actif.
 * @property categorySpent Montant cumulé des dépenses des catégories sélectionnées pour le mois actif.
 * @property remainingBudget Montant restant calculé du budget mensuel pour le mois actif.
 * @property isAddDialogOpen Indique si la boîte de dialogue d'enregistrement est visible.
 * @property editingTransaction Transaction en cours d'édition (ou null si mode création / fermé).
 */
@Immutable
data class EcoBudgetUiState(
    val currentMonth: YearMonth = YearMonth.current(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val monthTransactions: List<Transaction> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val monthlyBudget: Double = 500000.0,
    val totalSpent: Double = 0.0,
    val categorySpent: Double = 0.0,
    val remainingBudget: Double = 500000.0,
    val isAddDialogOpen: Boolean = false,
    val editingTransaction: Transaction? = null
) {
    /**
     * Indique si toutes les catégories sont actuellement sélectionnées / affichées.
     */
    val isAllCategoriesSelected: Boolean
        get() = selectedCategories.isEmpty() || selectedCategories.size == Category.entries.size

    /**
     * Ratio de consommation du budget mensuel (entre 0.0 et 1.0 ou supérieur).
     */
    val budgetUsageRatio: Float
        get() = if (monthlyBudget > 0) (totalSpent / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f

    /**
     * Pourcentage entier de consommation du budget mensuel.
     */
    val budgetUsagePercentage: Int
        get() = if (monthlyBudget > 0) ((totalSpent / monthlyBudget) * 100).toInt() else 0
}
