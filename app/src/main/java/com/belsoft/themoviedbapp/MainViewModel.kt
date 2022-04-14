package com.belsoft.themoviedbapp

import androidx.lifecycle.LiveData
import com.belsoft.themoviedbapp.components.SingleLiveEvent
import com.belsoft.themoviedbapp.models.InternetConnectionDialogStateModel
import com.belsoft.themoviedbapp.services.ConnectionLiveData
import com.belsoft.themoviedbapp.services.IRequestHelper

class MainViewModel(private val requestHelper: IRequestHelper) : BaseViewModel(), IMainViewModel {
    override val toastMessage = SingleLiveEvent<Int>()
    override val toastMessageString = SingleLiveEvent<String>()
    override val internetConnectionDialogState = InternetConnectionDialogStateModel()

    val connectionLiveData: ConnectionLiveData
        get() = requestHelper.connectionLiveData

    val connectionDialogState: LiveData<Boolean?>
        get() = internetConnectionDialogState.connectionDialogState

    override fun setConnectionDialogState(isShown: Boolean) {
        internetConnectionDialogState.setMutableLiveDataValue(isShown)
    }

    override fun forceSetConnectionDialogState(isShown: Boolean) {
        internetConnectionDialogState.forceSetMutableLiveDataValue(isShown)
    }

    override fun setConnectionDialogIsDismissed() {
        internetConnectionDialogState.setDialogIsDismissed()
    }
}