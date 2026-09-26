package com.example.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
        get() = "${FRENCH_MONTH_NAMES[month]} $year"

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
     * Retourne le YearMonth décalé de [offset] mois (négatif pour reculer), en gérant le changement d'année.
     */
    fun plusMonths(offset: Int): YearMonth {
        val totalMonths = year * 12 + month + offset
        return YearMonth(totalMonths.floorDiv(12), totalMonths.mod(12))
    }

    /**
     * Vérifie si un timestamp millisecondes appartient à ce mois précis.
     */
    fun containsTimestamp(timestamp: Long): Boolean {
        return fromTimestamp(timestamp) == this
    }

    /**
     * Horodatage (millisecondes, fuseau local) du jour [day] de ce mois à [hour] heures pile.
     */
    fun timestampAt(day: Int, hour: Int): Long {
        return LocalDateTime(year, month + 1, day, hour, 0)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    }

    companion object {
        /**
         * Crée le YearMonth courant.
         */
        fun current(): YearMonth {
            return fromTimestamp(Clock.System.now().toEpochMilliseconds())
        }

        /**
         * Crée le YearMonth correspondant à un timestamp.
         */
        fun fromTimestamp(timestamp: Long): YearMonth {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(
                year = dateTime.year,
                month = dateTime.monthNumber - 1
            )
        }

        /** Noms des mois en français, indexés de 0 (Janvier) à 11 (Décembre). */
        private val FRENCH_MONTH_NAMES = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
    }
}
