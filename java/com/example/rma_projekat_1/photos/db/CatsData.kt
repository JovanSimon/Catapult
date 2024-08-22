package com.example.rma_projekat_1.photos.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rma_projekat_1.cats.api.model.Weight

@Entity
data class CatsData (
    @PrimaryKey val id: String,
    val name: String,
    val alt_names: String? = null,
    val description: String,
    val temperament: String,
    val reference_image_id: String? = null,
    val origin: String,
    val life_span: String,
    val adaptability: Int,
    val affection_level: Int,
    val child_friendly: Int,
    val dog_friendly: Int,
    val energy_level: Int,
    val rare: Int,
    val metric: String,
    val wikipedia_url: String? = null
)