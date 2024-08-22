package com.example.rma_projekat_1.leaderboard.repository

import com.example.rma_projekat_1.db.AppDatabase
import com.example.rma_projekat_1.leaderboard.api.LeaderboardApi
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardApiModel
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardRequest
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardResponse
import com.example.rma_projekat_1.leaderboard.db.LeaderboardData
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    private val database: AppDatabase
) {
    suspend fun fetchAllResults(): List<LeaderboardApiModel> {
        return leaderboardApi.getLeaderboard(1)
    }

    suspend fun publishResult(req: LeaderboardRequest) : LeaderboardResponse {
        return leaderboardApi.publishLeaderboard(req)
    }

    suspend fun addResult(result: LeaderboardData) {
        database.leaderboardDao().insertResult(result)
    }

    suspend fun getAllResults() : List<LeaderboardData> {
        return database.leaderboardDao().getAllResults()
    }

    suspend fun getBestResult() : LeaderboardData {
        return database.leaderboardDao().getBestResult()
    }

    suspend fun getBestRankedResult() : LeaderboardData {
        return database.leaderboardDao().getBestRankedResult()
    }
}