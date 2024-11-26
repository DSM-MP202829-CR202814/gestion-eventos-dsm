package com.dsm.foro2_mp202814_cr202814

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private val comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentDate: TextView = itemView.findViewById(R.id.tvCommentDate)
        private val commentText: TextView = itemView.findViewById(R.id.tvCommentText)
        private val commentAuthor: TextView = itemView.findViewById(R.id.tvCommentAuthor)

        fun bind(comment: Comment) {
            commentDate.text = comment.fecha
            commentText.text = comment.comentario
            commentAuthor.text = comment.displayName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        Log.d("CommentsAdapter", "Dibujando comentario: ${comments[position]}")
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        Log.d("CommentsAdapter", "Actualizando comentarios: $newComments")
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}
