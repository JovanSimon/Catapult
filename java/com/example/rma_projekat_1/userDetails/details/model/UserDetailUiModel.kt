package com.example.rma_projekat_1.userDetails.details.model

import com.example.rma_projekat_1.leaderboard.db.LeaderboardData
import com.example.rma_projekat_1.users.db.UserData

data class UserDetailUiModel(
    val user: UserData,
    val allResults: List<LeaderboardData>,
    val bestResult: LeaderboardData?,
    val bestRanking: Int
)