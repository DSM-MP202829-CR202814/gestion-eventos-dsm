package com.dsm.foro2_mp202814_cr202814

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddEditEventActivity : AppCompatActivity() {
    lateinit var btnSaveEvent: Button
    lateinit var etEventTitle: EditText
    lateinit var etEventDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_event)

        btnSaveEvent = findViewById(R.id.btnSaveEvent)
        etEventTitle = findViewById(R.id.etEventTitle)
        etEventDate = findViewById(R.id.etEventDate)

        // Recuperar datos si se están editando
        val eventTitle = intent.getStringExtra("event_title") ?: ""
        val eventDate = intent.getStringExtra("event_date") ?: ""

        etEventTitle.setText(eventTitle)
        etEventDate.setText(eventDate)

        // Guardar evento
        btnSaveEvent.setOnClickListener {
            // Aquí agregarías lógica para guardar el evento en la base de datos o lista
            finish() // Vuelve a la lista
        }
    }
}