package com.example.proyecto0

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia la actividad de inicio
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}
