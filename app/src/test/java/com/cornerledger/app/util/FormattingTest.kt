package com.cornerledger.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class FormattingTest {

    @Test
    fun `formatAmount always shows magnitude with two decimals`() {
        assertEquals("£128.50", formatAmount(128.5))
        assertEquals("£16.50", formatAmount(-16.5))
        assertEquals("£0.00", formatAmount(0.0))
    }

    @Test
    fun `titleCase capitalizes each word`() {
        assertEquals("Priya Shah", titleCase("priya SHAH"))
        assertEquals("Tom Baxter", titleCase("tom baxter"))
    }

    @Test
    fun `normalizeName strips punctuation and lowercases`() {
        assertEquals("tombaxter", normalizeName("Tom Baxter!"))
        assertEquals("priyashah", normalizeName("Priya-Shah"))
    }

    @Test
    fun `initialsOf takes up to two words`() {
        assertEquals("PS", initialsOf("Priya Shah"))
        assertEquals("C", initialsOf("Cher"))
        assertEquals("JM", initialsOf("Jane Middle Doe"))
    }

    @Test
    fun `dateDisplay shows Today for same-day timestamps`() {
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 14, 14, 2, 0)
        }.timeInMillis
        val anHourAgo = now - 3_600_000L
        assertEquals("Today, 13:02", dateDisplay(anHourAgo, now))
    }

    @Test
    fun `dateDisplay shows day and month for earlier dates`() {
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 14, 14, 2, 0)
        }.timeInMillis
        val lastWeek = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 7, 9, 30, 0)
        }.timeInMillis
        val display = dateDisplay(lastWeek, now)
        assertTrue(display.startsWith("07 Sep"))
        assertTrue(display.endsWith("09:30"))
    }
}
