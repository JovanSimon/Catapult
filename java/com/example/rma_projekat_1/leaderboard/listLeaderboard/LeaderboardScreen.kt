package com.example.rma_projekat_1.leaderboard.listLeaderboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.cats.list.CatListContract
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge

fun NavGraphBuilder.leaderboard(
    route: String,
    onUserClick: (String) -> Unit
) = composable(
    route = route
) {
    val leaderboardViewModel = hiltViewModel<LeaderboardViewModel>()

    val state = leaderboardViewModel.state.collectAsState()
    EnableEdgeToEdge()
    LeaderboardScreen(
        state = state.value,
        eventPublisher = {
            leaderboardViewModel.setEvent(it)
        },
        onUserClick = onUserClick
    )
}

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    state: LeaderboardContract.LeaderboardState,
    eventPublisher: (uiEvent: LeaderboardContract.LeaderboardUiEvent) -> Unit,
    onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Leaderboard", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This is leaderboard for you category",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        bottomBar = {
            NavigationBar {
                state.navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = state.selectedItemNavigationIndex == index,
                        onClick = {
                            eventPublisher(LeaderboardContract.LeaderboardUiEvent.SelectedNavigationIdex(index))
                            when (index) {
                                1 -> onUserClick("quiz")
                                0 -> onUserClick("breeds")
                                3 -> onUserClick("profile")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == state.selectedItemNavigationIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        },
        content = { paddingValues ->
            AnimatedContent(
                targetState = state.tmpPage,
                transitionSpec = {
                    if (state.incPage) {
                        slideInHorizontally { it }.togetherWith(slideOutHorizontally { -it })
                    } else {
                        slideInHorizontally { -it }.togetherWith(slideOutHorizontally { it })

                    }
                }
            ) {
                if (state.loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is LeaderboardContract.LeaderboardError.LeaderboardLoadFail ->
                                "Failed to load. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(
                                onClick = { onUserClick("breeds") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Go back to breeds",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (state.tmpPage == 1){
                                Button(onClick = { }, enabled = false) {
                                    Text("<-")
                                }
                            } else {
                                Button(onClick = { eventPublisher(LeaderboardContract.LeaderboardUiEvent.MovePage(-1))}) {
                                    Text("<-")
                                }
                            }
                            
                            Text(text = it.toString())

                            if (state.tmpPage == state.maxPage){
                                Button(onClick = { }, enabled = false) {
                                    Text("->")
                                }
                            }else {
                                Button(onClick = { eventPublisher(LeaderboardContract.LeaderboardUiEvent.MovePage(1))}) {
                                    Text("->")
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(
                                items = state.usersResultsToShowPerPage
                            ) { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .padding(bottom = 16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = user.nickname,
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Text(
                                            text = user.result.toString(),
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = user.createdAt,
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = user.totalGamesPlayed.toString(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        }
    )
}

