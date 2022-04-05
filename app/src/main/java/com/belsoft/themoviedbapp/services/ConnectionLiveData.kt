package com.belsoft.themoviedbapp.services

import android.content.Context
import android.net.*
import android.util.Log
import androidx.lifecycle.MutableLiveData

data class ConnectionModel(
    val type: ConnectionType,
    val isConnected: Boolean,
    val wasConnected: Boolean
)

data class NetworkStateModel(
    val type: ConnectionType,
    val isConnected: Boolean
)

enum class ConnectionType {
    TRANSPORT_VPN,
    TRANSPORT_WIFI,
    TRANSPORT_CELLULAR,
    TRANSPORT_OTHER,
    NO_DATA
}

class ConnectionLiveData(context: Context) : MutableLiveData<ConnectionModel>() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(
        ConnectivityManager::class.java
    )
    private var wasConnectedBefore: Boolean = true

    val isConnected: Boolean
        get() = evaluateNetwork(connectivityManager.activeNetwork).isConnected


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d("ConnectionLiveData", "onAvailable() called, $network")
//            logState("onAvailable()")
//            generateConnectionState(network)
        }

        override fun onLost(network: Network) {
            Log.d("ConnectionLiveData", "onLost() called, $network")
//            logState("onLost()")
            generateConnectionState(ConnectionType.NO_DATA, false)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.d("ConnectionLiveData", "onCapabilitiesChanged() called, $networkCapabilities")
//            logState("onCapabilitiesChanged()")
            generateConnectionState(networkCapabilities)
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Log.d("ConnectionLiveData", "onLinkPropertiesChanged() called, $linkProperties")
//            logState("onLinkPropertiesChanged()")
        }
    }

    override fun onActive() {
        super.onActive()
        Log.d("ConnectionLiveData", "onActive() called")
//        logState("onActive()")
//        Log.d("ConnectionLiveData", "calling checkForDisconnectedStatus()")
//        checkForDisconnectedStatus()
        Log.d("ConnectionLiveData", "calling evaluateActiveNetwork()")
        evaluateActiveNetwork()
        Log.d("ConnectionLiveData", "registerDefaultNetworkCallback")
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d("ConnectionLiveData", "unregisterNetworkCallback")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun evaluateActiveNetwork() {
        evaluateNetwork(connectivityManager.activeNetwork).also {
            if (!it.isConnected) {
                updatePreviousStatus(true)
            }
            generateConnectionState(it.type, it.isConnected)
        }
    }

    private fun checkForDisconnectedStatus() {
        if (!isConnected) {
            updatePreviousStatus(true)
            generateConnectionState(ConnectionType.NO_DATA, false)
        }
    }

    private fun evaluateCapabilities(networkCapabilities: NetworkCapabilities): NetworkStateModel {
        val type = getConnectionType(networkCapabilities)
        val isConnected = getConnectionStatus(type, networkCapabilities)
        return NetworkStateModel(type, isConnected)
    }

    private fun evaluateNetwork(network: Network?): NetworkStateModel {
        return network?.let {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val type = getConnectionType(networkCapabilities)
            val isConnected = getConnectionStatus(type, networkCapabilities)
            NetworkStateModel(type, isConnected)
        } ?: NetworkStateModel(ConnectionType.NO_DATA, false)

    }

    private fun getConnectionType(networkCapabilities: NetworkCapabilities?): ConnectionType {
        return when {
            networkCapabilities == null -> ConnectionType.NO_DATA
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.TRANSPORT_VPN
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.TRANSPORT_WIFI
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.TRANSPORT_CELLULAR
            else -> ConnectionType.TRANSPORT_OTHER
        }
    }

    private fun hasTransport(networkCapabilities: NetworkCapabilities?, transportType: Int) =
        networkCapabilities?.hasTransport(transportType) ?: false

    private fun getConnectionStatus(type: ConnectionType, networkCapabilities: NetworkCapabilities?): Boolean {
        return networkCapabilities?.run {
            when (type) {
                ConnectionType.TRANSPORT_VPN -> {
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
                else -> {
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            }
        } ?: false
    }

    private fun generateConnectionState(network: Network?) {
        evaluateNetwork(network).also {
            generateConnectionState(it.type, it.isConnected)
        }
    }

    private fun generateConnectionState(networkCapabilities: NetworkCapabilities) {
        evaluateCapabilities(networkCapabilities).also {
            generateConnectionState(it.type, it.isConnected)
        }
    }

    private fun generateConnectionState(networkType: ConnectionType, isConnected: Boolean) {
        handleNetworkInfo(networkType, isConnected)
        updatePreviousStatus(isConnected)
    }

    private fun handleNetworkInfo(networkType: ConnectionType, isConnected: Boolean) {
        val model = ConnectionModel(
            type = networkType,
            isConnected = isConnected,
            wasConnected = wasConnectedBefore
        )
        postValue(model)
        Log.d("ConnectionLiveData", "we just posted the model = $model")
    }

    private fun updatePreviousStatus(isConnected: Boolean) {
        wasConnectedBefore = isConnected
        Log.d(
            "ConnectionLiveData",
            "wasConnectedBefore status new value = $wasConnectedBefore"
        )
    }

    private fun logState(place: String) {
        Log.d(
            "ConnectionLiveData",
            "$place activeNetworkInfo? = ${connectivityManager.activeNetworkInfo}"
        )
        Log.d(
            "ConnectionLiveData",
            "$place activeNetworkInfo?.isConnected = ${connectivityManager.activeNetworkInfo?.isConnected ?: false}"
        )
        Log.d(
            "ConnectionLiveData",
            "$place allNetworks: ${
                connectivityManager.allNetworks.map { network ->
                    evaluateNetwork(network)
                }
            }"
        )
        Log.d(
            "ConnectionLiveData",
            "$place activeNetwork: ${evaluateNetwork(connectivityManager.activeNetwork)}"
        )
    }
}


