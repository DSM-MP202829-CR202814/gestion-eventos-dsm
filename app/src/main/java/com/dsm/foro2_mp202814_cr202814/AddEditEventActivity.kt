package com.dsm.foro2_mp202814_cr202814

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditEventActivity : AppCompatActivity() {
    lateinit var btnSaveEvent: Button
    lateinit var btnCancel: Button

    lateinit var etEventTitle: EditText
    lateinit var etEventDate: EditText
    lateinit var etEventTime: EditText
    lateinit var etEventLocation: EditText
    lateinit var etEventDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_event)

        title = "Detalles del evento"

        btnSaveEvent = findViewById(R.id.btnSaveEvent)
        btnCancel = findViewById(R.id.btnCancel)

        etEventTitle = findViewById(R.id.etEventTitle)
        etEventDate = findViewById(R.id.etEventDate)
        etEventTime = findViewById(R.id.etEventTime)
        etEventLocation = findViewById(R.id.etEventLocation)
        etEventDescription = findViewById(R.id.etEventDescription)

        // Recuperar datos si se están editando
        val eventId = intent.getStringExtra("event_id")
        val eventNombre = intent.getStringExtra("event_nombre") ?: ""
        val eventFecha = intent.getStringExtra("event_fecha") ?: ""
        val eventHora = intent.getStringExtra("event_hora") ?: ""
        val eventUbicacion = intent.getStringExtra("event_ubicacion") ?: ""
        val eventDescripcion = intent.getStringExtra("event_descripcion") ?: ""

        etEventTitle.setText(eventNombre)
        etEventDate.setText(eventFecha)
        etEventTime.setText(eventHora)
        etEventLocation.setText(eventUbicacion)
        etEventDescription.setText(eventDescripcion)

        // Guardar evento
        btnSaveEvent.setOnClickListener {
            // lógica para guardar el evento en la base de datos
            val nombre = etEventTitle.text.toString()
            val fecha = etEventDate.text.toString()
            val hora = etEventTime.text.toString()
            val ubicacion = etEventLocation.text.toString()
            val descripcion = etEventDescription.text.toString()

            if (eventId == null) {
                createEvent(Event(nombre = nombre, fecha = fecha, hora = hora, ubicacion = ubicacion, descripcion = descripcion))
            } else {
                updateEvent(eventId, Event(id = eventId, nombre = nombre, fecha = fecha, hora = hora, ubicacion = ubicacion, descripcion = descripcion))
            }
        }

        btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED) // Notifica que no hubo cambios
            finish() // Cierra la actividad
        }
    }

    private fun createEvent(event: Event) {
        RetrofitClient.instance.createEvent(event).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditEventActivity, "Evento creado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Notifica que hubo un cambio
                    finish()
                } else {
                    Toast.makeText(this@AddEditEventActivity, "Error al crear evento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Toast.makeText(this@AddEditEventActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEvent(id: String, event: Event) {
        RetrofitClient.instance.updateEvent(id, event).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditEventActivity, "Evento actualizado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Notifica que hubo un cambio
                    finish()
                } else {
                    Toast.makeText(this@AddEditEventActivity, "Error al actualizar evento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Toast.makeText(this@AddEditEventActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

}