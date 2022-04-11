package com.belsoft.themoviedbapp

import com.belsoft.themoviedbapp.components.SingleLiveEvent
import com.belsoft.themoviedbapp.services.ConnectionLiveData
import com.belsoft.themoviedbapp.services.IRequestHelper

class MainViewModel(private val requestHelper: IRequestHelper) : BaseViewModel(), IMainViewModel {
    override val toastMessage = SingleLiveEvent<Int>()
    override val toastMessageString = SingleLiveEvent<String>()

    val connectionLiveData: ConnectionLiveData
        get() = requestHelper.connectionLiveData
}