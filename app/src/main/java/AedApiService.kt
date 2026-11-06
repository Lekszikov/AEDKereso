package com.example.aedkereso

import retrofit2.http.GET

interface AedApiService {
    @GET("defibrillators.json")
    suspend fun getDefibrillators(): List<AedElement>
}
