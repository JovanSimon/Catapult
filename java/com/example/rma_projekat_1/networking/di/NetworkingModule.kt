package com.example.rma_projekat_1.networking.di

import com.example.rma_projekat_1.networking.CatApiClient
import com.example.rma_projekat_1.networking.LeaderboardApiClient
import com.example.rma_projekat_1.networking.serialization.AppJson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @CatApiClient
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor{
                val updateRequest = it.request().newBuilder()
                    .addHeader("CustomHeader", "CustomValue")
                    .addHeader("x-api-key", "live_0jYi3GHntzGW19oLqP9AKoEgaazzqbGyaozecWu6NvNci512pTsaVcuTROY6h2o4")
                    .build()
                it.proceed(updateRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }

    @CatApiClient
    @Singleton
    @Provides
    fun provideRetrofitClient(
        @CatApiClient okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @LeaderboardApiClient
    @Singleton
    @Provides
    fun provideLeaderboardRetrofitClient(
        @LeaderboardApiClient anotherOkHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(anotherOkHttpClient)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @LeaderboardApiClient
    @Singleton
    @Provides
    fun provideLeaderboarOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val updatedRequest = it.request().newBuilder()
                    .addHeader("CustomHeader", "CustomValue")
                    .build()
                it.proceed(updatedRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }
}