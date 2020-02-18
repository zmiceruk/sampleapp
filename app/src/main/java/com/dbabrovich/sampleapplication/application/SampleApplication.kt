package com.dbabrovich.sampleapplication.application

import android.app.Application
import com.dbabrovich.sampleapplication.di.createModules
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //Initialise ThreeTen Android Backport
        AndroidThreeTen.init(this)

        //Initiate DI
        startKoin {
            androidLogger()
            androidContext(this@SampleApplication)
            modules(listOf(createModules()))
        }
    }
}