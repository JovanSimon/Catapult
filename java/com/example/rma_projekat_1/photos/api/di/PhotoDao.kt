package com.example.rma_projekat_1.photos.api.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.rma_projekat_1.cats.db.PhotoData
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPic(photoData: PhotoData)

    @Query("SELECT * FROM PhotoData WHERE imageId = :id")
    suspend fun getSpecificPic(id: String?): PhotoData

    @Upsert
    fun upsertAllPhotosForBreed(data: List<PhotoData>)

    @Query("SELECT * FROM PhotoData WHERE breedId = :breedId")
    suspend fun getAllPhotosForCat(breedId: String): List<PhotoData>

    @Query("SELECT * FROM PhotoData WHERE breedId = :breedId")
    fun observeCatPhotos(breedId: String): Flow<List<PhotoData>>

    @Query("SELECT * FROM PhotoData WHERE imageId = :imageId")
    suspend fun getBreedIdFromImageId(imageId: String): PhotoData
}