package com.example.rma_projekat_1.cats.mappers

import com.example.rma_projekat_1.album.gallery.model.PhotoUiModel
import com.example.rma_projekat_1.cats.api.model.CatApiModel
import com.example.rma_projekat_1.photos.db.CatsData
import com.example.rma_projekat_1.cats.db.PhotoData
import com.example.rma_projekat_1.cats.list.model.CatUiModel
import kotlin.random.Random

fun CatApiModel.asCatDbModel(): CatsData {
    return CatsData(
        id = this.id,
        name = this.name,
        alt_names = this.alt_names,
        description = this.description,
        temperament = this.temperament,
        reference_image_id = this.reference_image_id,
        origin = this.origin,
        life_span = this.life_span,
        adaptability = this.adaptability,
        affection_level = this.affection_level,
        child_friendly = this.child_friendly,
        dog_friendly = this.dog_friendly,
        energy_level = this.energy_level,
        rare = this.rare,
        metric = this.weight.metric,
        wikipedia_url = this.wikipedia_url
    )
}

fun PhotoData.asPhotoUiModel(): PhotoUiModel {
    return PhotoUiModel(
        photoId = this.imageId,
        url = this.url
    )
}

private fun parseStringToList(input: String): List<String> {
    val words = input.split(",")

    val trimmedWords = words.map { it.trim() }

    return trimmedWords
}

private fun pickThreeRandom(words: List<String>): List<String>{
    if(words.size <= 3)
        return words

    val pickedWords = mutableListOf<String>()
    val randomIndxes = mutableSetOf<Int>()

    while (randomIndxes.size < 3){
        val idx = Random.nextInt(words.size)
        if(!randomIndxes.contains(idx)){
            randomIndxes.add(idx)
            pickedWords.add(words[idx])
        }
    }

    return pickedWords
}

fun CatsData.asCatUiModel(): CatUiModel {
    return CatUiModel(
        id = this.id,
        name = this.name,
        alt_names = this.alt_names,
        description = this.description,
        temperament = pickThreeRandom(parseStringToList(this.temperament))
    )
}