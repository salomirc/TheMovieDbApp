package com.belsoft.themoviedbapp

import com.belsoft.themoviedbapp.components.SingleLiveEvent
import com.belsoft.themoviedbapp.models.InternetConnectionDialogStateModel

interface IMainViewModel {
    val toastMessage: SingleLiveEvent<Int>
    val toastMessageString: SingleLiveEvent<String>
    val internetConnectionDialogState: InternetConnectionDialogStateModel
    fun setConnectionDialogState(isShown: Boolean)
    fun forceSetConnectionDialogState(isShown: Boolean)
}