package com.example.rma_projekat_1.leaderboard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardApiModel (
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)