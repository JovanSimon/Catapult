package com.example.rma_projekat_1.cats.repository

import com.example.rma_projekat_1.cats.api.CatsApi
import com.example.rma_projekat_1.photos.db.CatsData
import com.example.rma_projekat_1.cats.mappers.asCatDbModel
import com.example.rma_projekat_1.db.AppDatabase
import javax.inject.Inject

class CatRepository @Inject constructor(
    private val catsApi: CatsApi,
    private val database: AppDatabase,
) {
    suspend fun fetchAllCats(){
        val cats = catsApi.getAllCats()
        return database.catDao().insertAll(
            list = cats.map { it.asCatDbModel() }
        )
    }

    suspend fun getAllCats(): List<CatsData> {
        return database.catDao().getAllCats()
    }

    suspend fun getSpecificCat(id: String): CatsData {
        return database.catDao().getSpecificCat(id)
    }
}