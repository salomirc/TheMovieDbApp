package com.belsoft.themoviedbapp

import com.belsoft.themoviedbapp.components.SingleLiveEvent

class MainViewModel : BaseViewModel(), IMainViewModel {
    override val toastMessage = SingleLiveEvent<Int>()
    override val toastMessageString = SingleLiveEvent<String>()
}