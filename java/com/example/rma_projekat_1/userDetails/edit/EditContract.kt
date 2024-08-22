package com.example.rma_projekat_1.userDetails.edit

import com.example.rma_projekat_1.album.grid.AlbumGridContract.AlbumGridError
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.userDetails.details.UserDetailContract.UserDetailContractUiEvent
import com.example.rma_projekat_1.userDetails.edit.model.EditUiModel

interface EditContract {
    data class EditState(
        val user: EditUiModel? = null,
        val loading: Boolean = false,
        val nameFieldEmpty: Boolean = true,
        val lastNameFieldEmpty: Boolean = true,
        val nicknameFieldEmpty: Boolean = true,
        val nicknameFieldValid: Boolean = true,
        val emailFieldEmpty: Boolean = true,
        val emailFieldValid: Boolean = true,
        val allValidationPassed: Boolean = false,
        val navigationItems: List<BottomNavigationItem> = emptyList(),
        val selectedItemNavigationIndex: Int = 3,
        val editHasBeenDone: Boolean = false,
        val error: EditUserError? = null
    )

    sealed class EditUserError {
        data class EditDetailsFaildTo(val cause: Throwable? = null) : EditUserError()
        data class FaildToUpdate(val cause: Throwable? = null) : EditUserError()
    }

    sealed class EditEvent{
        data class nameFieldValue(val value: String): EditEvent()
        data class lastNameFieldValue(val value: String): EditEvent()
        data class nicknameFieldValue(val value: String): EditEvent()
        data class emailFieldValue(val value: String): EditEvent()
        data object editButtonClicked: EditEvent()
        data class SelectedNavigationIdex(val index: Int): EditEvent()
    }
}