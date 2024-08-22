package com.example.rma_projekat_1.photos.api.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoApiModel(
    val id: String,
    val url: String? = null,
    val height: Int
)

//{
//    "id": "BkIEhN3pG",
//    "url": "",
//    "width": 912,
//    "height": 1024,
//    "mime_type": "image/jpeg",
//    "breeds": [
//    {
//        "id": 10,
//        "name": "American Bulldog"
//    }
//    ],
//    "categories": [],
//    "breed_ids": "10"
//}