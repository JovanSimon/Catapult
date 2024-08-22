package com.example.rma_projekat_1.users.login

import com.example.rma_projekat_1.album.grid.AlbumGridContract.AlbumGridError
import com.example.rma_projekat_1.users.login.model.UserUiModel

interface LoginContract {
    data class LoginUiState (
        val loggedUser: UserUiModel? = null,
        val nameFieldEmpty: Boolean = true,
        val lastNameFieldEmpty: Boolean = true,
        val nicknameFieldEmpty: Boolean = true,
        val nicknameFieldValid: Boolean = true,
        val emailFieldEmpty: Boolean = true,
        val emailFieldValid: Boolean = true,
        val allValidationPassed: Boolean = false,
        val checkingUser: Boolean = true,
        val error: LoginUiError? = null
    )

    sealed class LoginUiError {
        data class FaildToLogIn(val cause: Throwable? = null) : LoginUiError()
    }

    sealed class LoginUiEvent{
        data class nameFieldValue(val value: String): LoginUiEvent()
        data class lastNameFieldValue(val value: String): LoginUiEvent()
        data class nicknameFieldValue(val value: String): LoginUiEvent()
        data class emailFieldValue(val value: String): LoginUiEvent()
        data object LoginButtonClicked: LoginUiEvent()
    }
}