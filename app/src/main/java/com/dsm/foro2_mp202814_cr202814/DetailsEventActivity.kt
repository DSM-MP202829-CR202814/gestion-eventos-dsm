package com.dsm.foro2_mp202814_cr202814

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailsEventActivity : AppCompatActivity() {

    lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_event)

        btnRegresar = findViewById(R.id.btnRegresar)

        // Recuperar datos del evento
        val eventId = intent.getStringExtra("event_id")
        val eventNombre = intent.getStringExtra("event_nombre") ?: ""
        val eventFecha = intent.getStringExtra("event_fecha") ?: ""
        val eventHora = intent.getStringExtra("event_hora") ?: ""
        val eventUbicacion = intent.getStringExtra("event_ubicacion") ?: ""
        val eventDescripcion = intent.getStringExtra("event_descripcion") ?: ""

        // Asignar datos a los campos de la UI
        findViewById<TextView>(R.id.tvEventTitle).text = eventNombre
        findViewById<TextView>(R.id.tvEventDate).text = eventFecha
        findViewById<TextView>(R.id.tvEventTime).text = eventHora
        findViewById<TextView>(R.id.tvEventLocation).text = eventUbicacion
        findViewById<TextView>(R.id.tvEventDescription).text = eventDescripcion

        // Configurar botón "Participaré"
        findViewById<Button>(R.id.btnParticipate).setOnClickListener {
            Toast.makeText(this, "¡Acción de unirme al evento!", Toast.LENGTH_SHORT).show()
        }

        btnRegresar.setOnClickListener {
            finish() // Cierra la actividad
        }
    }
}