package com.dsm.foro2_mp202814_cr202814

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsEventActivity : AppCompatActivity() {

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var recyclerViewComments: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_event)

        // Recuperar datos del evento
        val eventId = intent.getStringExtra("event_id") ?: ""

        // Configurar RecyclerView para los comentarios
        recyclerViewComments = findViewById(R.id.recyclerViewComments)
        commentsAdapter = CommentsAdapter(mutableListOf())
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentsAdapter

        // Llamar al endpoint para obtener los datos del evento
        fetchEventDetails(eventId)

        // Configurar botón "Participaré"
        findViewById<Button>(R.id.btnParticipate).setOnClickListener {
            Toast.makeText(this, "¡Acción de unirme al evento!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchEventDetails(eventId: String) {
        RetrofitClient.instance.getEventById(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful && response.body() != null) {
                    val event = response.body()!!

                    // Llenar datos del evento
                    findViewById<TextView>(R.id.tvEventTitle).text = event.nombre
                    findViewById<TextView>(R.id.tvEventDate).text = "${event.fecha} a las ${event.hora}"
                    findViewById<TextView>(R.id.tvEventLocation).text = event.ubicacion
                    findViewById<TextView>(R.id.tvEventDescription).text = event.descripcion
                    Log.d("DetailsEventActivity", "Comentarios recibidos: ${event.comments}")
                    // Llenar RecyclerView con los comentarios
                    commentsAdapter.updateComments(event.comments)
                } else {
                    Toast.makeText(this@DetailsEventActivity, "Error al cargar datos del evento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Log.e("DetailsEventActivity", "Error de conexión: ${t.message}")
                Toast.makeText(this@DetailsEventActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}