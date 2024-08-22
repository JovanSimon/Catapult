package com.example.rma_projekat_1.album.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rma_projekat_1.cats.list.CatListContract
import com.example.rma_projekat_1.photos.api.model.PhotoApiModel
import com.example.rma_projekat_1.core.compose.AppIconButton
import com.example.rma_projekat_1.core.compose.PhotoPreview
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge

fun NavGraphBuilder.breedGallery(
    route: String,
    arguments: List<NamedNavArgument>,
    onUserClick: (String, String) -> Unit,
    onClose: () -> Unit,
) = composable(
    route = route,
    arguments = arguments
) {navBackStackEntry ->

    val albumGridViewModel: AlbumGridViewModel = hiltViewModel(navBackStackEntry)

    val state = albumGridViewModel.state.collectAsState()
    EnableEdgeToEdge()
    AlbumGridScreen(
        state = state.value,
        onUserClick = onUserClick,
        onClose = onClose,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumGridScreen(
    state: AlbumGridContract.AlbumGridUiState,
    onUserClick: (breedId: String, photoId: String) -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Photos") },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose
                    )
                }
            )
        },
        content = { paddingValues ->
            BoxWithConstraints (
                modifier = Modifier,
                contentAlignment = Alignment.BottomCenter
            ) {
                val screenWidth = this.maxWidth
                val cellSize = (screenWidth / 2) - 4.dp

                if (state.photos.isEmpty() && state.updating) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                        )
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is AlbumGridContract.AlbumGridError.AlbumGridCantLoad ->
                                "Failed to load. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(
                                onClick = onClose,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Go back",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        columns = GridCells.Fixed(2),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        itemsIndexed(
                            items = state.photos,
                            key = { index: Int, photo: PhotoApiModel -> photo.id },
                        ) { index: Int, album: PhotoApiModel ->
                            Card(
                                modifier = Modifier
                                    .size(cellSize)
                                    .clickable {
                                        onUserClick(state.catId, album.id)
                                    },
                            ) {
                                PhotoPreview(
                                    modifier = Modifier.fillMaxSize(),
                                    url = album.url
                                )
                            }
                        }

                        if (state.updating) {
                            item (
                                span = {
                                    GridItemSpan(10)
                                }
                            ) {
                                CircularProgressIndicator()

                            }
                        }
                    }
                }
            }
        }
    )
}
