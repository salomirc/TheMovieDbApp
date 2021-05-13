package com.belsoft.themoviedbapp.services

import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface IRequestHelper {

    fun hasInternetConnection() : Boolean

    suspend fun getMovieDbSearch(
        api_key: String,
        query: String
    ) : MovieDbResponseModel

    fun getPoster(fileSize: String, filePath: String): ByteArray?
}