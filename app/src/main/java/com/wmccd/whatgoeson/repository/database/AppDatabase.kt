package com.wmccd.whatgoeson.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters

//Note: Room stores data in a plain SQLite database file.
//The data within the tables is not encrypted by default.
//Data that stays on the device (e.g. in a Room table) can generally remain unencrypted.
//Not mandatory but consider encrypting any passwords

@Database(entities = [
    User::class,
    Artist::class,
    Album::class
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : androidx.room.RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao

    companion object{
        const val DATABASE_NAME = "app_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}