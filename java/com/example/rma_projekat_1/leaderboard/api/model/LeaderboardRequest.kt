package com.example.rma_projekat_1.leaderboard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardRequest (
    val nickname: String?,
    val result: Double,
    val category: Int
)