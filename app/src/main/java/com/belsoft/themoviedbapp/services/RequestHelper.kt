package com.belsoft.themoviedbapp.services

import android.content.Context
import com.belsoft.themoviedbapp.api.ImageTmdbApi
import com.belsoft.themoviedbapp.api.TheMovieDbApi
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_THE_MOVIE_DB_URL = "https://api.themoviedb.org/"
const val IMAGE_TM_DB_URL = "https://image.tmdb.org/t/p/"
const val LOGO_SIZE = "w185"

class RequestHelper(context: Context) : IRequestHelper {

    companion object {
        private const val TAG = "RequestHelper"
    }

    private val logHelper = LogHelper()

    override val connectionLiveData = ConnectionLiveData(
        context
    )

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    private val retrofitMovieDb = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(API_THE_MOVIE_DB_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitTmDb = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(IMAGE_TM_DB_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val theMovieDbApi: TheMovieDbApi by lazy { retrofitMovieDb.create(TheMovieDbApi::class.java) }
    private val imageTmdbApi: ImageTmdbApi by lazy { retrofitTmDb.create(ImageTmdbApi::class.java) }

    override suspend fun getMovieDbSearch(api_key: String, query: String): MovieDbResponseModel? {
        try {
            return theMovieDbApi.getMovieDbSearch(api_key, query)
        } catch (e: Exception) {
            logHelper.logError(TAG, e)
        }
        return null
    }

    override fun getPoster(fileSize: String, filePath: String): ByteArray? {
        try {
            imageTmdbApi.getPoster(fileSize, filePath).execute().let { response ->
                if (response.code() == 200) {
                    return response.body()?.bytes()
                }
            }
        } catch (e: Exception) {
            logHelper.logError(TAG, e)
        }
        return null
    }
}