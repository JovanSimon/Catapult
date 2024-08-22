package com.example.rma_projekat_1.photos.mappers

import com.example.rma_projekat_1.photos.api.model.PhotoApiModel
import com.example.rma_projekat_1.cats.db.PhotoData

fun PhotoApiModel.asPhotoDbModel(catId: String): PhotoData {
    return PhotoData(
        imageId = this.id,
        url = this.url,
        height = this.height,
        breedId = catId
    )
}

fun PhotoData.asPhotoApiModel(): PhotoApiModel {
    return PhotoApiModel(
        url = this.url,
        height = this.height,
        id = this.imageId
    )
}