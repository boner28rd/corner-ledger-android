package com.cornerledger.app.data

import android.content.Context
import android.provider.Settings

object DeviceId {
    fun get(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown-device"
}
