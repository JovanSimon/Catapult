package com.example.rma_projekat_1.userDetails.details

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge

fun NavGraphBuilder.profile(
    route: String,
    onUserClick: (String) -> Unit
) = composable(
    route = route
) {
    val userDetailViewModel = hiltViewModel<UserDetailViewModel>()

    val state = userDetailViewModel.state.collectAsState()
    EnableEdgeToEdge()
    UserDetailScreen(
        state = state.value,
        eventPublisher = {
            userDetailViewModel.setEvent(it)
        },
        onUserClick = onUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    state: UserDetailContract.UserDetailContractState,
    eventPublisher: (uiEvent: UserDetailContract.UserDetailContractUiEvent) -> Unit,
    onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                state.navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = state.selectedItemNavigationIndex == index,
                        onClick = {
                            eventPublisher(
                                UserDetailContract.UserDetailContractUiEvent.SelectedNavigationIdex(
                                    index
                                )
                            )
                            when (index) {
                                0 -> onUserClick("breeds")
                                1 -> onUserClick("quiz")
                                2 -> onUserClick("leaderboard")
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
            if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    val errorMessage = when (state.error) {
                        is UserDetailContract.UserDetailError.CantFindUser ->
                            "Failed to load profile details. Error message: ${state.error.cause?.message}."
                    }
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = errorMessage)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(
                                onClick = {
                                    onUserClick("breeds")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Go back to breeds screen",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else if (state.loading && !state.readyToShow) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column (
                    modifier = Modifier
                        .padding(paddingValues)
                ) {
                    Row (
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    ){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Name: " + state.userUiModel?.user!!.name + " " + state.userUiModel.user.lastName,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Nickname: ${state.userUiModel.user.nickName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Email: ${state.userUiModel.user.email}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            if (state.userUiModel!!.bestResult != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        if (state.userUiModel!!.allResults!!.isNotEmpty()) {
                                            println(state.userUiModel.allResults)
                                            Text(
                                                text = "Best result:",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = "- ${state.userUiModel.bestResult!!.result}\n" +
                                                        "- ${state.userUiModel.bestResult.createdAt}\n" +
                                                        "- ${state.userUiModel.bestResult.published}\n",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(
                            items = state.userUiModel!!.allResults
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
                                        text = user.published,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        item {
                            Row {
                                Button(
                                    onClick = {
                                        onUserClick("profileEdit")
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Edit profile info",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }
                }
            }
        }
    )
}
