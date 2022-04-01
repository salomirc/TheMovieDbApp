package com.belsoft.themoviedbapp.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    TRANSPORT_WIFI,
    TRANSPORT_CELLULAR,
    TRANSPORT_OTHER,
    NO_DATA
}

class ConnectionLiveData(context: Context) : MutableLiveData<ConnectionModel>() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(
        ConnectivityManager::class.java
    )
    private val validNetworks: MutableSet<Network> = mutableSetOf()
    private var wasConnectedBefore: Boolean = true

    val isConnected: Boolean
        get() = evaluateNetwork(connectivityManager.activeNetwork).isConnected


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            validNetworks.add(network)
            Log.d("ConnectionLiveData", "onAvailable() called, $network")
            evaluateValidNetworks()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            Log.d("ConnectionLiveData", "onLost() called network = $network")
            evaluateValidNetworks()
        }
    }

    override fun onActive() {
        super.onActive()
        Log.d("ConnectionLiveData", "onActive() called")
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
            .build()
        validNetworks.clear()
        Log.d("ConnectionLiveData", "checkForDisconnectedStatus() called")
        checkForDisconnectedStatus()
        Log.d("ConnectionLiveData", "registerDefaultNetworkCallback")
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d("ConnectionLiveData", "unregisterNetworkCallback")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun checkForDisconnectedStatus() {
        if (!isConnected) {
            updatePreviousStatus(true)
            generateConnectionState(ConnectionType.NO_DATA, false)
        }
    }

    private fun evaluateValidNetworks() {
        evaluateNetworks(validNetworks.toList())
    }

    private fun evaluateNetworks(networks: List<Network>) {
        evaluateActiveNetworkState(networks).also {
            generateConnectionState(it.type, it.isConnected)
        }
    }

    private fun evaluateActiveNetworkState(networks: List<Network>): NetworkStateModel {
        return networks
            .map { network -> evaluateNetwork(network) }
            .filter { it.isConnected }
            .minByOrNull { it.type } ?: NetworkStateModel(ConnectionType.NO_DATA, false)
    }

    private fun evaluateNetwork(network: Network?): NetworkStateModel {
        return network?.let {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val type = getConnectionType(networkCapabilities)
            val isConnected = getConnectionStatus(networkCapabilities)
            NetworkStateModel(type, isConnected)
        } ?: NetworkStateModel(ConnectionType.NO_DATA, false)

    }

    private fun getConnectionType(networkCapabilities: NetworkCapabilities?): ConnectionType {
        return when {
            networkCapabilities == null -> ConnectionType.NO_DATA
            hasTransport(
                networkCapabilities,
                NetworkCapabilities.TRANSPORT_WIFI
            ) -> ConnectionType.TRANSPORT_WIFI
            hasTransport(
                networkCapabilities,
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) -> ConnectionType.TRANSPORT_CELLULAR
            else -> ConnectionType.TRANSPORT_OTHER
        }
    }

    private fun hasTransport(networkCapabilities: NetworkCapabilities?, transportType: Int) =
        networkCapabilities?.hasTransport(transportType) ?: false

    private fun getConnectionStatus(networkCapabilities: NetworkCapabilities?): Boolean {
        return networkCapabilities?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
        } ?: false
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
            "$place validNetworks: ${validNetworks.map { network -> evaluateNetwork(network) }}"
        )
        Log.d(
            "ConnectionLiveData",
            "$place activeNetwork: ${evaluateNetwork(connectivityManager.activeNetwork)}"
        )
    }
}


