package com.example.rma_projekat_1.photos.repository

import com.example.rma_projekat_1.photos.api.PhotoApi
import com.example.rma_projekat_1.cats.db.PhotoData
import com.example.rma_projekat_1.photos.mappers.asPhotoDbModel
import com.example.rma_projekat_1.db.AppDatabase
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoApi: PhotoApi,
    private val database: AppDatabase

) {
    suspend fun fetchPhoto(photoId: String, catId: String) {
        val photo = photoApi.getPhoto(photoId)
        database.photoDao().insertPic(
            photo.asPhotoDbModel(photoId)
        )
    }

    suspend fun getOnePhoto(imageId: String?): PhotoData {
        return database.photoDao().getSpecificPic(imageId)
    }

    suspend fun getPhotosForSpecificBreed(breedId: String): List<PhotoData> {
        return database.photoDao().getAllPhotosForCat(breedId)
    }

    suspend fun fetchPhotosForBreed(breedId: String) {
        return database.photoDao().upsertAllPhotosForBreed(
            photoApi.getPhotosByBreed(breedId = breedId).map { it.asPhotoDbModel(breedId) }
        )
    }

    suspend fun getBreedIdFromImageId(imageId: String): PhotoData {
        return database.photoDao().getBreedIdFromImageId(imageId)
    }

    fun observePhotosForCat(breedId: String) = database.photoDao().observeCatPhotos(breedId = breedId)
}

