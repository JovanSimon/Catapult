package com.example.rma_projekat_1.cats.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rma_projekat_1.catDetails.CatDetailContract
import com.example.rma_projekat_1.cats.list.model.CatUiModel
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge


fun NavGraphBuilder.breeds(
    route: String,
    onUserClick: (String) -> Unit
) = composable(
    route = route
) {
    val catListViewModel = hiltViewModel<CatListViewModel>()

    val state = catListViewModel.state.collectAsState()
    EnableEdgeToEdge()
    CatListScreen(
        state = state.value,
        eventPublisher = {
            catListViewModel.setEvent(it)
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
fun CatListScreen(
    state: CatListContract.CatListState,
    eventPublisher: (uiEvent: CatListContract.CatListUiEvent) -> Unit,
    onUserClick: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var text by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "BreedsList", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = text,
                    onValueChange = { newValue ->
                        text = newValue
                        eventPublisher(CatListContract.CatListUiEvent.SearchQueryChanged(newValue.text))
                    },
                    label = { Text("Search", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    interactionSource = interactionSource

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
                            eventPublisher(CatListContract.CatListUiEvent.SelectedNavigationIdex(index))
                            when (index) {
                                1 -> onUserClick("quiz")
                                2 -> onUserClick("leaderboard")
                                3 -> onUserClick("profile")
                            }
                        },
                        icon = {
                            Icon(imageVector = if (index == state.selectedItemNavigationIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                                contentDescription = item.title)
                        }
                    )

                }
            }
        },
        content = { paddingValues ->
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if(state.error != null){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is CatListContract.ListError.CatListFailed ->
                                "Failed to load. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)
                    }
                }else if(state.isSearchMode){
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues
                    ) {
                        items(
                            items = state.filtredCats,
                            key = { it.name },
                        ) { cat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 16.dp)
                                    .clickable { onUserClick(cat.id) },
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = cat.name,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = cat.description,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        repeat(cat.temperament.size) { index ->
                                            AssistChip(
                                                onClick = { onUserClick(cat.id) },
                                                label = { Text(
                                                    cat.temperament[index],
                                                    style = MaterialTheme.typography.bodyMedium)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues,
                    ) {
                        items(
                            items = state.cats,
                            key = { it.name },
                        ) { cat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 16.dp)
                                    .clickable { onUserClick(cat.id) },
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = cat.name,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = cat.description,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        repeat(cat.temperament.size) { index ->
                                            AssistChip(
                                                onClick = { onUserClick(cat.id) },
                                                label = { Text(
                                                    cat.temperament[index],
                                                    style = MaterialTheme.typography.bodySmall
                                                ) }
                                            )
                                        }
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
