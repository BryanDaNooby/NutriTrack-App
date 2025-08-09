package com.fit2081.bryan_34309861_a3_app.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit service interface that defines API endpoints for picsum operations
 *
 * This interface is used by Retrofit to generate a concrete implementation
 * for making HTTP request to the remote API server
 */
interface PicsumApiService {
    /**
     * GET endpoint to retrieve a random image from the API service
     *
     * @return Response<ResponseBody> containing all the API response
     */
    @GET("300")
    suspend fun getRandomImage(): Response<ResponseBody>
}