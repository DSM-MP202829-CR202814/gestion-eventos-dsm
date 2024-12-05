package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventListActivity : AppCompatActivity() {
    private lateinit var adapter: EventAdapter

    lateinit var recyclerViewEvents: RecyclerView
    lateinit var btnAddEvent: Button
    lateinit var headerTitle: TextView
    private var isPastEvents: Boolean = false

    private val addEditEventLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchEvents(isPastEvents) // Recarga la lista de eventos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        title = "Listado de eventos"

        // Recibir el valor de ADMIN desde el Intent
        val isAdmin = intent.getBooleanExtra("ADMIN", false)
        isPastEvents = intent.getBooleanExtra("isPastEvents", false) // Recibimos la bandera

        headerTitle = findViewById(R.id.headerTitle)

        if (isPastEvents) {
            headerTitle.text = "Eventos Pasados"
        } else {
            headerTitle.text = "Eventos Próximos"
        }

        adapter = EventAdapter(mutableListOf(), isAdmin) { event ->
            if (isAdmin) {
                // Redirección a AddEditEventActivity
                val intent = Intent(this, AddEditEventActivity::class.java)
                intent.putExtra("event_id", event.id)
                intent.putExtra("event_nombre", event.nombre)
                intent.putExtra("event_fecha", event.fecha)
                intent.putExtra("event_hora", event.hora)
                intent.putExtra("event_ubicacion", event.ubicacion)
                intent.putExtra("event_descripcion", event.descripcion)
                addEditEventLauncher.launch(intent)
            } else {
                // Redirección a DetailsEventActivity
                val intent = Intent(this, DetailsEventActivity::class.java)
                intent.putExtra("event_id", event.id)
                startActivity(intent)
            }
        }

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents)
        btnAddEvent = findViewById(R.id.btnAddEvent)

        // Configurar RecyclerView
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        recyclerViewEvents.adapter = adapter

        Log.e("ADMIN", isAdmin.toString())

        // Controlar la visibilidad del botón
        if (isAdmin) {
            btnAddEvent.visibility = Button.VISIBLE
        } else {
            btnAddEvent.visibility = Button.GONE
        }

        // Configurar acción del botón para agregar un evento
        btnAddEvent.setOnClickListener {
            val intent = Intent(this, AddEditEventActivity::class.java)
            addEditEventLauncher.launch(intent)
        }

        fetchEvents(isPastEvents)
    }


    private fun fetchEvents(isPastEvents: Boolean) {
        RetrofitClient.instance.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        val currentDate = getCurrentDate()  // Fecha actual
                        val eventDateFormats = arrayOf(
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()), // Formato para eventos
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Formato ISO
                        )

                        val currentDateParsed = parseDate(currentDate, eventDateFormats)

                        // Filtrar eventos según la bandera
                        val filteredEvents = if (isPastEvents) {
                            // Filtrar eventos pasados
                            events.filter { event ->
                                val eventDate = parseDate(event.fecha, eventDateFormats)
                                eventDate?.before(currentDateParsed) ?: false // Pasados
                            }.sortedByDescending { event ->
                                parseDate(event.fecha, eventDateFormats) ?: Date(0) // Los más recientes
                            }
                        } else {
                            // Filtrar eventos futuros
                            events.filter { event ->
                                val eventDate = parseDate(event.fecha, eventDateFormats)
                                eventDate?.after(currentDateParsed) ?: false // Futuros
                            }.sortedBy { event ->
                                parseDate(event.fecha, eventDateFormats) ?: Date(0) // Los más próximos
                            }
                        }

                        // Actualizar los eventos en el adaptador
                        adapter.updateEvents(filteredEvents)
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

    private fun getCurrentDate(): String {
        // Retorna la fecha actual en formato "yyyy-MM-dd" para comparación
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun parseDate(dateString: String, formats: Array<SimpleDateFormat>): Date? {
        for (format in formats) {
            try {
                return format.parse(dateString)
            } catch (e: Exception) {
                // Ignorar y seguir probando con otros formatos
            }
        }
        return null // Si no se puede parsear con ninguno de los formatos, retorna null
    }
}