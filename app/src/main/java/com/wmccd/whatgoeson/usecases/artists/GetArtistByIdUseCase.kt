package com.wmccd.whatgoeson.usecases.artists

import com.wmccd.whatgoeson.repository.database.Artist
import com.wmccd.whatgoeson.repository.database.ArtistDao
import kotlinx.coroutines.flow.Flow

class GetArtistByIdUseCase(private val dao: ArtistDao) {
    fun execute(id: Int): Flow<Artist> {
        return dao.getArtistById(id)
    }
}
