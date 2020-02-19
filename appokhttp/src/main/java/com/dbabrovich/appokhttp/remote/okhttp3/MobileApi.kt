package com.dbabrovich.appokhttp.remote.okhttp3

import io.reactivex.Single
import retrofit2.http.GET

interface MobileApi {

    @GET("provider/opta/football/v1/matches/987597/commentary")
    fun getFeed(): Single<RemoteModel.JsonCommentaryFeed>
}