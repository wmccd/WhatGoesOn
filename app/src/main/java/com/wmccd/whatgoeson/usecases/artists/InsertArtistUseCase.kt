package com.wmccd.whatgoeson.usecases.artists

import com.wmccd.whatgoeson.repository.RepositoryException
import com.wmccd.whatgoeson.repository.database.Artist
import com.wmccd.whatgoeson.repository.database.ArtistDao

class InsertArtistUseCase(
    private val dao: ArtistDao
) {

    suspend fun execute(
        artistName: String
    ) {
        runCatching {
            dao.insert(
                entity = Artist(artistName = artistName)
            )
        }.onFailure {
            throw RepositoryException(
                message = "Failed to insert Artist: ${artistName}",
                cause = it
            )
        }
    }

    suspend fun execute(entity: Artist) {
        runCatching {
            dao.insert(entity)
        }.onFailure {
            throw Exception("Failed to insert Artist: " + entity.toString(), it)
        }
    }

    companion object {
        private const val TAG = "InsertArtistUseCase"
    }
}