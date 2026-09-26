package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Category
import com.example.model.Transaction
import com.example.ui.theme.DarkDialogBackground
import com.example.ui.theme.DarkDialogChipInactive
import com.example.ui.theme.DarkDialogFieldBackground
import com.example.ui.theme.DarkDialogOutline
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.ui.theme.VioletPrimaryLight

/**
 * Boîte de dialogue permettant l'enregistrement ou la modification d'une dépense.
 *
 * @param initialTransaction Transaction à modifier si en mode édition, ou null si nouvelle dépense.
 * @param onDismissRequest Déclenché lors de l'annulation ou fermeture.
 * @param onConfirm Déclenché avec les données saisies (titre, montant, catégorie).
 */
@Composable
fun AddTransactionDialog(
    initialTransaction: Transaction? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (title: String, amount: Double, category: Category) -> Unit
) {
    val isEditMode = initialTransaction != null
    var title by remember(initialTransaction) { mutableStateOf(initialTransaction?.title ?: "") }
    var amountText by remember(initialTransaction) {
        mutableStateOf(
            if (initialTransaction != null) {
                if (initialTransaction.amount % 1.0 == 0.0) {
                    initialTransaction.amount.toLong().toString()
                } else {
                    initialTransaction.amount.toString()
                }
            } else ""
        )
    }
    var selectedCategory by remember(initialTransaction) {
        mutableStateOf(initialTransaction?.category ?: Category.ALIMENTATION)
    }
    var isError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val handleDismiss = {
        keyboardController?.hide()
        focusManager.clearFocus()
        onDismissRequest()
    }

    AlertDialog(
        onDismissRequest = handleDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = DarkDialogBackground,
        modifier = Modifier.border(
            width = 1.5.dp,
            color = DarkDialogOutline,
            shape = RoundedCornerShape(28.dp)
        ),
        title = {
            Text(
                text = if (isEditMode) {
                    stringResource(R.string.dialog_title_edit)
                } else {
                    stringResource(R.string.dialog_title_new)
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                modifier = Modifier.testTag("dialog_title")
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Titre
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (isError) isError = false
                    },
                    label = { Text(stringResource(R.string.label_transaction_title)) },
                    placeholder = { Text(stringResource(R.string.placeholder_transaction_title), color = DarkTextSecondary) },
                    singleLine = true,
                    isError = isError && title.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = VioletPrimaryLight,
                        unfocusedLabelColor = DarkTextSecondary,
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedContainerColor = DarkDialogFieldBackground,
                        unfocusedContainerColor = DarkDialogFieldBackground,
                        cursorColor = VioletPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_transaction_title")
                )

                // Montant en FCFA
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*$"))) {
                            amountText = it
                            if (isError) isError = false
                        }
                    },
                    label = { Text(stringResource(R.string.label_transaction_amount)) },
                    placeholder = { Text(stringResource(R.string.placeholder_transaction_amount), color = DarkTextSecondary) },
                    singleLine = true,
                    isError = isError && amountText.isBlank(),
                    trailingIcon = {
                        Text(
                            text = stringResource(R.string.currency_fcfa),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = VioletPrimaryLight,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = VioletPrimaryLight,
                        unfocusedLabelColor = DarkTextSecondary,
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedContainerColor = DarkDialogFieldBackground,
                        unfocusedContainerColor = DarkDialogFieldBackground,
                        cursorColor = VioletPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_transaction_amount")
                )

                // Sélecteur de catégorie
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.label_category),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Category.entries.forEach { category ->
                            val isSelected = category == selectedCategory
                            val label = stringResource(category.labelResId)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) VioletPrimary else DarkDialogChipInactive
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) VioletPrimary else DarkOutline,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        keyboardController?.hide()
                                        selectedCategory = category
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = category.emoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else Color(0xFFE2E2EC),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amountText.toDoubleOrNull()
                    if (title.isNotBlank() && parsedAmount != null && parsedAmount > 0.0) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onConfirm(title, parsedAmount, selectedCategory)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VioletPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("button_confirm_add_transaction")
            ) {
                Text(
                    text = if (isEditMode) {
                        stringResource(R.string.btn_save)
                    } else {
                        stringResource(R.string.btn_add)
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = handleDismiss,
                modifier = Modifier.testTag("button_cancel_add_transaction")
            ) {
                Text(
                    text = stringResource(R.string.btn_cancel),
                    color = DarkTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
