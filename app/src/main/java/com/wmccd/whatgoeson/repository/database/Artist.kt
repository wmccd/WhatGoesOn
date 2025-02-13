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

private const val ARTIST_TABLE_NAME = "Artists"
private const val ARTIST_ID = "artist_id"
private const val ARTIST_NAME = "artist_name"

@Entity(
    tableName = ARTIST_TABLE_NAME,
    indices = [Index(value = [ARTIST_NAME], unique = true)]
)
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = ARTIST_NAME) val artistName: String,
)

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: Artist): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entity: Artist)

    @Delete
    suspend fun delete(entity: Artist)

    @Query("DELETE FROM " + ARTIST_TABLE_NAME + "  WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM " + ARTIST_TABLE_NAME)
    suspend fun deleteAll()

    @Query("SELECT * FROM " + ARTIST_TABLE_NAME +  " WHERE id = :id")
    fun getArtistById(id: Long): Flow<Artist>

    @Query("SELECT * FROM " + ARTIST_TABLE_NAME +  " WHERE $ARTIST_NAME = :artistName")
    fun getArtistByString(artistName: String): Flow<Artist>

    @Query("SELECT * FROM " + ARTIST_TABLE_NAME)
    fun getAllArtists(): Flow<List<Artist>>
}