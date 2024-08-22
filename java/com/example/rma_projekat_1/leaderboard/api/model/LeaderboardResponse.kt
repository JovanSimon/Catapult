package com.example.rma_projekat_1.leaderboard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardResponse(
    val result: ResultDetails,
    val ranking: Int
)

@Serializable
data class ResultDetails(
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)