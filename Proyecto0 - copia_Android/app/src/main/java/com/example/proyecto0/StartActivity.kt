package com.example.proyecto0

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView // Asegúrate de importar esto
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class StartActivity : AppCompatActivity() {

    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Inicializar el botón
        playButton = findViewById(R.id.playButton)

        // Configurar el listener del botón
        playButton.setOnClickListener {
            val intent = Intent(this, QuestionActivity::class.java)
            startActivity(intent)
        }

        // Cargar el GIF
        val gifImageView = findViewById<ImageView>(R.id.gifImageView)
        Glide.with(this)
            .asGif()
            .load("https://media.tenor.com/_3qDS9NqpVsAAAAj/rem-ram.gif") // Cambia esto por la URL del GIF
            .into(gifImageView)

        // Animar el GIF
        animateGif(gifImageView)
    }

    private fun animateGif(gifImageView: ImageView) {
        val animator = ObjectAnimator.ofFloat(gifImageView, "translationX", 0f, 200f) // Mueve el GIF de 0 a 200 píxeles
        animator.duration = 2000 // Duración de la animación
        animator.repeatCount = ObjectAnimator.INFINITE // Repite infinitamente
        animator.repeatMode = ObjectAnimator.REVERSE // Vuelve a la posición inicial
        animator.start() // Inicia la animación
    }
}
