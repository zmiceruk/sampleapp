package com.dbabrovich.sampleapplication.application

import android.app.Application
import com.dbabrovich.sampleapplication.di.createModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //Initiate DI
        startKoin {
            androidLogger()
            androidContext(this@SampleApplication)
            modules(listOf(createModules()))
        }
    }
}