package com.dbabrovich.apppresentation.presenter

import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.TiView

sealed class MainActions {

}

/**
 * View states for the main view
 */
sealed class MainViewState {
    object Unspecified : MainViewState()
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