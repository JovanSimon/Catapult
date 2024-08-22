package com.example.rma_projekat_1.photos.api.di

import com.example.rma_projekat_1.networking.CatApiClient
import com.example.rma_projekat_1.photos.api.PhotoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotoModule {

    @Provides
    @Singleton
    fun providePhotoApi(@CatApiClient retrofit: Retrofit): PhotoApi {
        return retrofit.create()
    }
}