package com.dbabrovich.domain

import io.reactivex.Single

/**
 * Use cases for manipulating commentaries
 */
interface CommentaryUseCases {
    fun getCommentary(): Single<CommentaryFeed>
}
