package com.example.rma_projekat_1.cats.list

import com.example.rma_projekat_1.cats.list.model.CatUiModel


interface CatListContract {

    data class CatListState(
        val loading: Boolean = false,
        val error: CatListContract.ListError? = null,
        val queue: String = "",
        val isSearchMode: Boolean = false,
        val cats: List<CatUiModel> = emptyList(),
        val filtredCats: List<CatUiModel> = emptyList(),
        val navigationItems: List<BottomNavigationItem> = emptyList(),
        val selectedItemNavigationIndex: Int = 0
    )

    sealed class ListError {
        data class CatListFailed(val cause: Throwable? = null) : ListError()
    }

    sealed class CatListUiEvent{
        data class SearchQueryChanged(val queue: String) : CatListUiEvent()
        data class SelectedNavigationIdex(val index: Int): CatListUiEvent()
        data object ClearSearch : CatListUiEvent()
        data object CloseSearchMode : CatListUiEvent()
        data object Dummy : CatListUiEvent()
    }
}