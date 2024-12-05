package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddCommentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_comment)

        // Recuperar datos del evento
        val eventId = intent.getStringExtra("event_id") ?: ""
        val eventTitle = intent.getStringExtra("event_title") ?: ""

        // Configurar título del evento
        findViewById<TextView>(R.id.tvCommentEventTitle).text = eventTitle

        // Configurar botón Guardar
        val btnSaveComment = findViewById<Button>(R.id.btnSaveComment)
        val etWriteComment = findViewById<EditText>(R.id.etWriteComment)

        btnSaveComment.setOnClickListener {
            val commentText = etWriteComment.text.toString()

            if (commentText.isEmpty()) {
                Toast.makeText(this, "Por favor escribe un comentario", Toast.LENGTH_SHORT).show()
            } else {
                saveComment(eventId, commentText)
            }
        }
    }

    private fun saveComment(eventId: String, commentText: String) {
        // Obtener las SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val nombres = sharedPreferences.getString("nombres", null)
        val apellidos = sharedPreferences.getString("apellidos", null)

        val comment = Comment(
            displayName = "$nombres $apellidos", // Reemplazar con el nombre del usuario logueado
            fecha = getCurrentDate(),
            comentario = commentText
        )

        RetrofitClient.instance.addComment(eventId, comment).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddCommentActivity, "Comentario guardado", Toast.LENGTH_SHORT).show()

                    // Regresar con RESULT_OK
                    setResult(RESULT_OK)
                    finish() // Finalizar esta actividad
                } else {
                    Toast.makeText(this@AddCommentActivity, "Error al guardar el comentario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddCommentActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) // Ejemplo: 25 noviembre 2024
        return dateFormat.format(Date())
    }
}
