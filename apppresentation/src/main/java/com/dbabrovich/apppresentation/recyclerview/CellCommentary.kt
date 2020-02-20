package com.dbabrovich.apppresentation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dbabrovich.apppresentation.R
import com.dbabrovich.apppresentation.databinding.CellCommentaryBinding

object CellCommentary {

    val VIEW_ID = R.layout.cell_commentary

    /**
     * View model for cell commentary
     */
    class ViewModel(
        val comment: String,
        val period: Long,
        val time: String
    ) : BaseObservable(), RecyclerViewBase.ViewModelBase {

        override val renderWithViewTypeId: Int get() = VIEW_ID

        val commentText: CharSequence
            @Bindable get() {
                return "$period | $time | $comment"
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ViewModel

            if (comment != other.comment) return false
            if (period != other.period) return false
            if (time != other.time) return false

            return true
        }

        override fun hashCode(): Int {
            var result = comment.hashCode()
            result = 31 * result + period.hashCode()
            result = 31 * result + time.hashCode()
            return result
        }
    }

    private class ViewHolder(private val viewBinding: CellCommentaryBinding) :
        RecyclerViewBase.ViewHolderBase(viewBinding.root) {

        override fun bindAdapterData(data: RecyclerViewBase.ViewModelBase) {
            (data as ViewModel).also {
                viewBinding.data = data
            }
        }
    }

    /**
     * Factory class for a view holder
     */
    object Creator : RecyclerViewBase.ViewHolderBaseCreator<RecyclerViewBase.ViewHolderBase> {
        override fun createViewHolder(
            recycleView: ViewGroup,
            callback: RecyclerViewBase.ViewHolderCallback?
        ): RecyclerViewBase.ViewHolderBase {
            val viewBinding = CellCommentaryBinding.inflate(
                LayoutInflater.from(recycleView.context),
                recycleView,
                false
            )
            return ViewHolder(viewBinding)
        }
    }
}