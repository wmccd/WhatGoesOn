package com.wmccd.whatgoeson.repository.database
import androidx.room.TypeConverter
import com.wmccd.whatgoeson.presentation.screens.common.MediaType

class Converters {

    @TypeConverter
    fun fromMediaType(value: MediaType): String {
        return value.name
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return MediaType.entries.first { it.name == value }
    }
}