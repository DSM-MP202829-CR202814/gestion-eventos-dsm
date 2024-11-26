package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var adapterNextEvents: EventAdapter
    private lateinit var adapterPastEvents: EventAdapter

    lateinit var recyclerViewNextEvents: RecyclerView
    lateinit var recyclerViewPastEvents: RecyclerView
    lateinit var btnLogOut: Button
    lateinit var txtWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val isAdmin = bundle?.getBoolean("isAdmin") ?: false
        setup(email ?: "", isAdmin)

        title = "Inicio"

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

        // Fetch events
        fetchEvents()
    }

    private fun setup(email: String, isAdmin: Boolean) {
        // You can use the email and isAdmin if needed, such as for showing different content
    }

    private fun fetchEvents() {
        RetrofitClient.instance.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        // For testing, set the same data for both sections
                        adapterNextEvents.updateEvents(events)  // All events as next events
                        adapterPastEvents.updateEvents(events)  // All events as past events
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
        val intent = Intent(this, DetailsEventActivity::class.java)
        intent.putExtra("event_id", event.id)
        intent.putExtra("event_nombre", event.nombre)
        intent.putExtra("event_fecha", event.fecha)
        intent.putExtra("event_hora", event.hora)
        intent.putExtra("event_ubicacion", event.ubicacion)
        intent.putExtra("event_descripcion", event.descripcion)
        startActivity(intent)
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
        // Return current date in the same format as "fecha" (for comparison)
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }
}
