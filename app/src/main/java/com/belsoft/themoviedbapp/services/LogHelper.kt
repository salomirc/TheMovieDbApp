package com.belsoft.themoviedbapp.services

import android.util.Log
import java.lang.Exception

class LogHelper {

    fun logError(tag: String?, e: Exception) {
        Log.d(tag, "Exception : ${e.javaClass.simpleName}, message: ${e.message}")
    }
}