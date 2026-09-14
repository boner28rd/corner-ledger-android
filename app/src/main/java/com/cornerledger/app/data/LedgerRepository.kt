package com.cornerledger.app.data

import androidx.room.withTransaction
import com.cornerledger.app.util.formatAmount
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class AppliedEntryResult(
    val customerId: String,
    val customerName: String,
    val message: String,
    val lastEntryId: String,
)

class LedgerRepository(
    private val db: LedgerDatabase,
    private val deviceId: String,
) {
    private val customerDao = db.customerDao()
    private val entryDao = db.entryDao()

    fun observeCustomers(): Flow<List<CustomerEntity>> = customerDao.observeAll()

    fun observeHistory(customerId: String): Flow<List<EntryEntity>> = entryDao.observeForCustomer(customerId)

    suspend fun getEntry(entryId: String): EntryEntity? = entryDao.getById(entryId)

    /**
     * Records a new ledger entry, creating the customer first if [customerId] doesn't
     * resolve to an existing one. When [type] is CHARGE and [paidNow] is a positive amount,
     * also records an immediate offsetting payment — mirroring the prototype's "Paid now" field.
     */
    suspend fun applyEntry(
        customerId: String?,
        name: String,
        type: EntryType,
        amount: Double,
        sign: EntrySign,
        paidNow: Double? = null,
        source: EntrySource,
        rawTranscript: String? = null,
    ): AppliedEntryResult = db.withTransaction {
        val now = System.currentTimeMillis()
        val customer = customerId?.let { customerDao.getById(it) } ?: CustomerEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            balance = 0.0,
            createdAt = now,
        ).also { customerDao.insert(it) }

        val delta = computeDelta(type, amount, sign)
        val entry = EntryEntity(
            id = UUID.randomUUID().toString(),
            customerId = customer.id,
            type = type,
            amount = amount,
            sign = sign,
            delta = delta,
            source = source,
            rawTranscript = rawTranscript,
            createdAt = now,
            createdByDevice = deviceId,
        )
        entryDao.insert(entry)
        var runningBalance = customer.balance + delta
        var message = "${customer.name} — ${entryLabel(type, sign)} ${formatAmount(amount)}"
        var lastEntryId = entry.id

        if (type == EntryType.CHARGE && paidNow != null && paidNow > 0) {
            val payDelta = -paidNow
            val payEntry = EntryEntity(
                id = UUID.randomUUID().toString(),
                customerId = customer.id,
                type = EntryType.PAYMENT,
                amount = paidNow,
                sign = EntrySign.PLUS,
                delta = payDelta,
                source = source,
                rawTranscript = null,
                createdAt = now,
                createdByDevice = deviceId,
            )
            entryDao.insert(payEntry)
            runningBalance += payDelta
            message += " (paid ${formatAmount(paidNow)} now)"
            lastEntryId = payEntry.id
        }

        customerDao.update(customer.copy(balance = runningBalance))
        AppliedEntryResult(customer.id, customer.name, message, lastEntryId)
    }

    suspend fun updateEntryAmount(entryId: String, newAmount: Double) = db.withTransaction {
        val entry = entryDao.getById(entryId) ?: return@withTransaction
        val customer = customerDao.getById(entry.customerId) ?: return@withTransaction
        val newDelta = computeDelta(entry.type, newAmount, entry.sign)
        customerDao.update(customer.copy(balance = customer.balance - entry.delta + newDelta))
        entryDao.update(entry.copy(amount = newAmount, delta = newDelta))
    }

    suspend fun deleteEntry(entryId: String) = db.withTransaction {
        val entry = entryDao.getById(entryId) ?: return@withTransaction
        val customer = customerDao.getById(entry.customerId) ?: return@withTransaction
        customerDao.update(customer.copy(balance = customer.balance - entry.delta))
        entryDao.delete(entry)
    }

    /** Full customer/ledger dataset as pretty JSON, for the Customers screen's "Export" action. */
    suspend fun exportJson(): String {
        val customers = customerDao.getAllOnce()
        val root = JSONArray()
        for (customer in customers) {
            val history = entryDao.getForCustomerOnce(customer.id)
            val historyJson = JSONArray()
            for (entry in history) {
                historyJson.put(
                    JSONObject()
                        .put("id", entry.id)
                        .put("type", entry.type.name.lowercase())
                        .put("amount", entry.amount)
                        .put("sign", if (entry.sign == EntrySign.MINUS) "-" else "+")
                        .put("delta", entry.delta)
                        .put("ts", entry.createdAt),
                )
            }
            root.put(
                JSONObject()
                    .put("id", customer.id)
                    .put("name", customer.name)
                    .put("balance", customer.balance)
                    .put("history", historyJson),
            )
        }
        return root.toString(2)
    }
}
