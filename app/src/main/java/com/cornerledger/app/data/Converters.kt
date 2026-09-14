package com.cornerledger.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromEntryType(value: EntryType): String = value.name

    @TypeConverter
    fun toEntryType(value: String): EntryType = EntryType.valueOf(value)

    @TypeConverter
    fun fromEntrySign(value: EntrySign): String = value.name

    @TypeConverter
    fun toEntrySign(value: String): EntrySign = EntrySign.valueOf(value)

    @TypeConverter
    fun fromEntrySource(value: EntrySource): String = value.name

    @TypeConverter
    fun toEntrySource(value: String): EntrySource = EntrySource.valueOf(value)
}
