package com.dbabrovich.appusercases.interactor

import com.dbabrovich.domain.CommentaryFeed
import com.dbabrovich.domain.CommentaryUseCases
import com.dbabrovich.domain.remote.MobileRemote
import io.reactivex.Single

class CommentaryInteractor(
    private val mobileRemote: MobileRemote
) : CommentaryUseCases {

    //Just request from remote - we don't store anything locally
    override fun getCommentary(): Single<CommentaryFeed> =
        mobileRemote.getCommentary()
}