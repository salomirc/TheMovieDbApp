package com.belsoft.themoviedbapp

import androidx.lifecycle.LiveData
import com.belsoft.themoviedbapp.components.SingleLiveEvent
import com.belsoft.themoviedbapp.services.ConnectionModel
import com.belsoft.themoviedbapp.services.IRequestHelper

class MainViewModel(private val requestHelper: IRequestHelper) : BaseViewModel(), IMainViewModel {
    override val toastMessage = SingleLiveEvent<Int>()
    override val toastMessageString = SingleLiveEvent<String>()

    val connectionLiveData: LiveData<ConnectionModel>
        get() = requestHelper.connectionLiveData
}