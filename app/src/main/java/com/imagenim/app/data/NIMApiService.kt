package com.imagenim.app.data

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class NIMApiService(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    private val baseUrl = "https://ai.api.nvidia.com/v1/genai"

    fun generateImage(model: ModelInfo, request: GenerateRequest): Result<NIMResponse> {
        return makeRequest(model, gson.toJson(request))
    }

    fun editImage(model: ModelInfo, request: EditRequest): Result<NIMResponse> {
        return makeRequest(model, gson.toJson(request))
    }

    private fun makeRequest(model: ModelInfo, jsonBody: String): Result<NIMResponse> {
        return try {
            val url = "$baseUrl/${model.publisher}/${model.modelName}"
            val body = jsonBody.toRequestBody(jsonMediaType)
            val httpRequest = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                val errorMsg = when (response.code) {
                    401 -> "Ungültiger API-Key"
                    429 -> "Rate-Limit erreicht. Bitte warten."
                    400 -> "Fehlerhafte Anfrage: $responseBody"
                    else -> "HTTP ${response.code}: $responseBody"
                }
                return Result.failure(IOException(errorMsg))
            }

            val nimResponse = gson.fromJson(responseBody, NIMResponse::class.java)
            if (nimResponse?.artifacts.isNullOrEmpty()) {
                return Result.failure(IOException("Keine Bilder empfangen"))
            }
            Result.success(nimResponse)
        } catch (e: Exception) {
            Result.failure(IOException("Netzwerkfehler: ${e.message}", e))
        }
    }
}
