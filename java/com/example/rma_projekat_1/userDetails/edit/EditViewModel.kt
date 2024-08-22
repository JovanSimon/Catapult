package com.example.rma_projekat_1.userDetails.edit

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
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.userDetails.edit.mappers.asEditUiModel
import com.example.rma_projekat_1.users.db.UserData
import com.example.rma_projekat_1.users.login.LoginContract
import com.example.rma_projekat_1.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(EditContract.EditState())
    val state = _state.asStateFlow()
    private fun setState(reducer: EditContract.EditState.() -> EditContract.EditState) = _state.update(reducer)

    private val events = MutableSharedFlow<EditContract.EditEvent>()
    fun setEvent(event: EditContract.EditEvent) = viewModelScope.launch { events.emit(event) }

    init{
        populateUser()
        observeNameField()
        observeLastNameField()
        observeMailField()
        observeNicknameField()
        observeEditClick()
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

    private fun observeEditClick() {
        viewModelScope.launch {
            events
                .filterIsInstance<EditContract.EditEvent.editButtonClicked>()
                .collect { event ->
                    println("ViewModel clickkkkk")
                    doEdit()
                }
        }
    }

    private fun doEdit() {
        println("Nickname: $nicknameFinal email: $emailFinal name: $nameFinal lastname: $lastNameFinal")
        if (nicknameFinal != "" && emailFinal != "" && nameFinal != "" && lastNameFinal != ""
            && (nicknameFinal != state.value.user!!.nickname || emailFinal != state.value.user!!.email
                    || nameFinal != state.value.user!!.name || lastNameFinal != state.value.user!!.lastname)) {
            println("PROSO")
            setState { copy(allValidationPassed = true) }
            viewModelScope.launch {
                try {
                    userRepository.updateUser(UserData(1, nameFinal, lastNameFinal, emailFinal, nicknameFinal))
                } catch (error: Exception) {
                    setState { copy(error = EditContract.EditUserError.FaildToUpdate(cause = error)) }
                } finally {
                    setState { copy(editHasBeenDone = true) }
                }
            }
        }
    }

    private fun populateUser() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUser()!!.asEditUiModel()
                }
                setState { copy(user = user) }
            } catch (error: Exception) {
                setState { copy(error = EditContract.EditUserError.EditDetailsFaildTo(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    private var nameFinal = ""
    private var lastNameFinal = ""
    private var nicknameFinal = ""
    private var emailFinal = ""

    private fun observeNameField() {
        viewModelScope.launch {
            events
                .filterIsInstance<EditContract.EditEvent.nameFieldValue>()
                .collect { event ->
                    if (event.value == "")
                        setState { copy(nameFieldEmpty = true) }
                    else {
                        setState { copy(nameFieldEmpty = false) }
                        nameFinal = event.value
                    }
                }
        }
    }


    private fun observeLastNameField() {
        viewModelScope.launch {
            events
                .filterIsInstance<EditContract.EditEvent.lastNameFieldValue>()
                .collect { event ->
                    if (event.value == "")
                        setState { copy(lastNameFieldEmpty = true) }
                    else {
                        setState { copy(lastNameFieldEmpty = false) }
                        lastNameFinal = event.value
                    }
                }
        }
    }

    private fun observeMailField() {
        viewModelScope.launch {
            events
                .filterIsInstance<EditContract.EditEvent.emailFieldValue>()
                .collect { event ->
                    if (event.value == "")
                        setState { copy(emailFieldEmpty = true) }
                    else {
                        setState { copy(emailFieldEmpty = false) }

                        if (checkEmail(event.value)) {
                            emailFinal = event.value
                            setState { copy(emailFieldValid = false) }
                        } else {
                            setState { copy(emailFieldValid = true) }
                        }
                    }
                }
        }
    }

    private fun observeNicknameField() {
        viewModelScope.launch {
            events
                .filterIsInstance<EditContract.EditEvent.nicknameFieldValue>()
                .collect { event ->
                    println("nickname promena")
                    if (event.value == "")
                        setState { copy(nicknameFieldEmpty = true) }
                    else {
                        setState { copy(nicknameFieldEmpty = false) }

                        if (checkNickname(event.value)) {
                            nicknameFinal = event.value
                            setState { copy(nicknameFieldValid = false) }
                        } else {
                            setState { copy(nicknameFieldValid = true) }
                        }
                    }
                }
        }
    }

    fun checkEmail(email: String): Boolean {
        val regex = Regex("^[^@]+@(gmail\\.com|yahoo\\.com)$")
        return email.matches(regex)
    }

    fun checkNickname(nickname: String): Boolean {
        val regex = "^[a-zA-Z0-9_]+$".toRegex()
        return nickname.matches(regex)
    }
}