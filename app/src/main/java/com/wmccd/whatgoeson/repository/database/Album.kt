package com.wmccd.whatgoeson.repository.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "Albums",
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "artist_id") val artistId: String
)

@Dao
interface AlbumDao {

    @Insert
    suspend fun insert(entity: Album)

    @Update
    suspend fun update(entity: Album)

    @Delete
    suspend fun delete(entity: Album)

    @Query("SELECT * FROM Albums WHERE id = :id")
    fun getAlbumById(id: Int): Flow<Album>

    @Query("SELECT * FROM Albums")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM Albums WHERE artist_id = :artistId")
    fun getAlbumsByArtistId(artistId: Int): Flow<List<Album>>

}