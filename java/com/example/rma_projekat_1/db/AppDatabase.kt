package com.example.rma_projekat_1.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rma_projekat_1.cats.api.di.CatDao
import com.example.rma_projekat_1.photos.api.di.PhotoDao
import com.example.rma_projekat_1.photos.db.CatsData
import com.example.rma_projekat_1.cats.db.PhotoData
import com.example.rma_projekat_1.leaderboard.api.di.LeaderboardDao
import com.example.rma_projekat_1.leaderboard.db.LeaderboardData
import com.example.rma_projekat_1.users.db.UserData
import com.example.rma_projekat_1.users.di.UserDao


@Database(
    entities = [
        UserData::class,
        CatsData::class,
        PhotoData::class,
        LeaderboardData::class
    ],
    version = 7,
    exportSchema = true,
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun catDao(): CatDao

    abstract fun photoDao(): PhotoDao
    abstract fun leaderboardDao(): LeaderboardDao
}