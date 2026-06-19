package com.pabloisla.mipostulacion.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {

    @GET("api.php")
    suspend fun obtenerPregunta(
        @Query("amount") cantidad: Int = 1,
        @Query("category") categoria: Int = 18,
        @Query("type") tipo: String = "multiple"
    ): TriviaResponse
}