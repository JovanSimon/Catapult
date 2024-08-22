package com.example.rma_projekat_1.leaderboard.listLeaderboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Share
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.cats.list.CatListContract
import com.example.rma_projekat_1.leaderboard.mappers.asLeaderboardUiModel
import com.example.rma_projekat_1.leaderboard.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.min
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
): ViewModel() {
    private val _state = MutableStateFlow(LeaderboardContract.LeaderboardState())
    val state = _state.asStateFlow()
    private fun setState(reducer: LeaderboardContract.LeaderboardState.() -> LeaderboardContract.LeaderboardState) = _state.update(reducer)


    private val events = MutableSharedFlow<LeaderboardContract.LeaderboardUiEvent>()
    fun setEvent(event: LeaderboardContract.LeaderboardUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        fetchAllResults()
        fillNavigationItems()
        observePageChage()
    }

    private fun observePageChage() {
        viewModelScope.launch {
            events
                .filterIsInstance<LeaderboardContract.LeaderboardUiEvent.MovePage>()
                .collect { event ->
                    if (event.num == -1)
                        setState { copy(tmpPage = state.value.tmpPage - 1, decrPage = true, incPage = false) }
                    else
                        setState { copy(tmpPage = state.value.tmpPage + 1, decrPage = false, incPage = true) }

                    val start = (state.value.tmpPage - 1) * state.value.showPerPage
                    val end = min(start + state.value.showPerPage, state.value.usersResults.size)

                    setState { copy(usersResultsToShowPerPage = state.value.usersResults.subList(start, end)) }
                }
        }
    }

    private fun fetchAllResults() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val allResults = withContext(Dispatchers.IO) {
                    repository.fetchAllResults()
                }

                val finalAllResults = allResults.map { tmpResult ->
                    tmpResult.asLeaderboardUiModel(allResults.filter { it.nickname.equals(tmpResult.nickname) }.size)
                }

                var pagesNum = finalAllResults.size / state.value.showPerPage

                if (finalAllResults.size % state.value.showPerPage != 0){
                    pagesNum += 1
                }

                val userResultsSubString = finalAllResults.subList(0, state.value.showPerPage)

                setState { copy(usersResults = finalAllResults, usersResultsToShowPerPage = userResultsSubString, maxPage = max(pagesNum, 1)) }
            } catch (error: Exception) {
                setState { copy(error = LeaderboardContract.LeaderboardError.LeaderboardLoadFail(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    private fun fillNavigationItems() {
        val catsScreen = BottomNavigationItem(
            title = "Cats",
            route = "breeds",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        )

        val quizScreen = BottomNavigationItem(
            title = "Quiz",
            route = "quiz",
            selectedIcon = Icons.Filled.Share,
            unselectedIcon = Icons.Outlined.Share
        )

        val leaderBoardScreen = BottomNavigationItem(
            title = "Leaderboard",
            route = "leaderboard",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        )

        val profileScreen = BottomNavigationItem(
            title = "profile",
            route = "profile",
            selectedIcon = Icons.Filled.AccountBox,
            unselectedIcon = Icons.Outlined.AccountBox
        )

        viewModelScope.launch {
            setState { copy(navigationItems = listOf(catsScreen, quizScreen, leaderBoardScreen, profileScreen)) }
        }
    }
}