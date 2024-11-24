package com.dsm.foro2_mp202814_cr202814

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
    GOOGLE
}


class HomeActivity : AppCompatActivity() {

    lateinit var emailTextView: TextView
    lateinit var providerTextView: TextView
    lateinit var logOutButton: Button
    lateinit var goEventsButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?:"", provider ?:"")
    }

    private fun setup(email:String, provider:String){
        title = "Inicio"

        emailTextView = findViewById(R.id.emailTextView)
        providerTextView = findViewById(R.id.providerTextView)
        logOutButton = findViewById(R.id.logOutButton)
        goEventsButton = findViewById(R.id.goEventsButton)

        emailTextView.text = email
        providerTextView.text = provider

        logOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        goEventsButton.setOnClickListener {
            goEvents()
        }
    }

    private fun goEvents(){
        val intent = Intent(this, EventListActivity::class.java)
        intent.putExtra("ADMIN", false) // Cambia a false si no es admin
        startActivity(intent)
    }

}