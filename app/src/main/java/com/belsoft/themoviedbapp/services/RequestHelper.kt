package com.belsoft.themoviedbapp.services

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.belsoft.themoviedbapp.api.ImageTmdbApi
import com.belsoft.themoviedbapp.api.TheMovieDbApi
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import com.belsoft.themoviedbapp.ui.search.SearchFragment
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_THE_MOVIE_DB_URL = "https://api.themoviedb.org/"
const val IMAGE_TMDB_URL = "https://image.tmdb.org/t/p/"
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

    enum class BaseUrlRetrofit(baseUrl: String) {
        API_THE_MOVIE_DB(API_THE_MOVIE_DB_URL),
        IMAGE_TMDB(IMAGE_TMDB_URL);

        object Singleton {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
        }

        val instance: Retrofit = Retrofit.Builder()
                .client(Singleton.okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private val theMovieDbApi: TheMovieDbApi by lazy { BaseUrlRetrofit.API_THE_MOVIE_DB.instance.create(TheMovieDbApi::class.java) }
    private val imageTmdbApi: ImageTmdbApi by lazy { BaseUrlRetrofit.IMAGE_TMDB.instance.create(ImageTmdbApi::class.java) }

    override fun getMovieDbSearch(api_key: String, query: String): MovieDbResponseModel? {
        try {
            theMovieDbApi.getMovieDbSearch(api_key, query).execute().let { response ->
                if (response.code() == 200) return response.body()
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
                if (response.code() == 200) return response.body()?.bytes()
            }
        }
        catch (e: Exception){
            logError(TAG, e)
        }
        return null
    }
}