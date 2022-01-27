package com.belsoft.themoviedbapp.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData

data class ConnectionModel(
    val type: ConnectionType,
    val isConnected: Boolean,
    val wasDisconnected: Boolean = false
)

enum class ConnectionType(val index: Int) {
    WIFI_DATA(0),
    MOBILE_DATA(1),
    OTHER_DATA(2),
    NO_DATA(3);

    companion object {
        fun valueOf(value: Int) = values().find { it.index == value }
    }
}

class ConnectionLiveData(context: Context) : MutableLiveData<ConnectionModel>() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = mutableSetOf()
    private var wasPreviousDisconnected : Boolean = false

    val isConnected: Boolean
        get() {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    connectivityManager.activeNetwork?.let {
                        hasInternet(it)
                    } ?: false
                }
                else -> {
                    @Suppress("DEPRECATION")
                    connectivityManager.activeNetworkInfo?.isConnected ?: false
                }
            }
        }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d("ConnectionLiveData", "onAvailable() called")
            val isConnected = hasInternet(network)
            if (isConnected == true) {
                validNetworks.add(network)
            }
            evaluateNetworkConnection()
        }

        override fun onLost(network: Network) {
            Log.d("ConnectionLiveData", "onLost() called")
            validNetworks.remove(network)
            evaluateNetworkConnection()
        }
    }

    override fun onActive() {
        super.onActive()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        Log.d("ConnectionLiveData", "registerDefaultNetworkCallback")
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        Log.d("ConnectionLiveData", "unregisterNetworkCallback")
    }

    private fun hasInternet(network: Network) =
        getNetworkCapabilities(network)?.hasCapability(NET_CAPABILITY_INTERNET)

    private fun getNetworkCapabilities(network: Network) = connectivityManager.getNetworkCapabilities(network)

    private fun getConnectionType(networkCapabilities: NetworkCapabilities?): ConnectionType {
        return when {
            networkCapabilities == null -> ConnectionType.NO_DATA
            hasTransport(networkCapabilities, TRANSPORT_WIFI) -> ConnectionType.WIFI_DATA
            hasTransport(networkCapabilities, TRANSPORT_CELLULAR) -> ConnectionType.MOBILE_DATA
            else -> ConnectionType.OTHER_DATA
        }
    }

    private fun hasTransport(networkCapabilities: NetworkCapabilities?, transportType: Int) =
        networkCapabilities?.hasTransport(transportType) == true

    private fun getActiveNetworkType() = validNetworks
        .map { network -> getConnectionType(getNetworkCapabilities(network)) }
        .minByOrNull { connectionType -> connectionType.index } ?: ConnectionType.NO_DATA

    private fun evaluateNetworkConnection() {
        val type = getActiveNetworkType()
        val isConnected = type != ConnectionType.NO_DATA
        generateConnectionState(isConnected, type)
    }

    private fun generateConnectionState(isConnected: Boolean, networkType: ConnectionType) {
        handleNetworkInfo(isConnected, networkType)
        updatePreviousStatus(isConnected)
    }

    private fun handleNetworkInfo(isConnected: Boolean, networkType: ConnectionType) {
        val model = ConnectionModel(
            type = networkType,
            isConnected = isConnected,
            wasDisconnected = wasPreviousDisconnected
        )
        postValue(model)
        Log.d("ConnectionLiveData", "we just posted the model = $model")
    }

    private fun updatePreviousStatus(isConnected: Boolean) {
        wasPreviousDisconnected = !isConnected
        Log.d("ConnectionLiveData", "wasPreviousDisconnected status new value = $wasPreviousDisconnected")
    }
}