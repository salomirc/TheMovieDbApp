package com.belsoft.themoviedbapp.services

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.belsoft.themoviedbapp.api.ImageTmdbApi
import com.belsoft.themoviedbapp.api.TheMovieDbApi
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

const val API_THE_MOVIE_DB_URL = "https://api.themoviedb.org/"
const val IMAGE_TM_DB_URL = "https://image.tmdb.org/t/p/"
const val LOGO_SIZE = "w185"

class RequestHelper private constructor(private val appContext: Application) : HelperBase(), IRequestHelper {

    companion object {
        @Volatile
        private var instance: RequestHelper? = null
        private val TAG = RequestHelper::class.simpleName

        fun getInstance(appContext: Application): RequestHelper {
            return instance ?: synchronized(this) {
                instance ?: RequestHelper(appContext).also { instance = it }
            }
        }
    }

    override fun hasInternetConnection(): Boolean {
        try {
            val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    @Suppress("DEPRECATION")
                    connectivityManager.activeNetworkInfo?.run {
                        return when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }
        }
        catch (e: Exception)
        {
            logError(TAG, e)
        }

        return false
    }

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

    override suspend fun getMovieDbSearch(api_key: String, query: String): MovieDbResponseModel? {
        try {
            return theMovieDbApi.getMovieDbSearch(api_key, query)
        }
        catch (e: IOException){
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