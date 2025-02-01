package com.wmccd.whatgoeson.usecases.artists

import com.wmccd.whatgoeson.repository.RepositoryException
import com.wmccd.whatgoeson.repository.database.Artist
import com.wmccd.whatgoeson.repository.database.ArtistDao

class UpdateArtistUseCase(
    private val dao: ArtistDao
) {

    suspend fun execute(
        //list of parameters for the use case
    ) {
        runCatching {
//            dao.update(
//                entity = Artist(
//                    //parameters for entity
//                )
//            )
        }.onFailure {
            throw RepositoryException(
                message = "Failed to update Artist: <parameters here>",
                cause = it
            )
        }
    }

    suspend fun execute(entity: Artist) {
        runCatching {
            dao.update(entity)
        }.onFailure {
            throw Exception("Failed to update Artist: " + entity.toString(), it)
        }
    }

    companion object {
        private const val TAG = "UpdateArtistUseCase"
    }
}