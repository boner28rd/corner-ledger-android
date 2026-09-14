package com.cornerledger.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    /** Cached running total of this customer's entries.delta — charges increase it, payments decrease it. */
    val balance: Double,
    val createdAt: Long,
)

@Entity(
    tableName = "entries",
    indices = [Index("customerId")],
)
data class EntryEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val type: EntryType,
    /** Always positive — the absolute value of the entry. */
    val amount: Double,
    /** Only meaningful when [type] is ADJUSTMENT. */
    val sign: EntrySign,
    /** Signed effect on the customer's balance. */
    val delta: Double,
    val source: EntrySource,
    @ColumnInfo(defaultValue = "NULL")
    val rawTranscript: String?,
    val createdAt: Long,
    val createdByDevice: String,
)
