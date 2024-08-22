package com.example.rma_projekat_1.users.repository

import com.example.rma_projekat_1.db.AppDatabase
import com.example.rma_projekat_1.users.db.UserData
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val database: AppDatabase
){
    suspend fun getUser(): UserData? {
        return database.userDao().getAllUsers()
    }

    suspend fun addUser(userData: UserData) {
        database.userDao().addUser(userData)
    }

    suspend fun updateUser(userData: UserData) {
        database.userDao().updateUser(userData)
    }
}