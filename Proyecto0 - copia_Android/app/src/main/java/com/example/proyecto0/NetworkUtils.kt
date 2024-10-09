package com.example.proyecto0 // Cambia esto según tu paquete

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    private const val TAG = "NetworkUtils"

    fun fetchQuestions(): JSONObject? {
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        return try {
            val url = URL("http://a23brioropoy.dam.inspedralbes.cat:26984/preguntes")
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = InputStreamReader(urlConnection.inputStream)
            reader = BufferedReader(inputStream)
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            JSONObject(response.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions", e)
            null
        } finally {
            urlConnection?.disconnect()
            reader?.close()
        }
    }
}
