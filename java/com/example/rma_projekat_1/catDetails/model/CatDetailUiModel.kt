package com.example.rma_projekat_1.catDetails.model

data class CatDetailUiModel(
    val catId: String,
    val image_id : String? = null,
    val name : String,
    val description: String,
    val origin: String,
    val temperament: List<String>,
    val life_span: String,
    val weight: String,
    val adaptability: Int,
    val affection_level: Int,
    val child_friendly: Int,
    val dog_friendly: Int,
    val energy_level: Int,
    val rare: Int,
    val wikipedia_url: String? = null,
    val photo_url: String? = null
)
