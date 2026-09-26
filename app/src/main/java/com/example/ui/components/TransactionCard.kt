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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Transaction
import com.example.ui.theme.DarkCardBadge
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composant atomique réutilisable pour afficher chaque dépense.
 * Cliquer sur la carte déclenche [onClick] pour ouvrir le formulaire pré-rempli d'édition.
 *
 * @param transaction La transaction immuable à afficher.
 * @param onClick Callback déclenché au clic sur la carte (modification).
 * @param onDelete Callback déclenché pour supprimer la transaction.
 * @param modifier Modificateur Compose optionnel.
 */
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = stringResource(transaction.category.labelResId)
    val todayText = stringResource(R.string.date_today)
    val yesterdayText = stringResource(R.string.date_yesterday)
    val currencyFcfa = stringResource(R.string.currency_fcfa)

    val formattedDate = remember(transaction.date, todayText, yesterdayText) {
        formatRelativeDate(transaction.date, todayText, yesterdayText)
    }

    val formattedAmount = remember(transaction.amount, currencyFcfa) {
        val nf = NumberFormat.getNumberInstance(Locale.FRENCH)
        nf.maximumFractionDigits = 0
        "${nf.format(transaction.amount)} $currencyFcfa"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = DarkOutline, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("transaction_card_${transaction.id}"),
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge d'icône/emoji avec fond violet sombre contrasté
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCardBadge),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.category.emoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Intitulé en Blanc pur et métadonnées en Gris clair hautement lisible
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.content_desc_edit),
                        tint = DarkTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$categoryName • $formattedDate",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = DarkTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Montant en Blanc Pur et bouton de suppression
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "-$formattedAmount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("delete_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.content_desc_delete_format, transaction.title),
                        tint = DarkTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Formate un timestamp en libellé lisible relatif (Aujourd'hui, Hier, ou date calendaire).
 */
fun formatRelativeDate(
    timestamp: Long,
    todayString: String = "Aujourd'hui",
    yesterdayString: String = "Hier"
): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneDay = 86400000L

    return when {
        diff < 3600000L * 12 && diff >= 0 -> todayString
        diff < oneDay * 2 && diff >= 0 -> yesterdayString
        else -> {
            val sdf = SimpleDateFormat("d MMM", Locale.FRANCE)
            sdf.format(Date(timestamp))
        }
    }
}
