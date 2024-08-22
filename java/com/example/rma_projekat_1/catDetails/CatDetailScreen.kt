package com.example.rma_projekat_1.catDetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.rma_projekat_1.core.compose.AppIconButton
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge

fun NavGraphBuilder.breedDetails(
    route: String,
    onUserClick: (String) -> Unit,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit
) = composable(
    route = route,
    arguments = arguments
) {navBackStackEntry ->

    val catDetailViewModel: CatDetailViewModel = hiltViewModel<CatDetailViewModel>(navBackStackEntry)

    val state = catDetailViewModel.state.collectAsState()
    EnableEdgeToEdge()

    CatDetailScreen(
        state = state.value,
        eventPublisher = {
            catDetailViewModel.setEvent(it)
        },
        onClose = onClose,
        onUserClick = onUserClick
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailScreen(
    state: CatDetailContract.CatDetailsUiState,
    eventPublisher: (uiEvent: CatDetailContract.CatDetailsUiEvent) -> Unit,
    onClose: () -> Unit,
    onUserClick: (String) -> Unit
){
    val context = LocalContext.current
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { state.specificCat?.let { Text(text = it.name,style = MaterialTheme.typography.titleLarge) } },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
                    )
                }
            )
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
                if (state.error != null){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is CatDetailContract.DetailsError.CatDetailFailed ->
                                "Failed to load. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Card(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            if (state.loading_image) {
                                state.image_heigth?.let {
                                    Modifier
                                        .fillMaxWidth()
                                        .height(it.dp)
                                }?.let {
                                    Box(
                                        modifier = it,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else {
                                SubcomposeAsyncImage(
                                    modifier = Modifier.fillMaxWidth(),
                                    model = state.image_url,
                                    contentDescription = null
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            state.specificCat?.let {
                                Text(
                                    text = it.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Countries of Origin: ${state.specificCat?.origin}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Temperament:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            state.specificCat?.temperament?.forEach {
                                Text(
                                    text = "• $it"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Average Lifespan: ${state.specificCat?.life_span} years",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Average Weight: ${state.specificCat?.weight} kg",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Behavior and Needs:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "• Adaptability:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            state.specificCat?.adaptability?.let { adaptability ->
                                CustomStarRatingContent(
                                    rating = adaptability.toFloat(),
                                    numStars = 5,
                                    starIcon = Icons.Default.Star
                                )
                            }
                            Text(
                                text = "• Affection Level:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            state.specificCat?.affection_level?.let { affection_level ->
                                CustomStarRatingContent(
                                    rating = affection_level.toFloat(),
                                    numStars = 5,
                                    starIcon = Icons.Default.Star
                                )
                            }
                            Text(
                                text = "• Child Friendliness:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            state.specificCat?.child_friendly?.let { child_friendly ->
                                CustomStarRatingContent(
                                    rating = child_friendly.toFloat(),
                                    numStars = 5,
                                    starIcon = Icons.Default.Star
                                )
                            }
                            Text(
                                text = "• Dog Friendliness:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            state.specificCat?.dog_friendly?.let { dog_friendly ->
                                CustomStarRatingContent(
                                    rating = dog_friendly.toFloat(),
                                    numStars = 5,
                                    starIcon = Icons.Default.Star
                                )
                            }
                            Text(
                                text = "• Energy Level:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            state.specificCat?.energy_level?.let { energy_level ->
                                CustomStarRatingContent(
                                    rating = energy_level.toFloat(),
                                    numStars = 5,
                                    starIcon = Icons.Default.Star
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (state.specificCat!!.rare == 1) {
                                Text(
                                    text = "This breed is rare.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            } else {
                                Text(
                                    text = "This breed is not rare.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val url = state.specificCat?.wikipedia_url
                                        url?.let {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                            context.startActivity(intent)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Open Wikipedia",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                                Button(
                                    onClick = {
                                        onUserClick(state.specificCat.catId)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Open Gallery",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun CustomStarRatingContent(rating: Float, numStars: Int, starIcon: Any) {
    val normalizedRating = rating.coerceIn(0f, numStars.toFloat())

    Row {
        repeat(numStars) { index ->
            val filled = index < normalizedRating  // Check if this star should be filled

            // Determine the icon color based on whether the star is filled
            val iconColor = if (filled) {
                Color.Gray // Filled stars are red
            } else {
                Color.Gray.copy(alpha = 0.5f) // Unfilled stars are a lighter shade of red
            }

            // Determine which icon to display
            val icon = if (filled && starIcon is ImageVector) {
                starIcon // If filled and a custom icon is provided, use it
            } else {
                Icons.Default.Star // Otherwise, use the default star icon
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor, // Use the calculated icon color
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}




