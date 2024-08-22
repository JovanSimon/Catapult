package com.example.rma_projekat_1.album.grid

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma_projekat_1.photos.mappers.asPhotoApiModel
import com.example.rma_projekat_1.photos.repository.PhotoRepository
import com.example.rma_projekat_1.navigation.breedId
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
class AlbumGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository
): ViewModel() {

    private val breedId: String = savedStateHandle.breedId

    private val _state = MutableStateFlow(AlbumGridContract.AlbumGridUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGridContract.AlbumGridUiState.() -> AlbumGridContract.AlbumGridUiState) = _state.update(reducer)

    init {
        fetchPhotos()
        observePhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            setState { copy(updating = true) }
            try {
                if (photoRepository.getPhotosForSpecificBreed(breedId).isEmpty()) {
                    withContext(Dispatchers.IO){
                        photoRepository.fetchPhotosForBreed(breedId)
                    }
                }
                setState { copy(catId = breedId) }
            } catch (error: Exception) {
                setState { copy(error = AlbumGridContract.AlbumGridError.AlbumGridCantLoad(cause = error)) }
            } finally {
                setState { copy(updating = false) }
            }
        }
    }

    private fun observePhotos() {
        viewModelScope.launch {
            photoRepository.observePhotosForCat(breedId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoApiModel() }) }
                }
        }
    }
}