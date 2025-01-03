package com.wmccd.whatgoeson.usecases.albums

import com.wmccd.whatgoeson.repository.RepositoryException
import com.wmccd.whatgoeson.repository.database.Album
import com.wmccd.whatgoeson.repository.database.AlbumDao

class InsertAlbumUseCase (
    private val dao: AlbumDao
) {

    suspend fun execute(
        //list of parameters for the use case
    ) {
        runCatching {
//            dao.insert(
//                entity = Album(
//                    //parameters for entity
//                )
//            )
        }.onFailure {
            throw RepositoryException(
                message = "Failed to insert album <parameters here>",
                cause = it
            )
        }
    }

    suspend fun execute(entity: Album) {
        runCatching {
            dao.insert(entity)
        }.onFailure {
            throw Exception("Failed to insert album: $entity", it)
        }
    }

    companion object{
        private const val TAG = "InsertAlbumUseCase"
    }
}