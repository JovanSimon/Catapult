package com.example.rma_projekat_1.album.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma_projekat_1.cats.mappers.asPhotoUiModel
import com.example.rma_projekat_1.photos.repository.PhotoRepository
import com.example.rma_projekat_1.navigation.breedId
import com.example.rma_projekat_1.navigation.photoId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository
): ViewModel(){

    private val breedId = savedStateHandle.breedId
    private val photoId = savedStateHandle.photoId

    private val _state = MutableStateFlow(PhotoGalleryContract.PhotoGalleryUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: PhotoGalleryContract.PhotoGalleryUiState.() -> PhotoGalleryContract.PhotoGalleryUiState) =
        _state.update(reducer)

    init {
        observePhotos()
    }

    private fun observePhotos() {
        viewModelScope.launch {

            val photos = withContext(Dispatchers.IO) {
                photoRepository.getPhotosForSpecificBreed(breedId)
            }

            val clickedPhotoIndex = photos.indexOfFirst { it.imageId == photoId }

            setState { copy(clickedIndex = clickedPhotoIndex) }

            photoRepository.observePhotosForCat(breedId)
                .distinctUntilChanged()
                .collect {
                    setState {
                        copy(photos = it.map { it.asPhotoUiModel() }, photoIdPressed = photoId)
                    }
                }
        }
    }

}