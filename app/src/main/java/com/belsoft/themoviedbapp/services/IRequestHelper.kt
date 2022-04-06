package com.belsoft.themoviedbapp.services

import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel

interface IRequestHelper {

    val connectionLiveData: ConnectionLiveData

    suspend fun getMovieDbSearch(
        api_key: String,
        query: String
    ): MovieDbResponseModel?

    fun getPoster(fileSize: String, filePath: String): ByteArray?
}