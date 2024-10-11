
data class Question(
    val pregunta: String,
    val respostes: List<String>,
    val imatge: String
)

data class QuestionsResponse(
    val sessionId: String,
    val preguntesSeleccionades: List<Question>
)
data class QuestionsRequest(
    val sessionId: String,
    val num: Int
)