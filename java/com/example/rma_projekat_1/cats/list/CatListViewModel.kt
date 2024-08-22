package com.example.rma_projekat_1.cats.list

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
import com.example.rma_projekat_1.cats.api.model.CatApiModel
import com.example.rma_projekat_1.cats.list.model.CatUiModel
import com.example.rma_projekat_1.cats.mappers.asCatUiModel
import com.example.rma_projekat_1.cats.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatRepository
): ViewModel() {
    private val _state = MutableStateFlow(CatListContract.CatListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatListContract.CatListState.() -> CatListContract.CatListState) = _state.update(reducer)

    private val events = MutableSharedFlow<CatListContract.CatListUiEvent>()
    fun setEvent(event: CatListContract.CatListUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        fetchAllCats()
        ovserveSearchQuery()
        observeNavBarIndex()
        fillNavigationItems()
    }

    private fun fillNavigationItems() {
        val catsScreen = BottomNavigationItem(
            title = "Cats",
            route = "breeds",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        )

        val quizScreen = BottomNavigationItem(
            title = "Quiz",
            route = "quiz",
            selectedIcon = Icons.Filled.Share,
            unselectedIcon = Icons.Outlined.Share
        )

        val leaderBoardScreen = BottomNavigationItem(
            title = "Leaderboard",
            route = "leaderboard",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        )

        val profileScreen = BottomNavigationItem(
            title = "profile",
            route = "profile",
            selectedIcon = Icons.Filled.AccountBox,
            unselectedIcon = Icons.Outlined.AccountBox
        )

        viewModelScope.launch {
            setState { copy(navigationItems = listOf(catsScreen, quizScreen, leaderBoardScreen, profileScreen)) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun ovserveSearchQuery(){
        viewModelScope.launch {
            events
                .filterIsInstance<CatListContract.CatListUiEvent.SearchQueryChanged>()
                .debounce(2000)
                .collect{ event ->
                    if (event.queue == "")
                        setState { copy(isSearchMode = false) }
                    else{
                        setState { copy(isSearchMode = true) }
                        setState {
                            copy(filtredCats = cats.filter { it.name.contains(event.queue) })
                        }
                    }
                }
        }
    }

    private fun observeNavBarIndex(){
        viewModelScope.launch {
            events
                .filterIsInstance<CatListContract.CatListUiEvent.SelectedNavigationIdex>()
                .collect{ event ->
                    setState { copy(selectedItemNavigationIndex = event.index) }
                }
        }
    }

    private fun cutString(tmp: String): String{
        return if (tmp.length <= 250) {
            tmp
        } else {
            tmp.substring(0, 250)
        }
    }

    private fun parseStringToList(input: String): List<String> {
        val words = input.split(",")

        val trimmedWords = words.map { it.trim() }

        return trimmedWords
    }

    private fun pickThreeRandom(words: List<String>): List<String>{
        if(words.size <= 3)
            return words

        val pickedWords = mutableListOf<String>()
        val randomIndxes = mutableSetOf<Int>()

        while (randomIndxes.size < 3){
            val idx = Random.nextInt(words.size)
            if(!randomIndxes.contains(idx)){
                randomIndxes.add(idx)
                pickedWords.add(words[idx])
            }
        }

        return pickedWords
    }

    private fun CatApiModel.asCatUiModel() = CatUiModel(
        name = this.name,
        alt_names = this.alt_names,
        description = cutString(this.description),
        temperament = pickThreeRandom(parseStringToList(this.temperament)),
        id = this.id
    )

    private fun fetchAllCats(){
        viewModelScope.launch{
            setState { copy(loading = true) }
            try {

                val catsFinal = withContext(Dispatchers.IO) {
                    repository.getAllCats().map { it.asCatUiModel() }
                }

                if (catsFinal.isEmpty()) {
                    val cats = withContext(Dispatchers.IO) {
                        repository.fetchAllCats()
                    }

                    val catsFinal2 = withContext(Dispatchers.IO) {
                        repository.getAllCats().map { it.asCatUiModel() }
                    }
                    setState { copy(cats = catsFinal2) }

                } else {
                    setState { copy(cats = catsFinal) }
                }
            }catch (error: Exception){
                setState { copy(error = CatListContract.ListError.CatListFailed(cause = error)) }
            }finally {
                setState { copy(loading = false) }
            }
        }
    }
}