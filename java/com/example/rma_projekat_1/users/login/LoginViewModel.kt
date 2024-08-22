package com.example.rma_projekat_1.users.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.users.db.UserData
import com.example.rma_projekat_1.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginContract.LoginUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: LoginContract.LoginUiState.() -> LoginContract.LoginUiState) = _state.update (reducer)

    private val events = MutableSharedFlow<LoginContract.LoginUiEvent>()
    fun setEvent(event: LoginContract.LoginUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        checkIfExist()
        observeNameField()
        observeLastNameField()
        observeMailField()
        observeNicknameField()
        observeLogInField()

    }

    private fun checkIfExist() {
        viewModelScope.launch {
            val users = userRepository.getUser()

            if (users != null) {
                setState { copy(allValidationPassed = true) }
                setState { copy(checkingUser = true) }
            } else {
                setState { copy(checkingUser = false) }
            }
        }
    }

    private var nameFinal = ""
    private var lastNameFinal = ""
    private var nicknameFinal = ""
    private var emailFinal = ""

    private fun observeLastNameField() {
        viewModelScope.launch {
            events
                .filterIsInstance<LoginContract.LoginUiEvent.lastNameFieldValue>()
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
                .filterIsInstance<LoginContract.LoginUiEvent.emailFieldValue>()
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
                .filterIsInstance<LoginContract.LoginUiEvent.nicknameFieldValue>()
                .collect { event ->
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

    private fun observeLogInField() {
        viewModelScope.launch {
            events
                .filterIsInstance<LoginContract.LoginUiEvent.LoginButtonClicked>()
                .collect { event ->
                    doLogIn()
                }
        }
    }

    private fun observeNameField() {
        viewModelScope.launch {
            events
                .filterIsInstance<LoginContract.LoginUiEvent.nameFieldValue>()
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

    private fun doLogIn() {
        if (nicknameFinal != "" && emailFinal != "" && nameFinal != "" && lastNameFinal != "") {
            setState { copy(allValidationPassed = true) }
            viewModelScope.launch {
                try {
                    userRepository.addUser(UserData(1, nameFinal, lastNameFinal, emailFinal, nicknameFinal))
                } catch (error : Exception) {
                    setState { copy(error = LoginContract.LoginUiError.FaildToLogIn(cause = error)) }
                }
            }

        }
    }

    fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

     fun checkNickname(nickname: String): Boolean {
         val regex = "^[a-zA-Z0-9_]+$".toRegex()
         return nickname.matches(regex)
    }
}