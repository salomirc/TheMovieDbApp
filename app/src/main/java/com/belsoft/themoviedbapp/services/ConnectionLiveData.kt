package com.belsoft.themoviedbapp.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.belsoft.themoviedbapp.api.API_KEY
import com.belsoft.themoviedbapp.models.api.MovieDbResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class ConnectionModel(
    val type: ConnectionType,
    val isConnected: Boolean,
    val wasDisconnected: Boolean
)

data class NetworkStateModel(
    val type: ConnectionType,
    val isConnected: Boolean
)

enum class ConnectionType {
    TRANSPORT_CELLULAR,
    TRANSPORT_WIFI,
    TRANSPORT_VPN,
    TRANSPORT_OTHER,
    NO_DATA
}

class ConnectionLiveData(
    context: Context,
    getMovieDbSearch: suspend (api_key: String, query: String) -> MovieDbResponseModel?
) : MutableLiveData<ConnectionModel>() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private var wasPreviousDisconnected : Boolean = false

    val isConnected: Boolean
        get() = evaluateNetwork(connectivityManager.activeNetwork).isConnected

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d("ConnectionLiveData", "onAvailable() called, $network")
            logState("onAvailable()")
            evaluateActiveNetwork(network)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.d("ConnectionLiveData", "onCapabilitiesChanged() called network = $network, networkCapabilities = $networkCapabilities")
        }

        override fun onLinkPropertiesChanged(
            network: Network,
            linkProperties: LinkProperties
        ) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Log.d("ConnectionLiveData", "onLinkPropertiesChanged() called network = $network, linkProperties = $linkProperties")
        }

        override fun onLost(network: Network) {
            Log.d("ConnectionLiveData", "onLost() called network = $network")
            logState("onLost()")
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("ConnectionLiveData", "getMovieDbSearch() called")
                getMovieDbSearch(API_KEY, "bean").let {
                    Log.d("ConnectionLiveData", "onLost() called and response = ${it?.results}")
                }
            }
            generateConnectionState(false, ConnectionType.NO_DATA)
        }
    }

    override fun onActive() {
        super.onActive()
        logState("onActive()")
        Log.d("ConnectionLiveData", "evaluateActiveNetwork")
        evaluateActiveNetwork(connectivityManager.activeNetwork)
        Log.d("ConnectionLiveData", "registerDefaultNetworkCallback")
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d("ConnectionLiveData", "unregisterNetworkCallback")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun evaluateActiveNetwork(network: Network?) {
        evaluateNetwork(network).let {
            generateConnectionState(it.isConnected, it.type)
        }
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
            hasTransport(networkCapabilities, TRANSPORT_WIFI) -> ConnectionType.TRANSPORT_WIFI
            hasTransport(networkCapabilities, TRANSPORT_CELLULAR) -> ConnectionType.TRANSPORT_CELLULAR
            hasTransport(networkCapabilities, TRANSPORT_VPN) -> ConnectionType.TRANSPORT_VPN
            else -> ConnectionType.TRANSPORT_OTHER
        }
    }

    private fun hasTransport(networkCapabilities: NetworkCapabilities?, transportType: Int) =
        networkCapabilities?.hasTransport(transportType) ?: false

    private fun getConnectionStatus(type: ConnectionType, networkCapabilities: NetworkCapabilities?): Boolean =
        when (type) {
            ConnectionType.NO_DATA -> false
            ConnectionType.TRANSPORT_VPN -> {
                networkCapabilities?.run {
                    hasCapability(NET_CAPABILITY_TRUSTED ) &&
                            hasCapability(NET_CAPABILITY_VALIDATED)
                } ?: false
            }
            else -> {
                networkCapabilities?.run {
                    hasCapability(NET_CAPABILITY_INTERNET) &&
                            hasCapability(NET_CAPABILITY_VALIDATED)
                } ?: false
            }
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

    private fun logState(place: String) {
        Log.d("ConnectionLiveData", "$place activeNetworkInfo? = ${connectivityManager.activeNetworkInfo}")
        Log.d("ConnectionLiveData", "$place activeNetworkInfo?.isConnected = ${connectivityManager.activeNetworkInfo?.isConnected ?: false}")
        Log.d("ConnectionLiveData", "$place allNetworks: ${connectivityManager.allNetworks.map { network -> evaluateNetwork(network) }}")
        Log.d("ConnectionLiveData", "$place activeNetwork: ${connectivityManager.activeNetwork?.let { evaluateNetwork(it) } ?: NetworkStateModel(ConnectionType.NO_DATA, false)}")
    }
}