package com.wmccd.whatgoeson.repository.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "Artists"
private const val ARTIST_NAME = "artist_name"

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [ARTIST_NAME], unique = true)]
)
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = ARTIST_NAME) val artistName: String,
)

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: Artist)

    @Update
    suspend fun update(entity: Artist)

    @Delete
    suspend fun delete(entity: Artist)

    @Query("DELETE FROM " + TABLE_NAME + "  WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM " + TABLE_NAME)
    suspend fun deleteAll()

    @Query("SELECT * FROM " + TABLE_NAME +  " WHERE id = :id")
    fun getArtistById(id: Int): Flow<Artist>

    @Query("SELECT * FROM " + TABLE_NAME)
    fun getAllArtists(): Flow<List<Artist>>
}