package com.example.rma_projekat_1.userDetails.details

import com.example.rma_projekat_1.album.grid.AlbumGridContract.AlbumGridError
import com.example.rma_projekat_1.cats.list.BottomNavigationItem
import com.example.rma_projekat_1.userDetails.details.model.UserDetailUiModel

interface UserDetailContract {
    data class UserDetailContractState(
        val loading: Boolean = false,
        val userUiModel: UserDetailUiModel? = null,
        val readyToShow: Boolean = false,
        val navigationItems: List<BottomNavigationItem> = emptyList(),
        val selectedItemNavigationIndex: Int = 3,
        val error: UserDetailError? = null
    )

    sealed class UserDetailError {
        data class CantFindUser(val cause: Throwable? = null) : UserDetailError()
    }

    sealed class UserDetailContractUiEvent{
        data class SelectedNavigationIdex(val index: Int): UserDetailContractUiEvent()
    }
}