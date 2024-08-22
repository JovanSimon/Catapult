package com.example.rma_projekat_1.album.gallery

import com.example.rma_projekat_1.album.gallery.model.PhotoUiModel

interface PhotoGalleryContract {
    data class PhotoGalleryUiState (
        val photos: List<PhotoUiModel> = emptyList(),
        val photoIdPressed: String = "",
        val clickedIndex: Int? = null
    )
}