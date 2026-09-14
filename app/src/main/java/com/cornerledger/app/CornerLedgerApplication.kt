package com.cornerledger.app

import android.app.Application
import com.cornerledger.app.data.DeviceId
import com.cornerledger.app.data.LedgerDatabase
import com.cornerledger.app.data.LedgerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CornerLedgerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val repository: LedgerRepository by lazy {
        val db = LedgerDatabase.getInstance(this, applicationScope)
        LedgerRepository(db, DeviceId.get(this))
    }
}
