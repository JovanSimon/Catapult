package com.example.rma_projekat_1.album.grid

import com.example.rma_projekat_1.photos.api.model.PhotoApiModel

interface AlbumGridContract {
    data class AlbumGridUiState(
        val updating: Boolean = false,
        val photos: List<PhotoApiModel> = emptyList(),
        val catId: String = "",
        val error: AlbumGridError? = null
    )

    sealed class AlbumGridError {
        data class AlbumGridCantLoad(val cause: Throwable? = null) : AlbumGridError()
    }
}