package com.example.rma_projekat_1.quiz

import com.example.rma_projekat_1.album.grid.AlbumGridContract.AlbumGridError
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.quiz.models.QuizQuestionTypes

interface QuizContract {
    data class QuizState(
        val loading: Boolean = false,
        val questions: List<QuizQuestionTypes> = emptyList(),
        val readyToPlay: Boolean = false,
        val tmpQuestionIndex: Int = 0,
        val quizHasEnded: Boolean = false,
        val finalScore: Int = 0,
        val timeLeft: Long = 0,
        val fullTime: Long = 300,
        val quizOverByTime: Boolean = false,
        val finalQuizPoints: Double = 0.00,
        val timeOut: Boolean = false,
        val wantToExit: Boolean = false,
        val readyToMoveToLeaderboar: Boolean = false,
        val doTransition: Boolean = false,
        val error: QuizError? = null
    )

    sealed class QuizError {
        data class QuizCantGenerateQuestions(val cause: Throwable? = null) : QuizError()
    }

    sealed class QuizEvent{
        data class ReadyToPlay(val value: Boolean) : QuizEvent()
        object doTransition: QuizEvent()
        data class WantsToPublish(val value: Boolean) : QuizEvent()
        data class StartTimer(val value: Boolean) : QuizEvent()
        data class QuestionIsAnswered(val answere: String, val trueAnswered: String, val transition: Boolean): QuizEvent()
        object TimeUp : QuizEvent()
        data class WantToExit (val value: Boolean): QuizEvent()
    }
}