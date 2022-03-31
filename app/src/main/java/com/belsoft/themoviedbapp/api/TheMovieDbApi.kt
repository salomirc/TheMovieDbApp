package com.belsoft.themoviedbapp.api

import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val API_KEY = "ee34ad57ab88c48dc8802abfad136b6b"

interface TheMovieDbApi {

    @Headers("Content-Type: application/json")
    @GET("3/search/movie")
    suspend fun getMovieDbSearch(
        @Query("api_key") api_key: String,
        @Query("query") query: String
    ) : MovieDbResponseModel
}