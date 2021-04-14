package com.belsoft.themoviedbapp

import com.belsoft.themoviedbapp.components.SingleLiveEvent

interface IMainViewModel {
    val toastMessage: SingleLiveEvent<Int>
    val toastMessageString: SingleLiveEvent<String>
}