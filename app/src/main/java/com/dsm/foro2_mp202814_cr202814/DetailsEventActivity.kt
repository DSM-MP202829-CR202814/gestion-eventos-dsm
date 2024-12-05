package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.gson.Gson
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

        // Obtener las SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", null)

        // Recuperar datos del evento
        eventId = intent.getStringExtra("event_id") ?: ""

        // Recuperar eventos del usuario
        val eventsJson = sharedPreferences.getString("events", "[]")
        val userEvents: MutableList<String> = Gson().fromJson(eventsJson, Array<String>::class.java).toMutableList()

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

        // Ocultar botón "Participaré" si ya está inscrito
        if (userEvents.contains(eventId)) {
            btnParticipate.visibility = View.GONE
            tvAlreadyConfirmed.visibility = View.VISIBLE
            btnAddComment.visibility = View.VISIBLE // Habilitar el botón "Comentar"
        } else {
            btnAddComment.visibility = View.GONE // Deshabilitar si no está confirmado
        }

        // Llamar al endpoint para obtener los datos del evento
        fetchEventDetails(eventId)

        // Configurar botón "Participaré"
        btnParticipate.setOnClickListener {
            if (userId != null) {
                participateInEvent(userId, eventId, sharedPreferences, userEvents)
            }
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

    private fun participateInEvent(userId: String, eventId: String, sharedPreferences: SharedPreferences, userEvents: MutableList<String>) {
        RetrofitClient.userApi.attendEvent(userId, eventId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Actualizar UI después de participar
                    btnParticipate.visibility = View.GONE
                    tvAlreadyConfirmed.visibility = View.VISIBLE

                    // Actualizar el conteo temporal de participantes
                    usersCount++
                    participantsText.text = "$usersCount personas han confirmado su asistencia al evento"

                    // Actualizar la lista de eventos del usuario y guardar en SharedPreferences
                    userEvents.add(eventId)
                    val editor = sharedPreferences.edit()
                    editor.putString("events", Gson().toJson(userEvents))
                    editor.apply()

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
