package com.example.rma_projekat_1.quiz

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.core.compose.AlertDialogExample
import com.example.rma_projekat_1.core.compose.AppIconButton
import com.example.rma_projekat_1.core.compose.PhotoPreview
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge
import com.example.rma_projekat_1.photos.api.model.PhotoApiModel
import com.example.rma_projekat_1.quiz.models.QuizQuestionTypes
import kotlin.random.Random

fun NavGraphBuilder.quiz(
    route: String,
    onUserClick: (String) -> Unit
) = composable(
    route = route
) {
    val quizViewModel = hiltViewModel<QuizViewModel>()

    val state = quizViewModel.state.collectAsState()
    EnableEdgeToEdge()
    QuizScreen(
        state = state.value,
        eventPublisher = {
            quizViewModel.setEvent(it)
        },
        onUserClick = onUserClick
    )

    val animationState = remember { mutableStateOf(false) }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: QuizContract.QuizState,
    eventPublisher: (uiEvent: QuizContract.QuizEvent) -> Unit,
    onUserClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Quiz",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        AppIconButton(
                            imageVector = Icons.Default.ArrowBack,
                            onClick = {
                                eventPublisher(QuizContract.QuizEvent.WantToExit(true))
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${state.timeLeft / 60} : ${state.timeLeft % 60}",
                    style = MaterialTheme.typography.bodyLarge

                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        content = { paddingValues ->
            AnimatedContent(
                targetState = state.doTransition,
                transitionSpec = {
                    slideInHorizontally { it }.togetherWith(slideOutHorizontally { -it })
                }
            ) {
                if (state.error != null){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is QuizContract.QuizError.QuizCantGenerateQuestions ->
                                "Failed to load questions. Error message: ${state.error.cause?.message}."
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
                                    Text(text = "Go back to breeds screen",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }else {

                    if (state.readyToMoveToLeaderboar) {
                        onUserClick("leaderboard")
                    }

                    if (state.wantToExit) {
                        AlertDialogExample(
                            onDismissRequest = {
                                eventPublisher(
                                    QuizContract.QuizEvent.WantToExit(
                                        false
                                    )
                                )
                            },
                            onConfirmation = { onUserClick("breeds") },
                            dialogText = "Are you sure you want to exit the quiz?",
                            dialogTitle = "Exiting quiz?",
                            icon = Icons.Default.PlayArrow
                        )
                    }

                    if (state.quizHasEnded) {
                        AlertDialogExample(
                            onDismissRequest = {
                                eventPublisher(QuizContract.QuizEvent.WantsToPublish(false))
                                onUserClick("breeds")
                            },
                            onConfirmation = {
                                eventPublisher(
                                    QuizContract.QuizEvent.WantsToPublish(
                                        true
                                    )
                                )
                            },
                            dialogText = "Your score is ${state.finalQuizPoints}.\nWould you like to publish your resoult to public leaderboard",
                            dialogTitle = "You finished the quiz",
                            icon = Icons.Default.PlayArrow
                        )
                    }

                    if (state.timeOut) {
                        AlertDialogExample(
                            onDismissRequest = {
                                eventPublisher(QuizContract.QuizEvent.WantsToPublish(false))
                            },
                            onConfirmation = {
                                eventPublisher(QuizContract.QuizEvent.WantsToPublish(true))
                                eventPublisher(QuizContract.QuizEvent.WantsToPublish(true))
                            },
                            dialogText = "Time is out.\nYour score is ${state.finalQuizPoints}.\nWould you like to publish your resoult to public leaderboard",
                            dialogTitle = "You are out of time",
                            icon = Icons.Default.PlayArrow
                        )
                    }

                    if (state.loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (!state.readyToPlay) {
                            AlertDialogExample(
                                onDismissRequest = { onUserClick("breeds") },
                                onConfirmation = {
                                    eventPublisher(QuizContract.QuizEvent.ReadyToPlay(true))
                                    eventPublisher(QuizContract.QuizEvent.StartTimer(true))
                                },
                                dialogText = "Do you wish to continue to the quiz?",
                                dialogTitle = "Quiz confirmation",
                                icon = Icons.Default.PlayArrow
                            )
                        } else if (state.readyToPlay && !state.loading) {
                            BackHandler {

                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                val question: QuizQuestionTypes = state.questions[state.tmpQuestionIndex]
                                var itemsForGrid = mutableListOf<String>()
                                var ans = ""
                                var questionText = ""
                                when (question) {
                                    is QuizQuestionTypes.QuizFirstTypeQuestion -> {
                                        itemsForGrid.addAll(question.allAnswers)
                                        ans = question.trueAnswere
                                        questionText = question.questionText
                                        SubcomposeAsyncImage(
                                            model = question.photo.url,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(400.dp)
                                                .width(300.dp)
                                        )
                                    }

                                    is QuizQuestionTypes.QuizSecondTypeQuestion -> {
                                        itemsForGrid.addAll(question.allAnswers)
                                        ans = question.falseAnswere
                                        questionText = question.questionText
                                        SubcomposeAsyncImage(
                                            model = question.photo.url,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(400.dp)
                                                .width(300.dp)
                                        )
                                    }

                                    is QuizQuestionTypes.QuizThirdTypeQuestion -> {
                                        itemsForGrid.addAll(question.allAnswers)
                                        ans = question.rightName
                                        questionText = question.questionText
                                        SubcomposeAsyncImage(
                                            model = question.photo.url,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(400.dp)
                                                .width(300.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = questionText,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge

                                )

                                Spacer(modifier = Modifier.height(8.dp))


                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    itemsIndexed(
                                        items = itemsForGrid
                                    ) { index: Int, item: String ->
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            onClick = {
                                                eventPublisher(
                                                    QuizContract.QuizEvent.QuestionIsAnswered(
                                                        item,
                                                        ans,
                                                        it
                                                    )
                                                )
                                            }
                                        ) {
                                            Text(
                                                text = item,
                                                style = MaterialTheme.typography.bodySmall
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
