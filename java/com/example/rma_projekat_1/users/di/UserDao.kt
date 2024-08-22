package com.example.rma_projekat_1.users.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.rma_projekat_1.users.db.UserData

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: UserData)

    @Upsert
    suspend fun updateUser(user: UserData)

    @Query("SELECT * FROM UserData")
    suspend fun getAllUsers(): UserData?
}