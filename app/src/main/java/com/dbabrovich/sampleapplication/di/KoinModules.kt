package com.dbabrovich.sampleapplication.di

import android.content.Context
import com.dbabrovich.appokhttp.remote.okhttp3.MobileRemoteFactory
import com.dbabrovich.apppresentation.AndroidMainPresenter
import com.dbabrovich.apppresentation.presenter.MainPresenter
import com.dbabrovich.appusercases.interactor.CommentaryInteractor
import com.dbabrovich.domain.CommentaryUseCases
import com.dbabrovich.sampleapplication.R
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

fun createModules(): Module = module {
    //Specify no network interceptor for okhttp
    single<OkHttpClient?> { MobileRemoteFactory.createOkHttpClient() }

    factory {
        val context: Context = get()
        MobileRemoteFactory.createMobileRemote(context.getString(R.string.server_name), get())
    }

    factory<CommentaryUseCases> { CommentaryInteractor(mobileRemote = get()) }

    factory<MainPresenter> { AndroidMainPresenter(commentsInteractor = get()) }
}