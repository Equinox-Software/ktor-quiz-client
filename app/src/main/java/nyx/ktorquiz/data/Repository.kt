package nyx.ktorquiz.data

import android.util.Log
import io.ktor.client.request.*
import nyx.ktorquiz.Status
import nyx.ktorquiz.model.Question
import nyx.ktorquiz.network.client

object Repository {

    private val TAG = "Repository"

    suspend fun getQuestions() = try {
        Status.Success(client.get<List<Question>>("https://ktor-quiz-backend.herokuapp.com/questions"))
    } catch (e: Exception) {
        Log.getStackTraceString(e)
        Status.Failure(e)
    }
}