package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EventListActivity : AppCompatActivity() {
    private val events = mutableListOf<Event>()

    lateinit var recyclerViewEvents: RecyclerView
    lateinit var btnAddEvent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        // Crear eventos de ejemplo
        events.add(Event("Conferencia de Tecnología", "25/11/2024"))
        events.add(Event("Reunión de Proyecto", "26/11/2024"))

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents)
        btnAddEvent = findViewById(R.id.btnAddEvent)

        // Configurar RecyclerView
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        recyclerViewEvents.adapter = EventAdapter(events) { event ->
            // Acción al hacer clic en un evento
            val intent = Intent(this, AddEditEventActivity::class.java)
            intent.putExtra("event_title", event.title)
            intent.putExtra("event_date", event.date)
            startActivity(intent)
        }

        // Botón para agregar un nuevo evento
        btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEditEventActivity::class.java))
        }
    }
}