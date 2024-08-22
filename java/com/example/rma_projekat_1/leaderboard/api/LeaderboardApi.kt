package com.example.rma_projekat_1.leaderboard.api

import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardApiModel
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardRequest
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApi {
    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("category") category: Int
    ): List<LeaderboardApiModel>

    @POST("leaderboard")
    suspend fun publishLeaderboard(
        @Body quizResult: LeaderboardRequest
    ): LeaderboardResponse
}