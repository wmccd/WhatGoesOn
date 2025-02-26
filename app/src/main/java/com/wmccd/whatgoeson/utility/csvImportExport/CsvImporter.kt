package com.wmccd.whatgoeson.utility.csvImportExport

import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.AlbumDao
import com.wmccd.whatgoeson.repository.database.Artist
import com.wmccd.whatgoeson.repository.database.ArtistDao
import kotlin.text.toBoolean
import android.util.Log
import com.opencsv.CSVReader
import com.wmccd.whatgoeson.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader

class CsvImporter(
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao
) {
    suspend fun importData(inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            try {
                MyApplication.utilities.logger.log(Log.DEBUG, TAG, "Import starting")

                //clear the tables
                albumDao.deleteAll()
                artistDao.deleteAll()

                val reader = CSVReader(InputStreamReader(inputStream))
                val lines = reader.readAll()

                // Skip the header row
                val dataLines = lines.drop(1)

                val albums = mutableListOf<Album>()
                val artists = mutableListOf<Artist>()

                for (line in dataLines) {
                    // Assuming CSV format: albumId,albumName,albumUrl,albumFavourite,artistId,artistName
                    val albumId = line[0]
                    val albumName = line[1]
                    val albumUrl = line[2]
                    val albumFavourite = line[3].toBoolean()
                    val artistId = line[4]
                    val artistName = line[5]

                    albums.add(
                        Album(
                            id = albumId.toLong(),
                            name = albumName.replace(CsvConstants.COMMA_DELIMITER, ","),
                            imageUrl = albumUrl,
                            isFavourite = albumFavourite,
                            artistId = artistId.toLong()
                        )
                    )
                    artists.add(
                        Artist(
                            id = artistId.toLong(),
                            artistName = artistName.replace(CsvConstants.COMMA_DELIMITER, ",")
                        )
                    )
                }
                artistDao.insertAll(artists)
                albumDao.insertAll(albums)

                MyApplication.utilities.logger.log(Log.DEBUG, TAG, "Import finishing")
            } catch (e: Exception) {
                MyApplication.utilities.logger.log(Log.ERROR, TAG, "importData: Exception", e)
            }
        }
    }

    companion object {
        private val TAG = CsvImporter::class.java.simpleName
    }
}