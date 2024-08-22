package com.example.rma_projekat_1.leaderboard.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LeaderboardData (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: Double,
    val published: String,
    val createdAt: String,
    val ranking: Int
)
