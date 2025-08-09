package com.fit2081.bryan_34309861_a3_app.data.repository

import android.content.Context
import com.fit2081.bryan_34309861_a3_app.data.api.PicsumApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PicsumApiRepository(context: Context) {

    /**
     * Retrofit instance configured for API communication
     * - Uses the base URL for the picsum API service
     * - Configures Gson converter for JSON parsing
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * API service interface implementation created by Retrofit
     * This is used to make network requests to the remote API
     */
    private val apiService = retrofit.create(PicsumApiService::class.java)

    /**
     * Fetches a random image URL from the picsum API
     *
     * Implementation:
     * - try to retrieve a response from the API
     * - returns the response's request URL (the image link)
     * - If the response failed, return a string as an error
     */
    suspend fun getRandomImage(): String {
        return try {
            val response = apiService.getRandomImage()
            if (response.isSuccessful) {
                val imageUrl = response.raw().request.url.toString()
                imageUrl
            } else {
                "Image failed to fetch"
            }
        } catch (e: Exception) {
            e.localizedMessage?: "Something went wrong"
        }
    }
}