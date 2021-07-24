package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        plantTimber()
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}