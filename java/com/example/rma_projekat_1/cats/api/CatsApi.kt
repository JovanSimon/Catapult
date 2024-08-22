package com.example.rma_projekat_1.cats.api

import com.example.rma_projekat_1.cats.api.model.CatApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {
    @GET("breeds")
    suspend fun getAllCats(): List<CatApiModel>

    @GET("breeds/{breed_id}")
    suspend fun getCat(
        @Path("breed_id") catId: String
    ): CatApiModel
}