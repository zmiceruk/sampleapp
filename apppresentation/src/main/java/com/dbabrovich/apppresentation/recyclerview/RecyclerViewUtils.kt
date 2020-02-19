package com.dbabrovich.apppresentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

/**
 * Helper utility functions for sending notifications to a [RecyclerView]
 */
object RecyclerViewUtils {
    @JvmStatic
    fun notifyItemRangeInserted(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int, itemCount: Int
    ) {

        if (recyclerView.isComputingLayout) {
            recyclerView.post {
                notifyItemRangeInserted(
                    recyclerView,
                    adapter,
                    startPos,
                    itemCount
                )
            }
        } else {
            adapter.notifyItemRangeInserted(startPos, itemCount)
        }
    }

    @JvmStatic
    fun notifyItemInserted(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int
    ) {
        notifyItemRangeInserted(
            recyclerView,
            adapter,
            startPos,
            1
        )
    }

    @JvmStatic
    fun notifyItemRangeRemoved(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int, itemCount: Int
    ) {

        if (recyclerView.isComputingLayout) {
            recyclerView.post {
                notifyItemRangeRemoved(
                    recyclerView,
                    adapter,
                    startPos,
                    itemCount
                )
            }
        } else {
            adapter.notifyItemRangeRemoved(startPos, itemCount)
        }
    }

    @JvmStatic
    fun notifyItemRemoved(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int
    ) {
        notifyItemRangeRemoved(
            recyclerView,
            adapter,
            startPos,
            1
        )
    }

    @JvmStatic
    fun notifyItemRangeChanged(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int, itemCount: Int, payload: Any?
    ) {

        if (recyclerView.isComputingLayout) {
            recyclerView.post {
                notifyItemRangeChanged(
                    recyclerView,
                    adapter,
                    startPos,
                    itemCount,
                    payload
                )
            }
        } else {
            adapter.notifyItemRangeChanged(startPos, itemCount, payload)
        }
    }

    @JvmStatic
    fun notifyItemMoved(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        fromPost: Int, toPos: Int
    ) {
        if (recyclerView.isComputingLayout) {
            recyclerView.post {
                notifyItemMoved(
                    recyclerView,
                    adapter,
                    fromPost,
                    toPos
                )
            }
        } else {
            adapter.notifyItemMoved(fromPost, toPos)
        }

    }

    @JvmStatic
    fun notifyItemChanged(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>, startPos: Int, payload: Any? = null
    ) {
        notifyItemRangeChanged(
            recyclerView,
            adapter,
            startPos,
            1,
            payload
        )
    }

    @JvmStatic
    fun clearRecyclerView(
        recyclerView: RecyclerView,
        adapter: RecyclerViewBase.ViewAdapter<*>,
        startPos: Int, length: Int
    ) {
        for (index in startPos + length downTo startPos) {
            adapter.removeData(index)
        }
        notifyItemRangeRemoved(
            recyclerView,
            adapter,
            startPos,
            length
        )
    }
}

/**
 * Used to create a [DiffUtil.Callback] to be used with [RecyclerView]
 * in order to issue list changes notifications.
 */
abstract class RecyclerDiffCallback(
    private val oldList: List<RecyclerViewBase.ViewModelBase>,
    private val newList: List<RecyclerViewBase.ViewModelBase>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldValue = oldList.getOrNull(oldItemPosition)
        val newValue = newList.getOrNull(newItemPosition)
        return oldValue == newValue
    }
}

/**
 * Callback class for safely posting updates to the recycler view
 */
class RecycleUpdateCallback(
    private val recyclerView: RecyclerView,
    private val adapter: RecyclerView.Adapter<*>
) : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        RecyclerViewUtils.notifyItemRangeChanged(
            recyclerView, adapter, position, count, payload
        )
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        RecyclerViewUtils.notifyItemMoved(
            recyclerView, adapter, fromPosition, toPosition
        )
    }

    override fun onInserted(position: Int, count: Int) {
        RecyclerViewUtils.notifyItemRangeInserted(
            recyclerView, adapter, position, count
        )
    }

    override fun onRemoved(position: Int, count: Int) {
        RecyclerViewUtils.notifyItemRangeRemoved(
            recyclerView, adapter, position, count
        )
    }
}