package com.example.rma_projekat_1.catDetails

import com.example.rma_projekat_1.catDetails.model.CatDetailUiModel

interface CatDetailContract {
    data class CatDetailsUiState(
        val loading: Boolean = false,
        val error: DetailsError? = null,
        val specificCat: CatDetailUiModel? = null,
        val image_url: String? = "",
        val image_heigth: Int? = 0,
        val loading_image: Boolean = false
    )

    sealed class DetailsError {
        data class CatDetailFailed(val cause: Throwable? = null) : DetailsError()
    }

    sealed class CatDetailsUiEvent {
        object OpenWiki : CatDetailsUiEvent()
    }
}