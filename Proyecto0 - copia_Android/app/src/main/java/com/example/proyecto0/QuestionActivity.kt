package com.example.proyecto0

import Question
import QuestionsRequest
import QuestionsResponse
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : AppCompatActivity() {
    private lateinit var sessionId: String // Almacena el sessionId
    private lateinit var questionTextView: TextView
    private lateinit var questionImageView: ImageView
    private lateinit var optionButtons: List<Button>
    private lateinit var timerTextView: TextView // Añadido para el temporizador

    private var currentQuestionIndex = 0 // Índice de la pregunta actual
    private var correctAnswersCount = 0 // Contador de respuestas correctas
    private var totalQuestions = 0 // Total de preguntas

    private var preguntas: List<Question> = emptyList() // Almacena las preguntas obtenidas

    private lateinit var timerHandler: Handler
    private var timerRunnable: Runnable? = null
    private var elapsedTime: Long = 0 // Tiempo transcurrido en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Inicializar el Handler
        timerHandler = Handler()

        // Inicializar las vistas
        questionTextView = findViewById(R.id.questionTextView)
        questionImageView = findViewById(R.id.questionImageView)
        optionButtons = listOf(
            findViewById(R.id.option1Button),
            findViewById(R.id.option2Button),
            findViewById(R.id.option3Button),
            findViewById(R.id.option4Button)
        )
        timerTextView = findViewById(R.id.timerTextView) // Inicializar el TextView del temporizador

        // Obtener el sessionId y luego las preguntas
        initializeSessionAndFetchQuestions(10) // Obtener 10 preguntas
    }

    private fun initializeSessionAndFetchQuestions(num: Int) {
        // Solicitud para obtener un nuevo sessionId y preguntas
        val initialRequest = QuestionsRequest("", num) // sessionId inicial vacío

        val call = RetrofitClient.apiService.getQuestions(initialRequest)
        call.enqueue(object : Callback<QuestionsResponse> {
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                if (response.isSuccessful) {
                    // Almacenar el sessionId devuelto
                    sessionId = response.body()?.sessionId ?: ""

                    preguntas = response.body()?.preguntesSeleccionades ?: emptyList()
                    totalQuestions = preguntas.size // Almacenar total de preguntas
                    if (preguntas.isNotEmpty()) {
                        startTimer() // Iniciar el temporizador
                        displayQuestion(preguntas[currentQuestionIndex]) // Muestra la primera pregunta
                    } else {
                        showToast("No hay preguntas disponibles.")
                        finish() // Cierra la actividad si no hay preguntas
                    }
                } else {
                    Log.e("API_ERROR", "Error: ${response.errorBody()}")
                    showToast("Error al cargar preguntas. Intenta nuevamente.")
                }
            }

            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failed to get questions: ${t.message}")
                showToast("Error de conexión. Intenta nuevamente.")
            }
        })
    }

    private fun displayQuestion(question: Question) {
        // Actualiza la UI con la pregunta y opciones
        questionTextView.text = question.pregunta

        // Cargar la imagen si hay una URL válida usando Glide
        if (question.imatge.isNotEmpty()) {
            Glide.with(this)
                .load(question.imatge)
                .placeholder(R.drawable.placeholder) // Placeholder mientras carga
                .into(questionImageView)
        } else {
            questionImageView.setImageResource(R.drawable.placeholder) // Placeholder
        }

        // Mezclar las respuestas y asignarlas a los botones
        val allAnswers = question.respostes.shuffled() // Mezcla las respuestas
        optionButtons.forEachIndexed { index, button ->
            button.text = allAnswers[index]
            button.setOnClickListener {
                // Manejar la selección de respuesta
                handleAnswer(allAnswers[index], question.respostes.last()) // Compara con la respuesta correcta
            }
        }
    }

    private fun handleAnswer(selectedAnswer: String, correctAnswer: String) {
        if (selectedAnswer == correctAnswer) {
            Log.d("ANSWER_CHECK", "Respuesta correcta")
            correctAnswersCount++
        } else {
            Log.d("ANSWER_CHECK", "Respuesta incorrecta")
        }

        currentQuestionIndex++

        // Verificar si hay más preguntas
        if (currentQuestionIndex < preguntas.size) {
            displayQuestion(preguntas[currentQuestionIndex])
        } else {
            showResults()
        }
    }

    private fun showResults() {
        timerRunnable?.let { timerHandler.removeCallbacks(it) } // Detener el temporizador
        val timeTakenInSeconds = elapsedTime / 1000 // Convertir a segundos

        // Inicia ResultActivity y pasa los datos
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL_QUESTIONS", totalQuestions)
            putExtra("TIME_TAKEN", formatTime(timeTakenInSeconds)) // Formatear el tiempo
            putExtra("SUCCESSES", correctAnswersCount)
        }
        startActivity(intent)
        finish() // Cierra esta actividad
    }

    private fun startTimer() {
        elapsedTime = 0 // Reiniciar el tiempo al iniciar el juego

        timerRunnable = object : Runnable {
            override fun run() {
                elapsedTime += 1000 // Incrementar el tiempo en 1 segundo
                // Actualizar el TextView del tiempo
                val minutes = (elapsedTime / 1000) / 60
                val seconds = (elapsedTime / 1000) % 60
                timerTextView.text = String.format("Tiempo: %02d:%02d", minutes, seconds)

                timerHandler.postDelayed(this, 1000) // Volver a ejecutar el runnable cada segundo
            }
        }
        timerHandler.post(timerRunnable as Runnable) // Iniciar el runnable
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
