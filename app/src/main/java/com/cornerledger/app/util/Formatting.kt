package com.cornerledger.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** "£12.34" — always shows the magnitude; callers prepend any +/− sign themselves. */
fun formatAmount(amount: Double): String = "£" + String.format(Locale.UK, "%.2f", kotlin.math.abs(amount))

/** Title-cases each whitespace-separated word: "jane DOE" -> "Jane Doe". */
fun titleCase(s: String): String =
    Regex("\\S+").replace(s) { m ->
        val w = m.value
        w[0].uppercaseChar() + w.substring(1).lowercase(Locale.UK)
    }

/** Lower-cases and strips everything but letters/digits, for fuzzy name matching. */
fun normalizeName(s: String): String = s.lowercase(Locale.UK).replace(Regex("[^a-z0-9]"), "")

/** Up to 2 initials from a name: "Priya Shah" -> "PS". */
fun initialsOf(name: String): String =
    name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it[0].uppercaseChar().toString() }

/** "Today, 14:02" for entries made today, else "12 Sep, 09:30". */
fun dateDisplay(timestampMs: Long, nowMs: Long = System.currentTimeMillis()): String {
    val d = Calendar.getInstance().apply { timeInMillis = timestampMs }
    val now = Calendar.getInstance().apply { timeInMillis = nowMs }
    val sameDay = d.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
        d.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
    val hh = String.format(Locale.UK, "%02d", d.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.UK, "%02d", d.get(Calendar.MINUTE))
    if (sameDay) return "Today, $hh:$mm"
    val day = String.format(Locale.UK, "%02d", d.get(Calendar.DAY_OF_MONTH))
    val month = SimpleDateFormat("MMM", Locale.UK).format(d.time)
    return "$day $month, $hh:$mm"
}
