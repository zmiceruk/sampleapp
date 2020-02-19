package com.dbabrovich.domain.remote

import com.dbabrovich.domain.CommentaryFeed
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