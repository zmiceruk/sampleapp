package com.dbabrovich.appokhttp.remote.okhttp3

import com.dbabrovich.domain.remote.MobileRemote
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

private const val TIMEOUT_CONNECTION: Long = 20
private const val TIMEOUT_READ: Long = 10
private const val TIMEOUT_WRITE: Long = 10

object MobileRemoteFactory {
    // Gson type adapters used by mobile api.
    private val gson by lazy {
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapterFactory(GsonAdaptersRemoteModel())
            .registerTypeAdapter(Date::class.java, GsonTypeAdapters.GsonDateAdapter())
            .create()
    }

    //Used to issue callbacks for retrofit code.
    private val threadExecutor by lazy {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        Executors.newFixedThreadPool(cpuCount + 1)
    }

    /**
     * Function for creating instance of [MobileApi]
     */
    private fun createMobileApi(serverUri: String, okHttpClient: OkHttpClient): MobileApi {
        require(!serverUri.isBlank()) { "Server uri is invalid." }

        return Retrofit.Builder()
            .baseUrl(serverUri)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .callbackExecutor(threadExecutor)
            .client(okHttpClient)
            .build()
            .create(MobileApi::class.java)
    }

    /**
     * Function to create an instance of [OkHttpClient]
     */
    fun createOkHttpClient(
        interceptor: Interceptor? = null,
        sslSocketFactory: SSLSocketFactory? = null,
        trustManager: X509TrustManager? = null,
        hostnameVerifier: HostnameVerifier? = null
    ): OkHttpClient {

        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
        httpBuilder.readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
        httpBuilder.writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)

        if (interceptor !== null) {
            httpBuilder.addNetworkInterceptor(interceptor)
        }

        if (sslSocketFactory !== null && trustManager !== null) {
            httpBuilder.sslSocketFactory(sslSocketFactory, trustManager)
        }

        if (hostnameVerifier !== null) {
            httpBuilder.hostnameVerifier(hostnameVerifier)
        }

        httpBuilder.proxy(Proxy.NO_PROXY)
        httpBuilder.followRedirects(false)

        return httpBuilder.build()
    }

    /**
     * Function used to create an instance of [MobileRemote]
     */
    fun createMobileRemote(serverUri: String, okHttpClient: OkHttpClient): MobileRemote =
        AndroidMobileRemote(createMobileApi(serverUri, okHttpClient))
}