package com.belsoft.themoviedbapp.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.belsoft.themoviedbapp.BuildConfig
import com.belsoft.themoviedbapp.api.TheMovieDbApi
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RequestHelper private constructor(private val appContext: Context) : IRequestHelper {

    companion object {
        @Volatile
        private var instance: RequestHelper? = null

        fun getInstance(appContext: Context): RequestHelper {
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
            println("Exception : ${e.message}")
        }

        return false
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val theMovieDbApi: TheMovieDbApi by lazy { retrofit.create(TheMovieDbApi::class.java) }

    override fun getMovieDbSearch(api_key: String, query: String): MovieDbResponseModel? {
        try {
            theMovieDbApi.getMovieDbSearch(api_key, query).execute().let { response ->
                if (response.code() == 200) return response.body()
            }
        }
        catch (e: Exception){
            println("Exception : ${e.message}")
        }
        return null
    }
}