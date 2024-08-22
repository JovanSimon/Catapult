package com.example.rma_projekat_1.cats.api.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rma_projekat_1.photos.db.CatsData

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CatsData>)

    @Query("SELECT * FROM CatsData")
    suspend fun getAllCats(): List<CatsData>

    @Query("SELECT * FROM CatsData WHERE id = :id")
    suspend fun getSpecificCat(id: String): CatsData
}