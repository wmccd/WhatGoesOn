package com.wmccd.whatgoeson.usecases.artists

import com.wmccd.whatgoeson.repository.database.Artist
import com.wmccd.whatgoeson.repository.database.ArtistDao
import kotlinx.coroutines.flow.Flow

class GetAllArtistUseCase(private val dao: ArtistDao) {
    fun execute(): Flow<List<Artist>> {
        return dao.getAllArtists()
    }
}
