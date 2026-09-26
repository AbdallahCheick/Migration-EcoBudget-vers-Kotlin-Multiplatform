package com.example.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YearMonthTest {

    @Test
    fun previousFromJanuaryWrapsToDecemberOfPreviousYear() {
        assertEquals(YearMonth(2025, 11), YearMonth(2026, 0).previous())
    }

    @Test
    fun nextFromDecemberWrapsToJanuaryOfNextYear() {
        assertEquals(YearMonth(2027, 0), YearMonth(2026, 11).next())
    }

    @Test
    fun plusMonthsHandlesYearBoundariesInBothDirections() {
        assertEquals(YearMonth(2025, 7), YearMonth(2026, 8).plusMonths(-13))
        assertEquals(YearMonth(2027, 10), YearMonth(2026, 8).plusMonths(14))
        assertEquals(YearMonth(2026, 8), YearMonth(2026, 8).plusMonths(0))
    }

    @Test
    fun displayLabelIsCapitalizedFrenchMonthAndYear() {
        assertEquals("Août 2026", YearMonth(2026, 7).displayLabel)
        assertEquals("Janvier 2025", YearMonth(2025, 0).displayLabel)
        assertEquals("Décembre 2024", YearMonth(2024, 11).displayLabel)
    }

    @Test
    fun timestampAtBelongsToItsMonthOnly() {
        val month = YearMonth(2026, 1)
        val timestamp = month.timestampAt(day = 15, hour = 12)

        assertTrue(month.containsTimestamp(timestamp))
        assertFalse(month.next().containsTimestamp(timestamp))
        assertFalse(month.previous().containsTimestamp(timestamp))
        assertEquals(month, YearMonth.fromTimestamp(timestamp))
    }
}
