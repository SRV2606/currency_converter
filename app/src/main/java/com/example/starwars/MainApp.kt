package com.example.starwars

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("SHAW_TAG", "onCreate: ")
    }

}