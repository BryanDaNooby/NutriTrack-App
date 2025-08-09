package com.fit2081.bryan_34309861_a3_app.data.api

import com.fit2081.bryan_34309861_a3_app.data.model.Fruit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service interface that defines API endpoints for FruityVice operations
 *
 * This interface is used by Retrofit to generate a concrete implementation
 * for making HTTP requests to the remote API server
 */
interface FruityViceApiService {
    /**
     * GET endpoint to retrieve the fruit details from the server
     *
     * @param name: a string to pass as the endpoint for API
     * @return Fruit that has been retrieved from the API
     */
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Response<Fruit>
}