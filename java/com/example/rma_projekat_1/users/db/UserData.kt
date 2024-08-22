package com.example.rma_projekat_1.users.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData (
    @PrimaryKey val id: Int,
    val name: String,
    val lastName: String,
    val email: String,
    val nickName: String
)