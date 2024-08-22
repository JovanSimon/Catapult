package com.example.rma_projekat_1.catDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma_projekat_1.catDetails.model.CatDetailUiModel
import com.example.rma_projekat_1.photos.db.CatsData
import com.example.rma_projekat_1.photos.mappers.asPhotoApiModel
import com.example.rma_projekat_1.cats.repository.CatRepository
import com.example.rma_projekat_1.photos.repository.PhotoRepository
import com.example.rma_projekat_1.navigation.breedId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catRepository: CatRepository,
    private val photoRepository: PhotoRepository

): ViewModel(){

    private val breedId: String = savedStateHandle.breedId

    private val _state = MutableStateFlow(CatDetailContract.CatDetailsUiState())
    val state = _state.asStateFlow()
    private fun setStete(reducer: CatDetailContract.CatDetailsUiState.() -> CatDetailContract.CatDetailsUiState) = _state.update(reducer)

    private val events = MutableSharedFlow<CatDetailContract.CatDetailsUiEvent>()
    fun setEvent(event : CatDetailContract.CatDetailsUiEvent) = viewModelScope.launch{events.emit(event)}

    init {
        fillDetails()
    }

    private fun handleWikiUrl(url: String) {
        viewModelScope.launch {
            setEvent(CatDetailContract.CatDetailsUiEvent.OpenWiki)
        }
    }

    private fun fillDetails() {
        viewModelScope.launch {
            setStete { copy(loading = true) }
            try {
                val specificCat = withContext(Dispatchers.IO) {
                    catRepository.getSpecificCat(id = breedId)
                }
                setStete { copy(specificCat = specificCat.asSpecificCat()) }
                fetchImageUrl()
            }catch (error : Exception){
                error.printStackTrace()
                setStete { copy(error = CatDetailContract.DetailsError.CatDetailFailed(cause = error)) }
            }finally {
                setStete { copy(loading = false) }
            }
        }
    }

    private fun fetchImageUrl() {
        viewModelScope.launch {
            setStete { copy(loading_image = true) }

            try {
                val savePhoto = withContext(Dispatchers.IO){
                    val photoId = if (state.value.specificCat != null){
                        state.value.specificCat!!.image_id
                    } else {
                        null
                    }

                    (if (photoId == null) null else photoId)?.let {
                        photoRepository.fetchPhoto(
                            photoId = it,
                            breedId
                        )
                    }

                    photoId
                }

                val photoFinal = withContext(Dispatchers.IO) {
                    photoRepository.getOnePhoto(savePhoto).asPhotoApiModel()
                }

                setStete { copy(image_url = photoFinal.url, image_heigth = photoFinal.height) }
            }catch (error : Exception){
                error.printStackTrace()
            }
            setStete { copy(loading_image = false) }
        }
    }

    private fun parseStringToList(input: String): List<String> {
        val words = input.split(",")

        val trimmedWords = words.map { it.trim() }

        return trimmedWords
    }

    private fun CatsData.asSpecificCat() = CatDetailUiModel(
        catId = this.id,
        image_id = this.reference_image_id,
        name = this.name,
        description = this.description,
        temperament = parseStringToList(this.temperament),
        life_span = this.life_span,
        weight = this.metric,
        adaptability = this.adaptability,
        affection_level = this.affection_level,
        child_friendly = this.child_friendly,
        dog_friendly = this.dog_friendly,
        energy_level = this.energy_level,
        rare = this.rare,
        wikipedia_url = this.wikipedia_url,
        origin = this.origin
    )


}