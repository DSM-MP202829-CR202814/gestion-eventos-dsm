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
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var adapterNextEvents: EventAdapter
    private lateinit var adapterPastEvents: EventAdapter

    lateinit var recyclerViewNextEvents: RecyclerView
    lateinit var recyclerViewPastEvents: RecyclerView
    lateinit var btnLogOut: Button
    lateinit var txtWelcome: TextView
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        val email = sharedPreferences.getString("email", "")
        isAdmin = sharedPreferences.getBoolean("isAdmin", false)

        title = "Inicio"

        // Configuración de los botones
        val verEventosProximosButton: Button = findViewById(R.id.verEventosProximos)
        val verEventosPasadosButton: Button = findViewById(R.id.verEventosPasados)

        // Initialize adapters
        adapterNextEvents = EventAdapter(mutableListOf(), false) { event ->
            // Handle event click for "Próximos" section
            navigateToEventDetails(event)
        }

        adapterPastEvents = EventAdapter(mutableListOf(), false) { event ->
            // Handle event click for "Pasados" section
            navigateToEventDetails(event)
        }

        // Find views
        recyclerViewNextEvents = findViewById(R.id.recyclerViewNextEvents)
        recyclerViewPastEvents = findViewById(R.id.recyclerViewPastEvents)
        btnLogOut = findViewById(R.id.btnLogOut)
        txtWelcome = findViewById(R.id.txtWelcome)

        // Set up RecyclerViews
        recyclerViewNextEvents.layoutManager = LinearLayoutManager(this)
        recyclerViewNextEvents.adapter = adapterNextEvents

        recyclerViewPastEvents.layoutManager = LinearLayoutManager(this)
        recyclerViewPastEvents.adapter = adapterPastEvents

        // Set the user's name dynamically (this is a placeholder)
        txtWelcome.text = "Bienvenido ${email ?: "Usuario"}"

        // Log out button click handler
        btnLogOut.setOnClickListener {
            logOutUser()
        }

        verEventosProximosButton.setOnClickListener {
            val intent = Intent(this, EventListActivity::class.java)
            intent.putExtra("isPastEvents", false)  // Pasar la bandera para eventos futuros
            intent.putExtra("ADMIN", isAdmin)
            startActivity(intent)
        }

        verEventosPasadosButton.setOnClickListener {
            val intent = Intent(this, EventListActivity::class.java)
            intent.putExtra("isPastEvents", true)  // Pasar la bandera para eventos pasados
            intent.putExtra("ADMIN", isAdmin)
            startActivity(intent)
        }


        // Fetch events
        fetchEvents()
    }

    private val addEditEventLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchEvents() // Recarga la lista de eventos
        }
    }

    private fun fetchEvents() {
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

                        // Filtrar eventos futuros (Próximos)
                        val futureEvents = events.filter { event ->
                            val eventDate = parseDate(event.fecha, eventDateFormats)
                            eventDate?.after(currentDateParsed) ?: false // Futuros
                        }.sortedBy { event ->
                            parseDate(event.fecha, eventDateFormats) ?: Date(0) // Si no se puede parsear, asignamos una fecha mínima
                        }.take(2) // Los dos más próximos

                        // Filtrar eventos pasados (Eventos Pasados)
                        val pastEvents = events.filter { event ->
                            val eventDate = parseDate(event.fecha, eventDateFormats)
                            eventDate?.before(currentDateParsed) ?: false // Pasados
                        }.sortedByDescending { event ->
                            parseDate(event.fecha, eventDateFormats) ?: Date(0) // Si no se puede parsear, asignamos una fecha mínima
                        }.take(2) // Los dos más recientes

                        // Actualizar los adaptadores
                        adapterNextEvents.updateEvents(futureEvents)
                        adapterPastEvents.updateEvents(pastEvents)
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@HomeActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToEventDetails(event: Event) {

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

    private fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
        // Redirect to login screen or previous activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity
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
