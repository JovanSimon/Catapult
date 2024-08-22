package com.example.rma_projekat_1.leaderboard.listLeaderboard

import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.leaderboard.listLeaderboard.model.LeaderboardUiModel

interface LeaderboardContract {
    data class LeaderboardState(
        val loading: Boolean = false,
        val usersResults: List<LeaderboardUiModel> = emptyList(),
        val incPage: Boolean = false,
        val decrPage: Boolean = false,
        val tmpPage: Int = 1,
        val maxPage: Int = 1,
        val showPerPage: Int = 10,
        val usersResultsToShowPerPage: List<LeaderboardUiModel> = emptyList(),
        val navigationItems: List<BottomNavigationItem> = emptyList(),
        val selectedItemNavigationIndex: Int = 2,
        val error: LeaderboardError? = null
    )

    sealed class LeaderboardError {
        data class LeaderboardLoadFail(val cause: Throwable? = null) : LeaderboardError()
    }

    sealed class LeaderboardUiEvent{
        data class SelectedNavigationIdex(val index: Int): LeaderboardUiEvent()
        data class MovePage(val num: Int) : LeaderboardUiEvent()
    }
}