package com.example.viewmodel

import com.example.data.repository.FakeTransactionRepository
import com.example.model.Category
import com.example.model.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests de non-régression du ViewModel partagé, exécutés sur le jeu de données de
 * [FakeTransactionRepository] (valeurs identiques à celles de l'application Android d'origine).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EcoBudgetViewModelTest {

    private lateinit var viewModel: EcoBudgetViewModel

    @BeforeTest
    fun setUp() {
        // viewModelScope s'exécute sur Dispatchers.Main : on le remplace par un dispatcher de test.
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = EcoBudgetViewModel(FakeTransactionRepository())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Abonne un collecteur (uiState est partagé en WhileSubscribed) et retourne l'état courant. */
    private fun TestScope.state(): EcoBudgetUiState {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel.uiState.value
    }

    @Test
    fun dashboardComputesCurrentMonthTotals() = runTest {
        val state = state()

        assertEquals(YearMonth.current(), state.currentMonth)
        assertEquals(7, state.monthTransactions.size)
        assertEquals(13, state.allTransactions.size)
        assertEquals(365_800.0, state.totalSpent)
        assertEquals(134_200.0, state.remainingBudget)
        assertEquals(73, state.budgetUsagePercentage)
        assertTrue(state.isAllCategoriesSelected)
        assertEquals(state.totalSpent, state.categorySpent)
    }

    @Test
    fun monthNavigationRecomputesTotals() = runTest {
        state()

        viewModel.previousMonth()
        assertEquals(YearMonth.current().previous(), viewModel.uiState.value.currentMonth)
        assertEquals(372_000.0, viewModel.uiState.value.totalSpent)
        assertEquals(4, viewModel.uiState.value.monthTransactions.size)

        viewModel.nextMonth()
        viewModel.nextMonth()
        assertEquals(270_000.0, viewModel.uiState.value.totalSpent)
        assertEquals(2, viewModel.uiState.value.monthTransactions.size)

        viewModel.nextMonth()
        assertEquals(0.0, viewModel.uiState.value.totalSpent)
        assertEquals(500_000.0, viewModel.uiState.value.remainingBudget)

        viewModel.goToCurrentMonth()
        assertEquals(365_800.0, viewModel.uiState.value.totalSpent)
    }

    @Test
    fun categoryFiltersSupportMultiSelectionAndReset() = runTest {
        state()

        viewModel.toggleCategory(Category.ALIMENTATION)
        with(viewModel.uiState.value) {
            assertFalse(isAllCategoriesSelected)
            assertEquals(2, filteredTransactions.size)
            assertEquals(49_800.0, categorySpent)
            // Le total du mois (carte Hero) ne dépend pas du filtre.
            assertEquals(365_800.0, totalSpent)
        }

        viewModel.toggleCategory(Category.TRANSPORT)
        assertEquals(4, viewModel.uiState.value.filteredTransactions.size)
        assertEquals(55_800.0, viewModel.uiState.value.categorySpent)

        viewModel.toggleCategory(Category.ALIMENTATION)
        assertEquals(setOf(Category.TRANSPORT), viewModel.uiState.value.selectedCategories)
        assertEquals(6_000.0, viewModel.uiState.value.categorySpent)

        viewModel.clearCategoryFilter()
        assertTrue(viewModel.uiState.value.isAllCategoriesSelected)
        assertEquals(7, viewModel.uiState.value.filteredTransactions.size)
    }

    @Test
    fun selectingEveryCategoryIsEquivalentToAll() = runTest {
        state()
        Category.entries.forEach { viewModel.toggleCategory(it) }

        assertTrue(viewModel.uiState.value.isAllCategoriesSelected)
        assertEquals(365_800.0, viewModel.uiState.value.categorySpent)
    }

    @Test
    fun addEditAndDeleteTransaction() = runTest {
        state()

        viewModel.openAddDialog()
        assertTrue(viewModel.uiState.value.isAddDialogOpen)
        viewModel.saveTransaction("  Marché  ", 10_000.0, Category.ALIMENTATION)

        val added = viewModel.uiState.value.monthTransactions.first { it.title == "Marché" }
        assertFalse(viewModel.uiState.value.isAddDialogOpen)
        assertEquals(375_800.0, viewModel.uiState.value.totalSpent)

        viewModel.openEditDialog(added)
        assertEquals(added, viewModel.uiState.value.editingTransaction)
        viewModel.saveTransaction("Marché bio", 15_000.0, Category.LOISIRS)
        val edited = viewModel.uiState.value.monthTransactions.first { it.id == added.id }
        assertEquals("Marché bio", edited.title)
        assertEquals(Category.LOISIRS, edited.category)
        assertEquals(380_800.0, viewModel.uiState.value.totalSpent)
        assertNull(viewModel.uiState.value.editingTransaction)

        viewModel.deleteTransaction(added.id)
        assertEquals(365_800.0, viewModel.uiState.value.totalSpent)
    }

    @Test
    fun transactionAddedWhileBrowsingAnotherMonthIsDatedInThatMonth() = runTest {
        state()
        viewModel.previousMonth()
        viewModel.saveTransaction("Rattrapage", 1_000.0, Category.TRANSPORT)

        val target = YearMonth.current().previous()
        val saved = viewModel.uiState.value.allTransactions.first { it.title == "Rattrapage" }
        assertTrue(target.containsTimestamp(saved.date))
        assertEquals(373_000.0, viewModel.uiState.value.totalSpent)
    }

    @Test
    fun invalidInputIsIgnored() = runTest {
        state()
        viewModel.saveTransaction("   ", 5_000.0, Category.LOISIRS)
        viewModel.saveTransaction("Valide", 0.0, Category.LOISIRS)

        assertEquals(13, viewModel.uiState.value.allTransactions.size)
    }

    @Test
    fun repositoryFlowEmitsInitialDataset() = runTest {
        val transactions = FakeTransactionRepository().getTransactions().first()
        assertEquals(13, transactions.size)
        assertEquals(13, transactions.map { it.id }.toSet().size)
    }
}
