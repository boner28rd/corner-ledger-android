package com.cornerledger.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class EntryLogicTest {

    @Test
    fun `charge always increases balance`() {
        assertEquals(50.0, computeDelta(EntryType.CHARGE, 50.0, EntrySign.PLUS), 0.0)
        assertEquals(50.0, computeDelta(EntryType.CHARGE, 50.0, EntrySign.MINUS), 0.0)
    }

    @Test
    fun `payment always decreases balance`() {
        assertEquals(-20.0, computeDelta(EntryType.PAYMENT, 20.0, EntrySign.PLUS), 0.0)
        assertEquals(-20.0, computeDelta(EntryType.PAYMENT, 20.0, EntrySign.MINUS), 0.0)
    }

    @Test
    fun `adjustment sign determines direction`() {
        assertEquals(10.0, computeDelta(EntryType.ADJUSTMENT, 10.0, EntrySign.PLUS), 0.0)
        assertEquals(-10.0, computeDelta(EntryType.ADJUSTMENT, 10.0, EntrySign.MINUS), 0.0)
    }

    @Test
    fun `entryLabel matches prototype copy`() {
        assertEquals("Charge", entryLabel(EntryType.CHARGE, EntrySign.PLUS))
        assertEquals("Payment", entryLabel(EntryType.PAYMENT, EntrySign.PLUS))
        assertEquals("Adjustment (+)", entryLabel(EntryType.ADJUSTMENT, EntrySign.PLUS))
        assertEquals("Adjustment (−)", entryLabel(EntryType.ADJUSTMENT, EntrySign.MINUS))
    }
}
