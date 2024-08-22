package com.example.rma_projekat_1.quiz

import android.annotation.SuppressLint
import android.os.CountDownTimer
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
import com.example.rma_projekat_1.cats.db.PhotoData
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.cats.repository.CatRepository
import com.example.rma_projekat_1.leaderboard.api.model.LeaderboardRequest
import com.example.rma_projekat_1.leaderboard.db.LeaderboardData
import com.example.rma_projekat_1.leaderboard.repository.LeaderboardRepository
import com.example.rma_projekat_1.photos.repository.PhotoRepository
import com.example.rma_projekat_1.quiz.models.QuizQuestionTypes
import com.example.rma_projekat_1.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val catRepository: CatRepository,
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
    private val leaderboardRepository: LeaderboardRepository
): ViewModel() {
    private val _state = MutableStateFlow(QuizContract.QuizState())
    val state = _state.asStateFlow()
    private fun setState(reducer: QuizContract.QuizState.() -> QuizContract.QuizState) = _state.update(reducer)

    private val events = MutableSharedFlow<QuizContract.QuizEvent>()
    fun setEvent(event: QuizContract.QuizEvent) = viewModelScope.launch { events.emit(event) }

    val allTemperamts = listOf(
        "Active",
        "Affectionate",
        "Agile",
        "Alert",
        "Calm",
        "Clever",
        "Curious",
        "Demanding",
        "Dependent",
        "Devoted",
        "Easy Going",
        "Energetic",
        "Expressive",
        "Friendly",
        "Fun-loving",
        "Gentle",
        "Highly interactive",
        "Independent",
        "Inquisitive",
        "Intelligent",
        "Interactive",
        "Lively",
        "Loving",
        "Loyal",
        "Mischievous",
        "Patient",
        "Peaceful",
        "Playful",
        "Quiet",
        "Relaxed",
        "Sedate",
        "Sensible",
        "Shy",
        "Social",
        "Sweet",
        "Talkative",
        "Trainable",
        "Warm"
    )

    val catBreeds = listOf(
        "Abyssinian",
        "Aegean",
        "American Bobtail",
        "American Curl",
        "American Shorthair",
        "American Wirehair",
        "Arabian Mau",
        "Australian Mist",
        "Balinese",
        "Bambino",
        "Bengal",
        "Birman",
        "Bombay",
        "British Longhair",
        "British Shorthair",
        "Burmese",
        "Burmilla",
        "California Spangled",
        "Chantilly-Tiffany",
        "Chartreux",
        "Chausie",
        "Cheetoh",
        "Colorpoint Shorthair",
        "Cornish Rex",
        "Cymric",
        "Cyprus",
        "Devon Rex",
        "Donskoy",
        "Dragon Li",
        "Egyptian Mau",
        "European Burmese",
        "Exotic Shorthair",
        "Havana Brown",
        "Himalayan",
        "Japanese Bobtail",
        "Javanese",
        "Khao Manee",
        "Korat",
        "Kurilian",
        "LaPerm",
        "Maine Coon",
        "Malayan",
        "Manx",
        "Munchkin",
        "Nebelung",
        "Norwegian Forest Cat",
        "Ocicat",
        "Oriental",
        "Persian",
        "Pixie-bob",
        "Ragamuffin",
        "Ragdoll",
        "Russian Blue",
        "Savannah",
        "Scottish Fold",
        "Selkirk Rex",
        "Siamese",
        "Siberian",
        "Singapura",
        "Snowshoe",
        "Somali",
        "Sphynx",
        "Tonkinese",
        "Toyger",
        "Turkish Angora",
        "Turkish Van",
        "York Chocolate"
    )

    private val timer = object: CountDownTimer(state.value.fullTime * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            setState { copy(timeLeft = (millisUntilFinished / 1000)) }
        }
        override fun onFinish() {
            setEvent(QuizContract.QuizEvent.TimeUp)
        }
    }

    //TODO proveri zastu puca kad nema tri temperamenta

    init {
        generateQuestions()
        observeReadyToPlay()
        observeTimeUp()
        observeAnsweres()
        observeWantToExit()
        observeWantsToPublish()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy. | HH:mm")
        val date = Date()
        return dateFormat.format(date)
    }

    private fun observeWantsToPublish() {
        viewModelScope.launch {
            events
                .filterIsInstance<QuizContract.QuizEvent.WantsToPublish>()
                .collect { event ->
                    if (event.value) {
                         val req = LeaderboardRequest(
                            nickname = userRepository.getUser()?.nickName,
                            result = state.value.finalQuizPoints,
                            category = 1
                        )

                        val response = leaderboardRepository.publishResult(req)

                        leaderboardRepository.addResult(LeaderboardData(
                            result = state.value.finalQuizPoints,
                            published = "Published",
                            createdAt = getCurrentDateString(),
                            ranking = response.ranking
                        ))
                        setState { copy(readyToMoveToLeaderboar = true) }
                    } else {
                        leaderboardRepository.addResult(LeaderboardData(
                            result = state.value.finalQuizPoints,
                            published = "Not published",
                            createdAt = getCurrentDateString(),
                            ranking = -1
                        ))
                    }
                }
        }
    }

    private fun observeWantToExit() {
        viewModelScope.launch {
            events
                .filterIsInstance<QuizContract.QuizEvent.WantToExit>()
                .collect { event ->
                    setState { copy(wantToExit = event.value) }
                }
        }
    }

    private fun observeTimeUp() {
        viewModelScope.launch {
            events
                .filterIsInstance<QuizContract.QuizEvent.TimeUp>()
                .collect { event ->
                    setState { copy(timeOut = true) }
                }
        }
    }

    var scoreCount = 0

    private fun observeAnsweres() {
        viewModelScope.launch {
            events
                .filterIsInstance<QuizContract.QuizEvent.QuestionIsAnswered>()
                .collect { event ->
                    if (event.answere.equals(event.trueAnswered)) {
                        scoreCount += 1
                    }
                    if (state.value.tmpQuestionIndex + 1 == 20) { // 20
                        timer.cancel()
                        var ubd = scoreCount * 2.5 * (1 + (state.value.timeLeft) / state.value.fullTime)
                        setState { copy(quizHasEnded = true, finalScore = scoreCount, finalQuizPoints = ubd.coerceAtMost(maximumValue = 100.00)) }
                    } else {
                        setState { copy(tmpQuestionIndex = state.value.tmpQuestionIndex + 1, doTransition = !event.transition) }
                    }
                }
        }
    }


    private fun observeReadyToPlay() {
        viewModelScope.launch {
            events
                .filterIsInstance<QuizContract.QuizEvent.ReadyToPlay>()
                .collect { event ->
                    setState { copy(readyToPlay = event.value) }
                    timer.start()
                }
        }
    }

    private fun generateQuestions() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                var listOfQuestions = mutableListOf<QuizQuestionTypes>()
                val allCats = withContext(Dispatchers.IO) {
                    catRepository.getAllCats().filter { it.id != "mala" && it.id != "cypr" }
                }

                repeat(10) {
                    val randomIndex = Random.nextInt(allCats.size)
                    val randomCat = allCats[randomIndex]

                    var photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    if (photosForRandomCat.isEmpty() || photosForRandomCat.size == 1) {
                        withContext(Dispatchers.IO) {
                            photoRepository.fetchPhotosForBreed(randomCat.id)
                        }
                    }

                    photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    val randomPhoto: PhotoData

                    val randomIndexForPhoto = Random.nextInt(photosForRandomCat.size)
                    randomPhoto = photosForRandomCat[randomIndexForPhoto]

                    val temperamentList = parseStringToList(randomCat.temperament)

                    val randomIndexForTemperament = Random.nextInt(temperamentList.size)
                    val randomTemperament = temperamentList[randomIndexForTemperament]

                    val falseTemeperamets = findFalseTemeperamets(allTemperamts, temperamentList)

                    var allQuestions = mutableListOf<String>()

                    allQuestions.addAll(falseTemeperamets)
                    allQuestions.add(randomTemperament)

                    val question = QuizQuestionTypes.QuizFirstTypeQuestion(
                        photo = randomPhoto,
                        trueAnswere = randomTemperament,
                        falseAnswere = falseTemeperamets,
                        allAnswers = allQuestions.shuffled()
                    )

                    listOfQuestions.add(question)
                }

                repeat(5) {
                    val randomIndex = Random.nextInt(allCats.size)
                    val randomCat = allCats[randomIndex]

                    var photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    if (photosForRandomCat.isEmpty() || photosForRandomCat.size == 1) {
                        withContext(Dispatchers.IO) {
                            photoRepository.fetchPhotosForBreed(randomCat.id)
                        }
                    }

                    photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    val randomPhoto: PhotoData

                    val randomIndexForPhoto = Random.nextInt(photosForRandomCat.size)
                    randomPhoto = photosForRandomCat[randomIndexForPhoto]

                    val temperamentList = parseStringToList(randomCat.temperament)

                    val falseTemeperamets = findFalseTemeperamets(allTemperamts, temperamentList)

                    var allQuestions = mutableListOf<String>()

                    val falseAnswere = falseTemeperamets.get(Random.nextInt(falseTemeperamets.size))

                    val trueAnswere = temperamentList.shuffled().subList(0, 3)

                    allQuestions.addAll(trueAnswere)
                    allQuestions.add(falseAnswere)

                    val question = QuizQuestionTypes.QuizSecondTypeQuestion(
                        photo = randomPhoto,
                        falseAnswere = falseAnswere,
                        trueAnsweres = trueAnswere,
                        allAnswers = allQuestions.shuffled()
                    )

                    listOfQuestions.add(question)
                }

                repeat(5) {
                    val randomIndex = Random.nextInt(allCats.size)
                    val randomCat = allCats[randomIndex]

                    var photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    if (photosForRandomCat.isEmpty() || photosForRandomCat.size == 1) {
                        withContext(Dispatchers.IO) {
                            photoRepository.fetchPhotosForBreed(randomCat.id)
                        }
                    }

                    photosForRandomCat = withContext(Dispatchers.IO) {
                        photoRepository.getPhotosForSpecificBreed(randomCat.id)
                    }

                    val randomPhoto: PhotoData

                    val randomIndexForPhoto = Random.nextInt(photosForRandomCat.size)
                    randomPhoto = photosForRandomCat[randomIndexForPhoto]

                    val catName = randomCat.name

                    val falseNames = getThreeRandomNames(catName, catBreeds)

                    val allQuestions = mutableListOf<String>()

                    allQuestions.addAll(falseNames)
                    allQuestions.add(catName)

                    val question = QuizQuestionTypes.QuizThirdTypeQuestion(
                        photo = randomPhoto,
                        rightName = catName,
                        falseNames = falseNames,
                        allAnswers = allQuestions.shuffled()
                    )

                    listOfQuestions.add(question)
                }

                setState { copy(questions = listOfQuestions.shuffled()) }
            } catch (error: Exception) {
                setState { copy(error = QuizContract.QuizError.QuizCantGenerateQuestions(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    fun getThreeRandomNames(exclude: String, list: List<String>): List<String> {
        val filteredList = list.filter { it != exclude }

        if (filteredList.size < 3) {
            throw IllegalArgumentException("The list does not contain enough elements to pick three random strings.")
        }

        return filteredList.shuffled().take(3)
    }

    fun findFalseTemeperamets(sourceList: List<String>, exclusionList: List<String>): List<String> {
        val filteredList = sourceList.filter { it !in exclusionList }

        return filteredList.shuffled(Random).take(3)
    }

    private fun parseStringToList(input: String): List<String> {
        val words = input.split(",")

        val trimmedWords = words.map { it.trim() }

        return trimmedWords
    }
}