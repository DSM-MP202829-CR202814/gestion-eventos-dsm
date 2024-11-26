package com.dsm.foro2_mp202814_cr202814

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var signUpButton: Button
    lateinit var loginButton: Button
    lateinit var googleButton: SignInButton
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is already signed in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // If user is signed in, go to HomeActivity
            showHome(currentUser.email ?: "")
        } else {
            // Set up the login and register actions if the user is not signed in
            setup()
        }
    }

    private fun setup(){
        title = "Autenticación"

        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        googleButton = findViewById(R.id.googleButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        signIn()
        login()
        google()
    }

    private fun signIn(){
        signUpButton.setOnClickListener {
            val signInIntent = Intent(this, RegisterActivity::class.java)
            startActivity(signInIntent)
        }
    }

    private fun login(){
        loginButton.setOnClickListener {
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful){
                            Log.d("SUCCESS LOGIN", task.result.user?.email.toString())
                            showHome(task.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {
        // Llamar al endpoint para obtener el usuario por email
        RetrofitClient.userApi.getUserByEmail(email).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!

                    // Save user data to SharedPreferences for later use
                    saveUserData(userResponse)

                    // Redirigir al HomeActivity con los datos del usuario
                    val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData(user: User) {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("idUser", user.id)
        editor.putString("nombres", user.nombres)
        editor.putString("apellidos", user.apellidos)
        editor.putString("email", user.email)
        editor.putBoolean("isAdmin", user.isAdmin)
        editor.apply()
    }

    private fun google(){
        googleButton.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful){
                                Log.d("SUCCESS LOGIN GOOGLE", account.email.toString())
                                showHome(task.result?.user?.email ?: "")
                            } else {
                                Log.w("USER ERROR", "signInWithEmailAndPassword:failure", task.exception)
                                showAlert()
                            }
                        }
                }

            } catch (e: ApiException){
                Log.w("USER ERROR", e.message, task.exception)
                Toast.makeText(baseContext, "Catch" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
