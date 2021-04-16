package com.belsoft.themoviedbapp.services

import android.util.Log
import java.lang.Exception

open class HelperBase {

    fun logError(tag: String?, e: Exception) {
        Log.d(tag, "Exception : ${e.javaClass.simpleName}, message: ${e.message}")
    }
}