package com.wmccd.whatgoeson.repository.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "Artists"

@Entity(tableName = TABLE_NAME)
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "first_value") val firstName: String,
    @ColumnInfo(name = "last_value") val lastName: String
)

@Dao
interface ArtistDao {

    @Insert
    suspend fun insert(entity: Artist)

    @Update
    suspend fun update(entity: Artist)

    @Delete
    suspend fun delete(entity: Artist)

    @Query("SELECT * FROM " + TABLE_NAME +  " WHERE id = :id")
    fun getArtistById(id: Int): Flow<Artist>

    @Query("SELECT * FROM " + TABLE_NAME)
    fun getAllArtists(): Flow<List<Artist>>
}