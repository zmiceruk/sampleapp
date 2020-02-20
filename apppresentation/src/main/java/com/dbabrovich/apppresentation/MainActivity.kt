package com.dbabrovich.apppresentation

import android.os.Bundle
import android.util.Log
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
import com.dbabrovich.domain.CommentaryFeed
import com.dbabrovich.domain.CommentaryUseCases
import com.dbabrovich.domain.Unspecified
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
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

        //Hook up refresh action
        viewBinding.refresh.setOnRefreshListener {
            presenter.dispatchAction(MainActions.Refresh)
        }
    }

    override fun render(viewState: MainViewState) {
        when (viewState) {
            is MainViewState.CommentaryViewState -> {
                //Update loading state
                viewBinding.refresh.isRefreshing = viewState.isLoading


            }
        }
    }
}

class AndroidMainPresenter(
    private val commentsInteractor: CommentaryUseCases
) : MainPresenter() {
    companion object {
        private const val TAG = "AndroidMainPresenter"
    }

    private sealed class Changes {
        //At the moment used to kick start view creation
        object Loading : Changes()

        data class Commentary(
            val commentaryFeed: CommentaryFeed,
            val error: Throwable? = null
        ) : Changes()
    }

    //For queueing action commands
    private val actions = PublishSubject.create<MainActions>()

    //This is used to post ui view states for rendering
    private val viewStateSubject = BehaviorSubject.create<MainViewState>()

    //Used to cleanup subscriptions when view becomes inactive
    private val viewVisibleDisposables = CompositeDisposable()

    //Used to clean up pending subscriptions/disposables
    private val disposables = CompositeDisposable()

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

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()

        //Reference to the current view state
        var currentViewState: MainViewState = MainViewState.Unspecified

        //Start loading data
        val onInitialLoad = commentsInteractor.getCommentary()
            .map<Changes> {
                //Map to change
                Changes.Commentary(it)
            }
            .onErrorReturn {
                //On error return unspecified commentary feed
                Changes.Commentary(Unspecified.COMMENTARY_FEED, error = it)
            }
            .toObservable()
            .subscribeOn(Schedulers.io())
            .startWith(Changes.Loading)

        //Hook up refreshing action
        val onRefreshAction = actions.ofType<MainActions.Refresh>()
            .flatMap {
                commentsInteractor.getCommentary()
                    .map<Changes> {
                        //Map to change
                        Changes.Commentary(it)
                    }
                    .onErrorReturn {
                        //On error return unspecified commentary feed
                        Changes.Commentary(Unspecified.COMMENTARY_FEED, error = it)
                    }
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .startWith(Changes.Loading)
            }

        disposables += Observable.merge<Changes>(
            listOf(
                onInitialLoad, onRefreshAction
            )
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ change ->
                val newState = reduce(change, currentViewState)
                if (newState != currentViewState) {
                    viewStateSubject.onNext(newState)
                    currentViewState = newState
                }
            }, { error ->
                Log.e(TAG, "This must not happen.", error)
            })
    }

    /**
     * The function takes change, current view state and generates new view state (or leaves the old one)
     */
    private fun reduce(change: Changes, currentViewState: MainViewState): MainViewState =
        when (change) {
            Changes.Loading -> {
                //This the initial loading stage - just show a spinner
                when (currentViewState) {
                    is MainViewState.CommentaryViewState -> {
                        currentViewState.copy(isLoading = true, errorMessage = "")
                    }
                    else -> {
                        MainViewState.CommentaryViewState(Unspecified.COMMENTARY_FEED, "", true)
                    }
                }
            }
            is Changes.Commentary -> {
                //We have commentary changes
                when (currentViewState) {
                    is MainViewState.CommentaryViewState -> {
                        //Update the view state
                        change.error?.let {
                            currentViewState.copy(
                                errorMessage = it.message ?: "",
                                isLoading = false
                            )
                        } ?: currentViewState.copy(
                            commentaryFeed = change.commentaryFeed,
                            errorMessage = "",
                            isLoading = false
                        )
                    }
                    else -> {
                        //Leave as is
                        currentViewState
                    }
                }
            }
            else -> {
                //Looks like we don't support this change yet - return old view state
                currentViewState
            }
        }
}