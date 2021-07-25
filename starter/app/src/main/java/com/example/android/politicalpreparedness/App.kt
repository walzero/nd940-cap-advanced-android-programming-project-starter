package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {

    private val module by lazy {
        module {
            single { ElectionDatabase.getInstance(get()) }
            single { CivicsApi }
            single { CivicInfoRepository(get(), get()) }
            factory { RepresentativeViewModel() }
            factory { ElectionsViewModel(get()) }
            factory { VoterInfoViewModel(get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        plantTimber()
        tossAKoinToYourWitcher()
    }

    private fun tossAKoinToYourWitcher() {

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(module))
        }
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}