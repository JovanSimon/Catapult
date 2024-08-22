package com.example.rma_projekat_1

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppCore : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}