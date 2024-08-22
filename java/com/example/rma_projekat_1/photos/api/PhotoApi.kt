package com.example.rma_projekat_1.photos.api

import com.example.rma_projekat_1.photos.api.model.PhotoApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoApi {
    @GET("images/{image_id}")
    suspend fun getPhoto(
        @Path("image_id") photoId: String?,
    ): PhotoApiModel

    @GET("images/search")
    suspend fun getPhotosByBreed(
        @Query("limit") limit: Int = 100,
        @Query("breed_ids") breedId: String
    ): List<PhotoApiModel>
}