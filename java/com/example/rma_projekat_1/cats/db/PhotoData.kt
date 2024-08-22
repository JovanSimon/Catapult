package com.example.rma_projekat_1.cats.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class PhotoData (
    @PrimaryKey val imageId: String,
    val url: String? = null,
    val height: Int,
    val breedId: String
)