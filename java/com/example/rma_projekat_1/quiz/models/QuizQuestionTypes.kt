package com.example.rma_projekat_1.quiz.models

import com.example.rma_projekat_1.cats.db.PhotoData
import com.example.rma_projekat_1.photos.db.CatsData

abstract class QuizQuestionTypes {
    data class QuizFirstTypeQuestion (
        val questionText: String = "What breed of cat is on the picture",
        val photo: PhotoData,
        val trueAnswere: String,
        val falseAnswere: List<String>,
        val allAnswers: List<String>
    ) : QuizQuestionTypes()

    data class QuizSecondTypeQuestion (
        val questionText: String = "What temperament does not belong to the cat from the picture have?",
        val photo: PhotoData,
        val falseAnswere: String,
        val trueAnsweres: List<String>,
        val allAnswers: List<String>
    ) : QuizQuestionTypes()

    data class QuizThirdTypeQuestion (
        val questionText: String = "What temperament does the cat from the picture have?",
        val photo: PhotoData,
        val rightName: String,
        val falseNames: List<String>,
        val allAnswers: List<String>
    ) : QuizQuestionTypes()
}



