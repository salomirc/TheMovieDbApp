package com.belsoft.themoviedbapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class InternetConnectionDialogStateModel {
    private val _connectionDialogState = MutableLiveData<Boolean?>()
    val connectionDialogState: LiveData<Boolean?>
        get() = _connectionDialogState

    private var previousNonNullConnectionStateValue: Boolean? = null

    fun setMutableLiveDataValue(value: Boolean) {
        if (value != previousNonNullConnectionStateValue) {
            forceSetMutableLiveDataValue(value)
        }
    }

    fun forceSetMutableLiveDataValue(value: Boolean) {
        _connectionDialogState.value = value
        previousNonNullConnectionStateValue = value
    }

    fun setDialogIsDismissed() {
        _connectionDialogState.value = null
    }
}