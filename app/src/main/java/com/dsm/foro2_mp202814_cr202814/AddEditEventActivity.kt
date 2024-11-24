package com.dsm.foro2_mp202814_cr202814

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        // Configurar clic para abrir el DatePickerDialog
        etEventDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etEventDate.setText(dateFormat.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Configurar clic para abrir el TimePickerDialog
        etEventTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Convierte la hora en formato de 12 horas con AM/PM
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val hourIn12Format = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, minute, amPm)
                    etEventTime.setText(timeFormatted)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // Usa formato de 24 horas
            )
            timePicker.show()
        }

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