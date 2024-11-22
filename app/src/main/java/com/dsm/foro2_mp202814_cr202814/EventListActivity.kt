package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventListActivity : AppCompatActivity() {
    private val events = mutableListOf<Event>()

    private lateinit var adapter: EventAdapter

    lateinit var recyclerViewEvents: RecyclerView
    lateinit var btnAddEvent: Button

    private val addEditEventLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchEvents() // Recarga la lista de eventos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        title = "Listado de eventos"

        adapter = EventAdapter(mutableListOf()) { event ->
            val intent = Intent(this, AddEditEventActivity::class.java)
            intent.putExtra("event_id", event.id)
            intent.putExtra("event_nombre", event.nombre)
            intent.putExtra("event_fecha", event.fecha)
            intent.putExtra("event_hora", event.hora)
            intent.putExtra("event_ubicacion", event.ubicacion)
            intent.putExtra("event_descripcion", event.descripcion)
            addEditEventLauncher.launch(intent)
        }

        // Crear eventos de ejemplo
//        events.add(Event("Conferencia de Tecnología", "25/11/2024"))
//        events.add(Event("Reunión de Proyecto", "26/11/2024"))

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents)
        btnAddEvent = findViewById(R.id.btnAddEvent)

        // Configurar RecyclerView
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        recyclerViewEvents.adapter = adapter

        // Botón para agregar un nuevo evento
        btnAddEvent.setOnClickListener {
            val intent = Intent(this, AddEditEventActivity::class.java)
            addEditEventLauncher.launch(intent) // Usa el launcher
        }

        fetchEvents()
    }

    private fun fetchEvents() {
        RetrofitClient.instance.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        adapter.updateEvents(it)
                    }
                } else {
                    Toast.makeText(this@EventListActivity, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Log.e("API_ERROR", t.toString())
                Toast.makeText(this@EventListActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}