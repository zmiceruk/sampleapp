package com.dbabrovich.basic.remote

import com.dbabrovich.basic.CommentaryFeed
import io.reactivex.Single

/**
 * Interface for downloading
 */
interface MobileRemote {

    /**
     * Used to download commentary feed
     */
    fun getCommentary(): Single<CommentaryFeed>
}