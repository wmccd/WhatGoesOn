package com.wmccd.whatgoeson.repository.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "Albums"

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"], //field name(s) in linked table
            childColumns = ["artist_id"], //field(s) name in this table
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "artist_id") val artistId: Long
)

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: Album): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entity: Album)

    @Delete
    suspend fun delete(entity: Album)

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id = :id")
    fun getAlbumById(id: Int): Flow<Album>

    @Query("SELECT * FROM " + TABLE_NAME)
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE artist_id = :artistId")
    fun getAlbumsByArtistId(artistId: Long): Flow<List<Album>>

}