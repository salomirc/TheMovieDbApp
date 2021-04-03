package com.belsoft.themoviedbapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val isVisibleProgressBar = MutableLiveData<Boolean>().apply { value = false}
}