package com.fit2081.bryan_34309861_a3_app.data.repository

import android.content.Context
import android.util.Log
import com.fit2081.bryan_34309861_a3_app.data.model.Fruit
import com.fit2081.bryan_34309861_a3_app.data.model.Nutrition
import com.fit2081.bryan_34309861_a3_app.data.api.FruityViceApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The repository responsible for retrieving fruit data from the FruityVice API.
 * This class acts as a bridge between the API service and the rest of the application.
 */
class FruitApiRepository(context: Context) {
    /**
     * Retrofit instance configured for API communication
     * - Uses the base URL for the FruityVice API service
     * - Configures Gson converter for JSON parsing
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.fruityvice.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * API service interface implementation created by Retrofit
     * This is used to make network requests to the remote API
     */
    private val apiService = retrofit.create(FruityViceApiService::class.java)

    /**
     * Fetches the fruit details from the network
     *
     * @return A Fruit object representing the fruit
     */
    suspend fun getFruitDetailsByName(fruitName: String): Fruit {
        val dummyFruit = Fruit(
            id = 0,
            name = "",
            family = "",
            order = "",
            genus = "",
            nutritions = Nutrition(
                calories = 0f,
                fat = 0f,
                sugar = 0f,
                carbohydrates = 0f,
                protein = 0f
            )
        )
        return try {
            val response =
                apiService.getFruitByName(fruitName)

            if (response.isSuccessful) {
                val apiFruit = response.body()!!
                Log.d("THE FRUIT", "$apiFruit")
                return apiFruit
            }
            dummyFruit
        } catch (e: Exception) {
            e.printStackTrace()
            dummyFruit
        }
    }
}