package com.cornerledger.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CustomerEntity::class, EntryEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var instance: LedgerDatabase? = null

        fun getInstance(context: Context, applicationScope: CoroutineScope): LedgerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "corner-ledger.db",
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        applicationScope.launch {
                            getInstance(context, applicationScope).seedIfEmpty()
                        }
                    }
                }).build().also { instance = it }
            }
        }
    }

    private suspend fun seedIfEmpty() {
        if (customerDao().count() > 0) return
        val (customers, entries) = seedData()
        customers.forEach { customerDao().insert(it) }
        entries.forEach { entryDao().insert(it) }
    }
}
