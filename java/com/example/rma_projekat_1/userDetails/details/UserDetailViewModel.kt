package com.example.rma_projekat_1.userDetails.details

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
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.leaderboard.repository.LeaderboardRepository
import com.example.rma_projekat_1.userDetails.details.model.UserDetailUiModel
import com.example.rma_projekat_1.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val leaderboardRepository: LeaderboardRepository
): ViewModel() {
    private val _state = MutableStateFlow(UserDetailContract.UserDetailContractState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UserDetailContract.UserDetailContractState.() -> UserDetailContract.UserDetailContractState) = _state.update(reducer)

    private val events = MutableSharedFlow<UserDetailContract.UserDetailContractUiEvent>()
    fun setEvent(event: UserDetailContract.UserDetailContractUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        fillDetails()
        fillNavigationItems()
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

    private fun fillDetails() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUser()
                }

                val allResults = withContext(Dispatchers.IO) {
                    leaderboardRepository.getAllResults()
                }

                if (allResults != null)
                    allResults.sortedByDescending { it.createdAt }

                val bestResult = withContext(Dispatchers.IO) {
                    leaderboardRepository.getBestResult()
                }

                val bestRanking = withContext(Dispatchers.IO) {
                    leaderboardRepository.getBestRankedResult()
                }

                var bestRankingInt = -1

                if (bestRanking != null)
                    bestRankingInt = bestRanking.ranking

                setState { copy(userUiModel = user?.let {
                    UserDetailUiModel(
                        user = it,
                        allResults = allResults,
                        bestResult = bestResult,
                        bestRanking = bestRankingInt
                        )
                }, readyToShow = true) }
            } catch (error : Exception) {
                setState { copy(error = UserDetailContract.UserDetailError.CantFindUser(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }
}