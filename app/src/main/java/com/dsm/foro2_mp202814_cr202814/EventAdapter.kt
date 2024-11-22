package com.dsm.foro2_mp202814_cr202814

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class EventAdapter(
    private val events: List<Event>,
    private val onEventClick: (Event) -> Unit // Callback para manejar clics
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder para manejar cada item
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val tvEventDate: TextView = itemView.findViewById(R.id.tvEventDate)

        fun bind(event: Event, onEventClick: (Event) -> Unit) {
            tvEventTitle.text = event.title
            tvEventDate.text = event.date
            itemView.setOnClickListener { onEventClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], onEventClick)
    }

    override fun getItemCount() = events.size
}