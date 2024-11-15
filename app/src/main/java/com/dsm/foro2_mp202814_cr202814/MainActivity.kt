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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {

    lateinit var signUpButton: Button
    lateinit var loginButton: Button
    lateinit var googleButton: Button
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText

    private val GOOGLE_SIGN_IN = 100

//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        auth = Firebase.auth
        setup()
    }

    private fun setup(){
        title = "Autenticación"

        signUpButton = findViewById(R.id.logOutButton)
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
            Log.d("Testing", "Se dio click")
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                Log.d("LOL", "No están vacios")

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")

//                            val user = auth.currentUser
//                            Log.d("USER", user?.email.toString())
                            Log.d("SUCCESS", task.result.user?.email.toString())
                            showHome(task.result?.user?.email ?:"", ProviderType.BASIC)
                        } else {
                            Log.w("USER ERROR", "createUserWithEmail:failure", task.exception)
                            showAlert()
                        }
                    }

            }

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
                            showHome(task.result?.user?.email ?:"", ProviderType.BASIC)
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

    private fun showHome(email:String, provider:ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
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
                                showHome(task.result?.user?.email ?:"", ProviderType.GOOGLE)
                            } else {
                                Log.w("USER ERROR", "signInWithEmailAndPassword:failure", task.exception)
                                showAlert()
                            }
                        }
                }

            } catch (e: ApiException){
                Log.w("USER ERROR", e.message, task.exception)
                Toast.makeText(
                    baseContext,
                    "Catch" + e.message,
                    Toast.LENGTH_SHORT,
                ).show()
            }


        }
    }
}