package com.belsoft.themoviedbapp

import android.app.Application
import com.belsoft.themoviedbapp.utils.InjectorUtils

class CustomApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        InjectorUtils.getInstance(this)
    }
}