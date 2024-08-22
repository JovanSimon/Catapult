package com.example.rma_projekat_1.cats.api.di

import com.example.rma_projekat_1.cats.api.CatsApi
import com.example.rma_projekat_1.networking.CatApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatModule {
    @Provides
    @Singleton
    fun provideCatApi(@CatApiClient retrofit: Retrofit): CatsApi {
        return retrofit.create(CatsApi::class.java)
    }
}