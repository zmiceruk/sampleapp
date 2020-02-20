package com.dbabrovich.apppresentation

import android.os.Bundle
import com.dbabrovich.apppresentation.presenter.MainActions
import com.dbabrovich.apppresentation.presenter.MainPresenter
import com.dbabrovich.apppresentation.presenter.MainView
import com.dbabrovich.apppresentation.presenter.MainViewState
import com.dbabrovich.appusercases.interactor.CommentaryInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import net.grandcentrix.thirtyinch.TiActivity
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit

class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {

    override fun providePresenter(): MainPresenter = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun render(viewState: MainViewState) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class AndroidMainPresenter(
    private val commentsInteractor: CommentaryInteractor
) : MainPresenter() {

    //For queueing action commands
    private val actions = PublishSubject.create<MainActions>()

    //This is used to post ui view states for rendering
    private val viewStateSubject = BehaviorSubject.create<MainViewState>()

    //Used to cleanup subscriptions when view becomes inactive
    private val viewVisibleDisposables = CompositeDisposable()

    override fun dispatchAction(action: MainActions) {
        actions.onNext(action)
    }

    override fun onAttachView(view: MainView) {
        super.onAttachView(view)
        viewVisibleDisposables += viewStateSubject
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewState ->
                this.view?.render(viewState)
            }
    }

    override fun onDetachView() {
        viewVisibleDisposables.clear()
        super.onDetachView()
    }
}