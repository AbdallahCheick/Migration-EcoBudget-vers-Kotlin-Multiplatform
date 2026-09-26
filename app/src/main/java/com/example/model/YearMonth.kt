package com.example.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Modèle immuable représentant un mois spécifique pour la navigation budgétaire.
 *
 * @property year Année (ex: 2026).
 * @property month Index du mois de 0 (Janvier) à 11 (Décembre).
 */
data class YearMonth(
    val year: Int,
    val month: Int
) {
    /**
     * Libellé formaté en français (ex: "Août 2026").
     */
    val displayLabel: String
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val sdf = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
            val formatted = sdf.format(Date(cal.timeInMillis))
            return formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.FRENCH) else it.toString() }
        }

    /**
     * Retourne le YearMonth précédent.
     */
    fun previous(): YearMonth {
        return if (month == 0) {
            YearMonth(year - 1, 11)
        } else {
            YearMonth(year, month - 1)
        }
    }

    /**
     * Retourne le YearMonth suivant.
     */
    fun next(): YearMonth {
        return if (month == 11) {
            YearMonth(year + 1, 0)
        } else {
            YearMonth(year, month + 1)
        }
    }

    /**
     * Vérifie si un timestamp millisecondes appartient à ce mois précis.
     */
    fun containsTimestamp(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
    }

    companion object {
        /**
         * Crée le YearMonth courant.
         */
        fun current(): YearMonth {
            val cal = Calendar.getInstance()
            return YearMonth(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH)
            )
        }

        /**
         * Crée le YearMonth correspondant à un timestamp.
         */
        fun fromTimestamp(timestamp: Long): YearMonth {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            return YearMonth(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH)
            )
        }
    }
}
