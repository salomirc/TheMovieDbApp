package com.belsoft.themoviedbapp.utils

import androidx.lifecycle.ViewModel

interface Provider<T: ViewModel> {
    fun get(): T
}