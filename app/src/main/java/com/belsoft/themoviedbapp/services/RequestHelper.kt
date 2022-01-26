package com.belsoft.themoviedbapp.services

import android.app.Application
import com.belsoft.themoviedbapp.api.ImageTmdbApi
import com.belsoft.themoviedbapp.api.TheMovieDbApi
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_THE_MOVIE_DB_URL = "https://api.themoviedb.org/"
const val IMAGE_TM_DB_URL = "https://image.tmdb.org/t/p/"
const val LOGO_SIZE = "w185"

class RequestHelper(application: Application) : HelperBase(), IRequestHelper {

    companion object {
        private val TAG = RequestHelper::class.simpleName
    }

    override val connectionLiveData = ConnectionLiveData(application)
    override val hasInternetConnection: Boolean
        get() = connectionLiveData.isConnected

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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

    override fun getMovieDbSearch(api_key: String, query: String): MovieDbResponseModel? {
        try {
            theMovieDbApi.getMovieDbSearch(api_key, query).execute().let { response ->
                if (response.code() == 200) {
                    return response.body()
                }
            }
        }
        catch (e: Exception){
            logError(TAG, e)
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
        }
        catch (e: Exception){
            logError(TAG, e)
        }
        return null
    }
}