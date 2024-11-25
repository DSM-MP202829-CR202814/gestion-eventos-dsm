package com.dsm.foro2_mp202814_cr202814

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var namesEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Vincular vistas
        namesEditText = findViewById(R.id.namesEditTextReg)
        lastNameEditText = findViewById(R.id.apellidosEditTextReg)
        emailEditText = findViewById(R.id.emailEditTextReg)
        passwordEditText = findViewById(R.id.passwordEditTextReg)
        registerButton = findViewById(R.id.registerBtn)
        backToLoginButton = findViewById(R.id.btnRegresarLogin)

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            registerUser()
        }

        // Configurar el botón para regresar al login
        backToLoginButton.setOnClickListener {
            finish()
        }



    }

    private fun registerUser() {
        val nombres = namesEditText.text.toString().trim()
        val apellidos = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validar campos
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto User
        val user = User(
            nombres = nombres,
            apellidos = apellidos,
            email = email,
            isAdmin = false // Cambia esto si deseas permitir admins desde el registro
        )

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")

                    Log.d("SUCCESS", task.result.user?.email.toString())

                    saveUser(user)
                } else {
                    Log.w("USER ERROR", "createUserWithEmail:failure", task.exception)
                    showAlert()
                }
            }
    }

    private fun saveUser(user: User){
        // Hacer POST a la API
        RetrofitClient.userApi.registerUser(user).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("TAG", response.body()!!.id)
                if (response.isSuccessful  && response.body() != null) {
                    val registerResponse = response.body()!!
                    val userId = registerResponse.id // Leer el ID del usuario creado

                    Toast.makeText(this@RegisterActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    // Redirigir al HomeActivity
                    val homeIntent = Intent(this@RegisterActivity, HomeActivity::class.java).apply {
                        putExtra("idUser", userId )
                        putExtra("isAdmin", false)
                    }
                    startActivity(homeIntent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}