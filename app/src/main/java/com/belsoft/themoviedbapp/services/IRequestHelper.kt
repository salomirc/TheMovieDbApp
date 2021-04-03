package com.belsoft.themoviedbapp.services

import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import retrofit2.http.Query

interface IRequestHelper {

    fun hasInternetConnection() : Boolean
    fun getMovieDbSearch(
        @Query("api_key") api_key: String,
        @Query("query") query: String
    ) : MovieDbResponseModel?
}