package com.wmccd.whatgoeson.repository.database

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.wmccd.whatgoeson.presentation.screens.common.MediaType
import kotlinx.coroutines.flow.Flow

const val ALBUM_TABLE_NAME = "Albums"
private const val ALBUM_ID = "album_id"
private const val ALBUM_NAME = "album_name"
private const val ALBUM_URL = "album_url"
private const val ALBUM_FAVOURITE = "album_favourite"
const val MEDIA_TYPE = "media_type"



@Entity(
    tableName = ALBUM_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Album @OptIn(ExperimentalFoundationApi::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = ALBUM_NAME) val name: String,
    @ColumnInfo(name = ALBUM_URL) val imageUrl: String,
    @ColumnInfo(name = ALBUM_FAVOURITE) val isFavourite: Boolean = false,
    @ColumnInfo(name = MEDIA_TYPE) val mediaType: MediaType = MediaType.VINYL,
    @ColumnInfo(name = "artist_id") val artistId: Long
)

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: Album): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<Album>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entity: Album)

    @Delete
    suspend fun delete(entity: Album)

    @Query("DELETE FROM $ALBUM_TABLE_NAME")
    suspend fun deleteAll()

    @Query("SELECT * FROM " + ALBUM_TABLE_NAME + " WHERE id = :id")
    fun getAlbumById(id: Int): Flow<Album>

    @Query("SELECT * FROM " + ALBUM_TABLE_NAME)
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT Count(*) FROM " + ALBUM_TABLE_NAME)
    fun getAlbumCount(): Flow<Int>

    @Query("SELECT Count(*) FROM " + ALBUM_TABLE_NAME + " WHERE media_type = :mediaType")
    fun getMediaCount(mediaType: String): Flow<Int>


    @Query("SELECT * FROM " + ALBUM_TABLE_NAME + " WHERE artist_id = :artistId")
    fun getAlbumsByArtistId(artistId: Long): Flow<List<Album>>

    @Query(
        "SELECT " +
                "Albums.album_name AS albumName, " +
                "Albums.album_url AS albumUrl, " +
                "Albums.id AS albumId, " +
                "Albums.album_favourite AS albumFavourite, " +
                "Artists.artist_name AS artistName, " +
                "Albums.media_type AS mediaType, " +
                "Artists.id AS artistId " +
                "FROM Albums " +
                "INNER JOIN Artists ON Albums.artist_id = Artists.id"
    )
    fun getAllDetails(): Flow<List<AlbumWithArtistName>>

    @Query(
        "SELECT " +
                "Artists.artist_name AS artistName, " +
                "Count(*) AS albumCount " +
                "FROM Albums " +
                "INNER JOIN Artists ON Albums.artist_id = Artists.id " +
                "GROUP BY Artists.artist_name " +
                "ORDER BY albumCount DESC, LOWER(Artists.artist_name) ASC"
    )
    fun getAlbumArtistCountList(): Flow<List<AlbumArtistCount>>
}


data class AlbumArtistCount(
    @ColumnInfo(name = "artistName") val artistName: String,
    @ColumnInfo(name = "albumCount") val albumCount: Int,
)

data class AlbumWithArtistName(
    @ColumnInfo(name = "albumName") val albumName: String,
    @ColumnInfo(name = "albumUrl") val albumUrl: String,
    @ColumnInfo(name = "albumFavourite") val albumFavourite: Boolean,
    @ColumnInfo(name = "albumId") val albumId: Long,
    @ColumnInfo(name = "artistName") val artistName: String,
    @ColumnInfo(name = "mediaType") val mediaType: MediaType,
    @ColumnInfo(name = "artistId") val artistId: Long
)
