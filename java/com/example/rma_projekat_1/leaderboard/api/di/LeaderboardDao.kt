package com.example.rma_projekat_1.leaderboard.api.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rma_projekat_1.leaderboard.db.LeaderboardData

@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertResult(result: LeaderboardData)

    @Query("SELECT * FROM LeaderboardData")
    suspend fun getAllResults(): List<LeaderboardData>

    @Query("SELECT * FROM LeaderboardData ORDER BY result DESC LIMIT 1")
    suspend fun getBestResult(): LeaderboardData

    @Query("SELECT * FROM LeaderboardData ORDER BY ranking ASC LIMIT 1")
    suspend fun getBestRankedResult(): LeaderboardData
}