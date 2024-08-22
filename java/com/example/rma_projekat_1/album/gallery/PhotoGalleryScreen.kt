package com.example.rma_projekat_1.album.gallery

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rma_projekat_1.core.compose.AppIconButton
import com.example.rma_projekat_1.core.compose.PhotoPreview
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge

fun NavGraphBuilder.breedAlbum(
    route: String,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit
) = composable(
    route = route,
    arguments = arguments,
    enterTransition = { slideInVertically { it } },
    popExitTransition = { slideOutVertically { it } }
) { navBackStackEntry ->

    val photoGalleryViewModel = hiltViewModel<PhotoGalleryViewModel>(navBackStackEntry)
    val state = photoGalleryViewModel.state.collectAsState()

    val initialPageIndex = state.value.photos.indexOfFirst { it.photoId == state.value.photoIdPressed }.coerceAtLeast(0)
    EnableEdgeToEdge()
    PhotoGalleryScreen(
        state = state.value,
        onClose = onClose,
        initialPageIndex = initialPageIndex
    )
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    state: PhotoGalleryContract.PhotoGalleryUiState,
    onClose: () -> Unit,
    initialPageIndex: Int
) {
    val pagerState = rememberPagerState(
        initialPage = state.photos.indexOfFirst { it.photoId == state.photoIdPressed }.coerceAtLeast(0),
        pageCount = { state.photos.size }
    )

    LaunchedEffect(state.photos, state.clickedIndex) {
        if (state.photos.isNotEmpty()) {
            state.clickedIndex?.let { pagerState.scrollToPage(it) }
        }
    }

    var currentTitle by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentTitle,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose
                    )
                }
            )
        },
        content = { paddingValues ->
            if (state.photos.isNotEmpty()) {
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
                        val album = state.photos.getOrNull(pageIndex)
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                    pageSize = PageSize.Fill,
                    pageSpacing = 16.dp,
                    key = { state.photos[it].photoId }
                ) { pageIndex ->
                    val photo = state.photos[pageIndex]
                    PhotoPreview(
                        modifier = Modifier,
                        url = photo.url,
                        showTitle = false
                    )
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = "No albums."
                )
            }
        }
    )
}
