package com.example.rma_projekat_1.leaderboard.mappers

import android.annotation.SuppressLint
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardApiModel
import com.example.rma_projekat_1.leaderboard.listLeaderboard.model.LeaderboardUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun LeaderboardApiModel.asLeaderboardUiModel(totalGames: Int): LeaderboardUiModel {
    return LeaderboardUiModel(
        nickname = this.nickname,
        result = this.result,
        createdAt = makeDate(this.createdAt),
        totalGamesPlayed = totalGames
    )
}

@SuppressLint("SimpleDateFormat")
fun makeDate(timestamp: Long): String {
    val format = SimpleDateFormat("dd.MM.yyyy. | HH:mm", Locale.getDefault())
    return format.format(timestamp)
}