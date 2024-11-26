package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

class DetailsEventActivity : AppCompatActivity() {

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var recyclerViewComments: RecyclerView

    private lateinit var btnParticipate: Button
    private lateinit var btnAddComment: Button
    private lateinit var tvAlreadyConfirmed: TextView
    private lateinit var participantsText: TextView

    private var usersCount: Int = 0 // Para manejar el conteo temporal
    private var eventId: String = ""

    // Configurar el ActivityResultLauncher
    private val addCommentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchComments() // Recarga la lista de comentarios
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_event)

        // Recuperar datos del evento
        eventId = intent.getStringExtra("event_id") ?: ""
//        val userId = intent.getStringExtra("user_id") ?: "" // Recibir el ID del usuario desde el Intent
        val userId = "uGjpbZDgfWqtf98dGFDW" // Recibir el ID del usuario desde el Intent

        // Configurar RecyclerView para los comentarios
        recyclerViewComments = findViewById(R.id.recyclerViewComments)
        commentsAdapter = CommentsAdapter(mutableListOf())
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentsAdapter

        // Elementos de la interfaz
        btnParticipate = findViewById(R.id.btnParticipate)
        btnAddComment = findViewById(R.id.btnAddComment)
        tvAlreadyConfirmed = findViewById(R.id.tvAlreadyConfirmed)
        participantsText = findViewById(R.id.participantsText)

        // Llamar al endpoint para obtener los datos del evento
        fetchEventDetails(eventId)

        // Configurar botón "Participaré"
        btnParticipate.setOnClickListener {
            participateInEvent(userId, eventId)
        }

        btnAddComment.setOnClickListener {
            navigateToAddComment()
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

                    // Actualizar el conteo de participantes
                    usersCount = event.usersCount
                    participantsText.text = "$usersCount personas han confirmado su asistencia al evento"

                    // Llenar RecyclerView con los comentarios
                    commentsAdapter.updateComments(event.comments)
                    recyclerViewComments.invalidate() // Forzar redibujado

                    recyclerViewComments.post {
                        commentsAdapter.notifyDataSetChanged()
                    }
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

    private fun participateInEvent(userId: String, eventId: String) {
        RetrofitClient.userApi.attendEvent(userId, eventId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Actualizar UI después de participar
                    btnParticipate.visibility = View.GONE
                    tvAlreadyConfirmed.visibility = View.VISIBLE

                    // Actualizar el conteo temporal de participantes
                    usersCount++
                    participantsText.text = "$usersCount personas han confirmado su asistencia al evento"

                    Toast.makeText(this@DetailsEventActivity, "¡Confirmaste tu asistencia!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DetailsEventActivity, "Error al confirmar asistencia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DetailsEventActivity", "Error al confirmar: ${t.message}")
                Toast.makeText(this@DetailsEventActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchComments() {
        // Solo actualizamos los comentarios
        RetrofitClient.instance.getEventById(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful && response.body() != null) {
                    val event = response.body()!!
                    commentsAdapter.updateComments(event.comments)
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Log.e("DetailsEventActivity", "Error de conexión: ${t.message}")
            }
        })
    }

    private fun navigateToAddComment() {
        val intent = Intent(this, AddCommentActivity::class.java).apply {
            putExtra("event_id", eventId)
            putExtra("event_title", findViewById<TextView>(R.id.tvEventTitle).text.toString())
        }
        addCommentLauncher.launch(intent)
    }
}