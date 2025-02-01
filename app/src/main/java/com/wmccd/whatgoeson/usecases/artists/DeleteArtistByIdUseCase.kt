package com.wmccd.whatgoeson.usecases.artists

import com.wmccd.whatgoeson.repository.RepositoryException
import com.wmccd.whatgoeson.repository.database.ArtistDao

class DeleteArtistByIdUseCase (
    private val dao: ArtistDao
) {

    suspend fun execute(
        id: Int
    ) {
        runCatching {
            dao.deleteById(id = id)
        }.onFailure {
            throw RepositoryException(
                message = "Failed to delete Artist:" + id.toString(),
                cause = it
            )
        }
    }

    companion object{
        private const val TAG = "DeleteArtistUseCase"
    }
}