package com.cornerledger.app.data

enum class EntryType { CHARGE, PAYMENT, ADJUSTMENT }

enum class EntrySign { PLUS, MINUS }

enum class EntrySource { VOICE, MANUAL }

/** Signed effect of [type]/[sign]/[amount] on a customer's balance. */
fun computeDelta(type: EntryType, amount: Double, sign: EntrySign): Double = when (type) {
    EntryType.CHARGE -> amount
    EntryType.PAYMENT -> -amount
    EntryType.ADJUSTMENT -> if (sign == EntrySign.MINUS) -amount else amount
}

/** Human-readable label for a ledger entry, e.g. "Adjustment (−)". */
fun entryLabel(type: EntryType, sign: EntrySign): String = when (type) {
    EntryType.CHARGE -> "Charge"
    EntryType.PAYMENT -> "Payment"
    EntryType.ADJUSTMENT -> if (sign == EntrySign.MINUS) "Adjustment (−)" else "Adjustment (+)"
}
