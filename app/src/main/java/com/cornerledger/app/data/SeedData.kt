package com.cornerledger.app.data

import java.util.UUID

private data class SeedEntry(val type: EntryType, val amount: Double, val hoursAgo: Long)

private data class SeedCustomer(val name: String, val entries: List<SeedEntry>)

// Mirrors the interactive prototype's seedCustomers() fixture, so a fresh install
// demoes the same starting ledger shown in the design handoff screenshots.
private val SEED_CUSTOMERS = listOf(
    SeedCustomer(
        "Priya Shah",
        listOf(
            SeedEntry(EntryType.CHARGE, 85.0, 96),
            SeedEntry(EntryType.CHARGE, 60.0, 48),
            SeedEntry(EntryType.PAYMENT, 16.5, 20),
        ),
    ),
    SeedCustomer("Tom Baxter", listOf(SeedEntry(EntryType.CHARGE, 42.0, 30))),
    SeedCustomer(
        "Grace Okafor",
        listOf(
            SeedEntry(EntryType.CHARGE, 30.0, 200),
            SeedEntry(EntryType.PAYMENT, 30.0, 190),
        ),
    ),
    SeedCustomer(
        "Liam Hughes",
        listOf(
            SeedEntry(EntryType.CHARGE, 50.0, 72),
            SeedEntry(EntryType.CHARGE, 26.25, 5),
        ),
    ),
    SeedCustomer("Sara Ilyas", listOf(SeedEntry(EntryType.CHARGE, 15.0, 3))),
)

fun seedData(): Pair<List<CustomerEntity>, List<EntryEntity>> {
    val now = System.currentTimeMillis()
    val customers = mutableListOf<CustomerEntity>()
    val entries = mutableListOf<EntryEntity>()
    for (seed in SEED_CUSTOMERS) {
        val customerId = UUID.randomUUID().toString()
        var balance = 0.0
        var oldestHoursAgo = 0L
        for (e in seed.entries) {
            val delta = computeDelta(e.type, e.amount, EntrySign.PLUS)
            balance += delta
            oldestHoursAgo = maxOf(oldestHoursAgo, e.hoursAgo)
            entries += EntryEntity(
                id = UUID.randomUUID().toString(),
                customerId = customerId,
                type = e.type,
                amount = e.amount,
                sign = EntrySign.PLUS,
                delta = delta,
                source = EntrySource.MANUAL,
                rawTranscript = null,
                createdAt = now - e.hoursAgo * 3_600_000L,
                createdByDevice = "seed",
            )
        }
        customers += CustomerEntity(
            id = customerId,
            name = seed.name,
            balance = balance,
            createdAt = now - oldestHoursAgo * 3_600_000L,
        )
    }
    return customers to entries
}
