package com.example.rma_projekat_1.leaderboard.api.di

import com.example.rma_projekat_1.leaderboard.api.LeaderboardApi
import com.example.rma_projekat_1.networking.LeaderboardApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardModule {
    @Provides
    @Singleton
    fun provideLeaderboardApi(@LeaderboardApiClient retrofit: Retrofit): LeaderboardApi {
        return retrofit.create()
    }
}