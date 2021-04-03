package com.belsoft.themoviedbapp

import com.belsoft.themoviedbapp.components.SingleLiveEvent

class MainViewModel() : BaseViewModel() {

    val toastMessage = SingleLiveEvent<Int>()
    val toastMessageString = SingleLiveEvent<String>()
}