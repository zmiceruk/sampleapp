package com.dbabrovich.apppresentation.presenter

import com.dbabrovich.domain.CommentaryFeed
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.TiView

sealed class MainActions {

}

/**
 * View states for the main view
 */
sealed class MainViewState {
    object Unspecified : MainViewState()

    /**
     * View state for displaying commentary feed.
     */
    data class CommentaryViewState(val commentaryFeed: CommentaryFeed) : MainViewState()
}

interface MainView : TiView {
    /**
     * Called to render view state
     */
    fun render(viewState: MainViewState)
}

abstract class MainPresenter : TiPresenter<MainView>() {
    abstract fun dispatchAction(action: MainActions)
}