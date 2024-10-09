
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/getPreguntes")
    fun getQuestions(@Body request: QuestionsRequest): Call<QuestionsResponse>

    @POST("/finalista") // Añadir este método
    fun submitAnswers(@Body request: FinalAnswersRequest): Call<FinalAnswersResponse>
}

