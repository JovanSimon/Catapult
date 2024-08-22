package com.example.rma_projekat_1.cats.list.model

data class CatUiModel (
    val id: String,
    val name: String,
    val alt_names: String?,
    val description: String,
    val temperament: List<String>
)