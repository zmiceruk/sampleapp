package com.dbabrovich.apppresentation

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import com.dbabrovich.apppresentation.databinding.ActivityMainBinding
import com.dbabrovich.apppresentation.presenter.MainActions
import com.dbabrovich.apppresentation.presenter.MainPresenter
import com.dbabrovich.apppresentation.presenter.MainView
import com.dbabrovich.apppresentation.presenter.MainViewState
import com.dbabrovich.apppresentation.recyclerview.CellCommentary
import com.dbabrovich.apppresentation.recyclerview.RecyclerDiffCallback
import com.dbabrovich.apppresentation.recyclerview.RecyclerViewBase
import com.dbabrovich.domain.CommentaryUseCases
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import net.grandcentrix.thirtyinch.TiActivity
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit

/**
 * Array that contains cell view holder creators used by the recycler view
 */
private val viewHolderCreators =
    SparseArray<RecyclerViewBase.ViewHolderBaseCreator<RecyclerViewBase.ViewHolderBase>>().apply {
        put(CellCommentary.VIEW_ID, CellCommentary.Creator)
    }

/**
 *Recycler view adapter for today's bookings
 */
private class RecyclerViewAdapter :
    RecyclerViewBase.ViewAdapter<RecyclerViewBase.ViewHolderBase>(
        viewHolderCreators, mutableListOf()
    )

private class DiffCallback(
    private val oldList: List<RecyclerViewBase.ViewModelBase>,
    private val newList: List<RecyclerViewBase.ViewModelBase>
) : RecyclerDiffCallback(oldList, newList) {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldViewModel = oldList.getOrNull(oldItemPosition)
        val newViewModel = newList.getOrNull(newItemPosition)
        return if (oldViewModel is CellCommentary.ViewModel && newViewModel is CellCommentary.ViewModel) {
            oldViewModel == newViewModel
        } else false
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newList[newItemPosition]
    }
}

class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {

    private lateinit var viewBinding: ActivityMainBinding

    override fun providePresenter(): MainPresenter = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
    }

    override fun render(viewState: MainViewState) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class AndroidMainPresenter(
    private val commentsInteractor: CommentaryUseCases
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