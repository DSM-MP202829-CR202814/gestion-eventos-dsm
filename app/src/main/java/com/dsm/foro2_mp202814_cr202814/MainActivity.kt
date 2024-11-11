package com.dsm.foro2_mp202814_cr202814

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    lateinit var signUpButton: Button
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText

//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        auth = Firebase.auth
        setup()
    }

    private fun setup(){
        title = "Autenticación"

        signUpButton = findViewById(R.id.signUpButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

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
                        } else {
                            Log.w("USER ERROR", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }

            }

        }
    }
}