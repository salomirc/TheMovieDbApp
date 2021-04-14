package com.belsoft.themoviedbapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ImageTmdbApi {

    @Headers("Content-Type: application/json")
    @GET("{fileSize}/{filePath}")
    fun getPoster(
            @Path("fileSize") fileSize: String,
            @Path("filePath") filePath: String
    ): Call<ResponseBody>
}