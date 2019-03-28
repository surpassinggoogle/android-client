package io.golos.cyber_android.ui.common.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.AbstractDiscussionModelAdapter
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.model.CommentModel
import kotlinx.android.synthetic.main.item_comment.view.*


/**
 * [RecyclerView.Adapter] for [PostEntity]
 */

abstract class CommentsAdapter(private var values: List<CommentModel>, private val listener: Listener) :
    AbstractDiscussionModelAdapter<CommentModel>() {

    override fun submit(list: List<CommentModel>) {
        val diff = DiffUtil.calculateDiff(CommentsDiffCallback(values, list))
        values = list
        dispatchUpdates(diff)
    }

    abstract fun dispatchUpdates(diff: DiffUtil.DiffResult)

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = values[position]
        holder as CommentViewHolder
        holder.bind(post, listener)
    }

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            commentModel: CommentModel,
            listener: Listener
        ) {
            with(itemView) {
                //todo commentAvatar
                //todo commentRating
                commentRating.text = "42"
                commentAuthorName.text = commentModel.author.username
                commentDate.text = DateUtils.createTimeLabel(
                    commentModel.meta.time.time,
                    commentModel.meta.elapsedFormCreation.elapsedMinutes,
                    commentModel.meta.elapsedFormCreation.elapsedHours,
                    commentModel.meta.elapsedFormCreation.elapsedDays,
                    context
                )
                commentContent.text = commentModel.content.body.full
                commentReply.setOnClickListener {
                    listener.onReplyClick(commentModel)
                }
                bindVoteButtons(commentModel, this)
            }
        }

        private fun bindVoteButtons(commentModel: CommentModel, view: View) {
            with(view) {
                commentUpvote.isActivated = commentModel.votes.hasUpVote
                commentDownvote.isActivated = commentModel.votes.hasDownVote

                commentDownvoteProgress.visibility =
                    if (commentModel.votes.hasDownVotingProgress || commentModel.votes.hasVoteCancelProgress && commentModel.votes.hasDownVote)
                        View.VISIBLE
                    else View.GONE

                commentUpvoteProgress.visibility =
                    if (commentModel.votes.hasUpVoteProgress || commentModel.votes.hasVoteCancelProgress && commentModel.votes.hasUpVote)
                        View.VISIBLE
                    else View.GONE
            }
        }
    }

    interface Listener {

        fun onReplyClick(comment: CommentModel)

        fun onUpvoteClick(comment: CommentModel)

        fun onDownvoteClick(comment: CommentModel)
    }

}