package com.cornerledger.app.voice

import com.cornerledger.app.data.EntryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VoiceCommandParserTest {

    private val customers = listOf(
        CustomerNameRef("1", "Priya Shah"),
        CustomerNameRef("2", "Tom Baxter"),
        CustomerNameRef("3", "Grace Okafor"),
        CustomerNameRef("4", "Liam Hughes"),
        CustomerNameRef("5", "Sara Ilyas"),
    )

    @Test
    fun `wordsToNumber parses simple and compound spelled-out numbers`() {
        assertEquals(40.0, VoiceCommandParser.wordsToNumber("forty"))
        assertEquals(120.0, VoiceCommandParser.wordsToNumber("one hundred and twenty"))
        assertEquals(15.0, VoiceCommandParser.wordsToNumber("fifteen"))
        assertNull(VoiceCommandParser.wordsToNumber(""))
    }

    @Test
    fun `README example - new customer via payment keyword`() {
        val result = VoiceCommandParser.parse("Jane, payment, 30", customers)!!
        assertEquals("Jane", result.name)
        assertNull(result.matchedCustomerId)
        assertEquals(EntryType.PAYMENT, result.type)
        assertEquals(30.0, result.amount, 0.0)
    }

    @Test
    fun `README example - existing customer via account keyword`() {
        val result = VoiceCommandParser.parse("Tom, account, 15", customers)!!
        assertEquals("Tom Baxter", result.name)
        assertEquals("2", result.matchedCustomerId)
        assertEquals(EntryType.CHARGE, result.type)
        assertEquals(15.0, result.amount, 0.0)
    }

    @Test
    fun `case-insensitive exact match on paid keyword`() {
        val result = VoiceCommandParser.parse("priya shah, paid, 20", customers)!!
        assertEquals("1", result.matchedCustomerId)
        assertEquals(EntryType.PAYMENT, result.type)
        assertEquals(20.0, result.amount, 0.0)
    }

    @Test
    fun `spelled-out amount resolves alongside fuzzy match`() {
        val result = VoiceCommandParser.parse("Sara, charged, forty", customers)!!
        assertEquals("5", result.matchedCustomerId)
        assertEquals(EntryType.CHARGE, result.type)
        assertEquals(40.0, result.amount, 0.0)
    }

    @Test
    fun `no-comma input falls back to keyword scan`() {
        val result = VoiceCommandParser.parse("Liam owes 26.25", customers)!!
        assertEquals("4", result.matchedCustomerId)
        assertEquals(EntryType.CHARGE, result.type)
        assertEquals(26.25, result.amount, 0.0)
    }

    @Test
    fun `filler words are stripped before amount parsing`() {
        val result = VoiceCommandParser.parse("Grace, payment, 30 pounds", customers)!!
        assertEquals("3", result.matchedCustomerId)
        assertEquals(30.0, result.amount, 0.0)
    }

    @Test
    fun `ambiguous fuzzy matches are surfaced as alternatives, not auto-matched`() {
        val ambiguous = customers + CustomerNameRef("6", "Tommy Baker")
        val result = VoiceCommandParser.parse("Tom, payment, 5", ambiguous)!!
        assertNull(result.matchedCustomerId)
        assertEquals(2, result.alternatives.size)
        assertEquals("Tom", result.name)
    }

    @Test
    fun `unknown input with no keyword defaults to charge and zero amount`() {
        val result = VoiceCommandParser.parse("mystery input with no numbers", customers)!!
        assertEquals(EntryType.CHARGE, result.type)
        assertEquals(0.0, result.amount, 0.0)
    }

    @Test
    fun `blank input returns null rather than opening the confirm sheet`() {
        assertNull(VoiceCommandParser.parse("", customers))
        assertNull(VoiceCommandParser.parse("   ", customers))
    }

    @Test
    fun `findCustomer prefers exact match over substring match`() {
        val (matched, alternatives) = VoiceCommandParser.findCustomer("Tom Baxter", customers)
        assertEquals("2", matched?.id)
        assertEquals(emptyList<CustomerNameRef>(), alternatives)
    }
}
