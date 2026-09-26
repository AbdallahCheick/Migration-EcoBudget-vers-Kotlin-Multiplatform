package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.Category
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.MonthNavigatorBar
import com.example.ui.components.TransactionCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.VioletCardHero
import com.example.ui.theme.VioletPrimary
import com.example.ui.theme.VioletPrimaryLight
import com.example.viewmodel.EcoBudgetViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Écran principal d'EcoBudget :
 * - Navigation mensuelle réactive.
 * - Suivi précis du budget restant en FCFA pour le mois actif.
 * - Sélection d'une ou plusieurs catégories simultanément.
 * - Modification d'une dépense au clic direct sur sa carte.
 * - Suppression d'une dépense.
 * - Internationalisation complète via strings.xml.
 */
@Composable
fun EcoBudgetScreen(
    viewModel: EcoBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val formatFcfa = remember {
        val nf = NumberFormat.getNumberInstance(Locale.FRENCH)
        nf.maximumFractionDigits = 0
        nf
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = VioletPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp, end = 8.dp)
                    .testTag("fab_add_transaction")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_desc_add_transaction),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // En-tête de l'application
            EcoBudgetCleanHeader()

            // Contenu défilant principal
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 2.dp, bottom = 88.dp)
            ) {
                // Section 1 : Navigateur mensuel
                item(key = "month_navigator_section") {
                    MonthNavigatorBar(
                        currentMonth = uiState.currentMonth,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onCurrentMonthClick = { viewModel.goToCurrentMonth() }
                    )
                }

                // Section 2 : Carte Hero mettant en avant le Budget Restant
                item(key = "budget_overview_hero") {
                    EcoBudgetOverviewCard(
                        remainingBudget = uiState.remainingBudget,
                        totalSpent = uiState.totalSpent,
                        usageRatio = uiState.budgetUsageRatio,
                        usagePercentage = uiState.budgetUsagePercentage,
                        currentMonthLabel = uiState.currentMonth.displayLabel
                    )
                }

                // Section 3 : Puces de filtrage multi-catégories
                item(key = "category_multi_filter_row") {
                    CategoryMultiFilterLazyRow(
                        selectedCategories = uiState.selectedCategories,
                        isAllSelected = uiState.isAllCategoriesSelected,
                        onSelectAll = { viewModel.clearCategoryFilter() },
                        onToggleCategory = { viewModel.toggleCategory(it) }
                    )
                }

                // Section 4 : Bandeau récapitulatif des Catégories sélectionnées
                item(key = "category_spent_summary") {
                    val summaryTitle = when {
                        uiState.isAllCategoriesSelected -> stringResource(R.string.all_categories_summary)
                        uiState.selectedCategories.size == 1 -> {
                            val cat = uiState.selectedCategories.first()
                            "${cat.emoji} ${stringResource(cat.labelResId)}"
                        }
                        else -> {
                            val emojis = uiState.selectedCategories.joinToString(" ") { it.emoji }
                            stringResource(
                                R.string.multiple_categories_selected_format,
                                emojis,
                                uiState.selectedCategories.size
                            )
                        }
                    }

                    val currencyFcfa = stringResource(R.string.currency_fcfa)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(width = 1.dp, color = DarkOutline, shape = RoundedCornerShape(16.dp))
                            .testTag("selected_category_summary"),
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = summaryTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(
                                        R.string.expense_count_month_format,
                                        uiState.filteredTransactions.size,
                                        uiState.currentMonth.displayLabel
                                    ),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = DarkTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Total des catégories sélectionnées
                            Text(
                                text = "${formatFcfa.format(uiState.categorySpent)} $currencyFcfa",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = VioletPrimaryLight,
                                modifier = Modifier.testTag("category_spent_amount")
                            )
                        }
                    }
                }

                // Section 5 : Titre de la liste des transactions
                item(key = "transactions_section_title") {
                    Text(
                        text = stringResource(
                            R.string.transactions_detail_title_format,
                            uiState.currentMonth.displayLabel
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Section 6 : Liste verticale avec TransactionCard (cliquable pour édition)
                if (uiState.filteredTransactions.isEmpty()) {
                    item(key = "empty_transactions_state") {
                        EmptyTransactionsView(
                            isAllSelected = uiState.isAllCategoriesSelected,
                            monthLabel = uiState.currentMonth.displayLabel
                        )
                    }
                } else {
                    items(
                        items = uiState.filteredTransactions,
                        key = { it.id }
                    ) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onClick = { viewModel.openEditDialog(transaction) },
                            onDelete = { viewModel.deleteTransaction(transaction.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialogue d'enregistrement / modification d'une dépense
    if (uiState.isAddDialogOpen) {
        AddTransactionDialog(
            initialTransaction = uiState.editingTransaction,
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirm = { title, amount, category ->
                viewModel.saveTransaction(title, amount, category)
            }
        )
    }
}

/**
 * En-tête supérieur épuré.
 */
@Composable
private fun EcoBudgetCleanHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = VioletPrimaryLight
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = Color.White
        )
    }
}

/**
 * Carte Hero Violette mettant en avant le BUDGET RESTANT (chiffre géant),
 * la jauge d'utilisation et les dépenses totales.
 */
@Composable
private fun EcoBudgetOverviewCard(
    remainingBudget: Double,
    totalSpent: Double,
    usageRatio: Float,
    usagePercentage: Int,
    currentMonthLabel: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = usageRatio,
        animationSpec = spring(stiffness = 300f),
        label = "budget_progress"
    )

    val formatFcfa = remember {
        val nf = NumberFormat.getNumberInstance(Locale.FRENCH)
        nf.maximumFractionDigits = 0
        nf
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(width = 1.dp, color = Color(0xFF7C3AED).copy(alpha = 0.5f), shape = RoundedCornerShape(24.dp))
            .testTag("budget_overview_card"),
        color = VioletCardHero,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Libellé principal avec le mois
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.budget_remaining_title),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFFEDE9FE)
                )

                Text(
                    text = currentMonthLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFFDDD6FE)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chiffre géant : Budget Restant en FCFA
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.testTag("remaining_budget_text")
            ) {
                Text(
                    text = formatFcfa.format(remainingBudget),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.currency_fcfa),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEDE9FE),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barre de progression
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B1578))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Affichage secondaire du Total des Dépenses et du pourcentage consommé
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total_spent_format, formatFcfa.format(totalSpent)),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFFEDE9FE),
                    modifier = Modifier.testTag("total_spent_text")
                )

                Text(
                    text = stringResource(R.string.budget_usage_percent_format, usagePercentage),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Rangée horizontale LazyRow permettant la sélection de UNE ou PLUSIEURS catégories.
 */
@Composable
private fun CategoryMultiFilterLazyRow(
    selectedCategories: Set<Category>,
    isAllSelected: Boolean,
    onSelectAll: () -> Unit,
    onToggleCategory: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        // Puce "Tous"
        item(key = "filter_chip_all") {
            FilterCategoryChip(
                label = stringResource(R.string.filter_all),
                emoji = "✨",
                isSelected = isAllSelected,
                isMultiSelect = false,
                onClick = onSelectAll,
                testTag = "filter_chip_all"
            )
        }

        // Puces pour chaque catégorie (multi-sélectionnable)
        items(
            items = Category.entries.toTypedArray(),
            key = { "filter_chip_${it.name}" }
        ) { category ->
            val label = stringResource(category.labelResId)
            val isSelected = !isAllSelected && selectedCategories.contains(category)

            FilterCategoryChip(
                label = label,
                emoji = category.emoji,
                isSelected = isSelected,
                isMultiSelect = true,
                onClick = { onToggleCategory(category) },
                testTag = "filter_chip_${category.name.lowercase()}"
            )
        }
    }
}

/**
 * Puce unitaire interactive pour le filtrage multi-catégories avec indicateur de coche visuel.
 */
@Composable
private fun FilterCategoryChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    isMultiSelect: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val backgroundColor = if (isSelected) VioletPrimary else DarkSurfaceVariant
    val contentColor = if (isSelected) Color.White else Color(0xFFE2E2EC)
    val borderColor = if (isSelected) VioletPrimaryLight else DarkOutline

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 15.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )

            // Coche discrète lorsque la catégorie est sélectionnée en multi-sélection
            if (isSelected && isMultiSelect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.content_desc_selected),
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Vue affichée lorsqu'aucune transaction ne correspond au filtre actif pour le mois donné.
 */
@Composable
private fun EmptyTransactionsView(
    isAllSelected: Boolean,
    monthLabel: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🌿", fontSize = 44.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (isAllSelected) {
                    stringResource(R.string.empty_expenses_month_format, monthLabel)
                } else {
                    stringResource(R.string.empty_expenses_filtered_format, monthLabel)
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.empty_expenses_hint),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = DarkTextSecondary
            )
        }
    }
}
