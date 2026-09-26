package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.YearMonth
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.VioletPrimary

/**
 * Navigateur mensuel élégant permettant de passer d'un mois à l'autre (◀ Mois Année ▶).
 *
 * @param currentMonth Mois actuellement sélectionné.
 * @param onPreviousMonth Callback pour passer au mois précédent.
 * @param onNextMonth Callback pour passer au mois suivant.
 * @param onCurrentMonthClick Callback pour revenir au mois courant au clic sur le libellé.
 */
@Composable
fun MonthNavigatorBar(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCurrentMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentCalendarMonth = currentMonth == YearMonth.current()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(width = 1.dp, color = DarkOutline, shape = RoundedCornerShape(18.dp))
            .testTag("month_navigator_bar"),
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bouton Mois Précédent
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .testTag("button_prev_month")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.nav_prev_month),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Mois et Année affichés
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onCurrentMonthClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("current_month_label")
            ) {
                Text(
                    text = "🗓️",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentMonth.displayLabel,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                if (isCurrentCalendarMonth) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VioletPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.current_month_badge),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            // Bouton Mois Suivant
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .testTag("button_next_month")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.nav_next_month),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
