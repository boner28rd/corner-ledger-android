package com.cornerledger.app.voice

import com.cornerledger.app.data.EntryType
import com.cornerledger.app.util.normalizeName
import com.cornerledger.app.util.titleCase
import java.util.Locale

/** Minimal customer projection the parser needs for name matching — keeps this file Room/Android-free and unit-testable. */
data class CustomerNameRef(val id: String, val name: String)

data class ParsedVoiceCommand(
    val rawText: String,
    val name: String,
    val matchedCustomerId: String?,
    val alternatives: List<CustomerNameRef>,
    val type: EntryType,
    val amount: Double,
)

/**
 * Parses the "name, then charge-or-payment keyword, then amount" voice/typed command grammar
 * — e.g. "Jane, payment, 30" or "Tom, account, 15". Ported 1:1 from the interactive prototype's
 * parseCommand/findCustomer/wordsToNumber so behavior matches exactly across commas, keyword-scan,
 * digit and spelled-out amounts, and fuzzy customer matching.
 */
object VoiceCommandParser {

    private val PAYMENT_WORDS = listOf("payment", "paid", "pay")
    private val CHARGE_WORDS = listOf("account", "charge", "charged", "owe", "owes", "bill", "billed")
    private val FILLER_WORDS = Regex("\\bpounds?\\b|\\bquid\\b|\\bgbp\\b|\\bpence\\b", RegexOption.IGNORE_CASE)
    private val DIGIT_AMOUNT = Regex("\\d+(\\.\\d+)?")
    private val NAME_PUNCTUATION = Regex("[,.]")

    private val WORD_NUM = mapOf(
        "zero" to 0, "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "ten" to 10,
        "eleven" to 11, "twelve" to 12, "thirteen" to 13, "fourteen" to 14, "fifteen" to 15,
        "sixteen" to 16, "seventeen" to 17, "eighteen" to 18, "nineteen" to 19,
        "twenty" to 20, "thirty" to 30, "forty" to 40, "fifty" to 50,
        "sixty" to 60, "seventy" to 70, "eighty" to 80, "ninety" to 90,
    )

    fun wordsToNumber(text: String): Double? {
        val tokens = text.lowercase(Locale.UK).split(Regex("[\\s-]+")).filter { it.isNotEmpty() }
        var total: Int? = null
        var current = 0
        var found = false
        for (tok in tokens) {
            if (tok == "hundred") {
                current = (if (current != 0) current else 1) * 100
                found = true
                continue
            }
            if (tok == "and") continue
            val num = WORD_NUM[tok]
            if (num != null) {
                current += num
                found = true
                continue
            }
            if (found) {
                total = (total ?: 0) + current
                current = 0
                found = false
            }
        }
        if (found) total = (total ?: 0) + current
        return total?.toDouble()
    }

    fun findCustomer(name: String, customers: List<CustomerNameRef>): Pair<CustomerNameRef?, List<CustomerNameRef>> {
        val norm = normalizeName(name)
        if (norm.isEmpty()) return null to emptyList()
        val exact = customers.find { normalizeName(it.name) == norm }
        if (exact != null) return exact to emptyList()
        val partial = customers.filter {
            val cn = normalizeName(it.name)
            cn.contains(norm) || norm.contains(cn)
        }
        if (partial.size == 1) return partial[0] to emptyList()
        return null to partial.take(3)
    }

    fun parse(raw: String, customers: List<CustomerNameRef>): ParsedVoiceCommand? {
        val text = raw.trim()
        if (text.isEmpty()) return null
        val cleaned = FILLER_WORDS.replace(text, "").trim()

        var name: String
        var typeWord: String
        var rest: String

        val parts = cleaned.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        when {
            parts.size >= 3 -> {
                name = parts[0]
                typeWord = parts[1].lowercase(Locale.UK)
                rest = parts.drop(2).joinToString(" ")
            }
            parts.size == 2 -> {
                name = parts[0]
                rest = parts[1]
                typeWord = parts[1].lowercase(Locale.UK)
            }
            else -> {
                val lower = cleaned.lowercase(Locale.UK)
                val allWords = PAYMENT_WORDS + CHARGE_WORDS
                var idx = -1
                var hitWord = ""
                for (w in allWords) {
                    val i = lower.indexOf(w)
                    if (i != -1 && (idx == -1 || i < idx)) {
                        idx = i
                        hitWord = w
                    }
                }
                if (idx != -1) {
                    name = cleaned.substring(0, idx).trim()
                    typeWord = hitWord
                    rest = cleaned.substring(idx + hitWord.length)
                } else {
                    name = cleaned
                    rest = cleaned
                    typeWord = ""
                }
            }
        }

        val type = when {
            PAYMENT_WORDS.any { typeWord.contains(it) } -> EntryType.PAYMENT
            CHARGE_WORDS.any { typeWord.contains(it) } -> EntryType.CHARGE
            else -> EntryType.CHARGE
        }

        val digitMatch = DIGIT_AMOUNT.find(rest)
        val amount = digitMatch?.value?.toDoubleOrNull() ?: wordsToNumber(rest) ?: 0.0

        val cleanedName = titleCase(NAME_PUNCTUATION.replace(name, "").trim()).ifEmpty { "Unknown" }
        val (matched, alternatives) = findCustomer(cleanedName, customers)

        return ParsedVoiceCommand(
            rawText = text,
            name = matched?.name ?: cleanedName,
            matchedCustomerId = matched?.id,
            alternatives = alternatives,
            type = type,
            amount = amount,
        )
    }
}
