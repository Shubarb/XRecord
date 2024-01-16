package com.module.config.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectionLiveData(val context: Context) : LiveData<Boolean>() {

    var intentFilter = IntentFilter(CONNECTIVITY_ACTION)
    private var connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: NetworkCallback = NetworkCallback(this)

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager.registerDefaultNetworkCallback(
                networkCallback
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val builder = NetworkRequest.Builder().addTransportType(TRANSPORT_CELLULAR)
                    .addTransportType(TRANSPORT_WIFI)
                connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
            }

            else -> {
                context.registerReceiver(networkReceiver, intentFilter)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateConnection()
        }
    }

    fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnectedOrConnecting == true)
    }

    class NetworkCallback(val liveData: ConnectionLiveData) :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            liveData.postValue(true)

        }

        override fun onLost(network: Network) {
            super.onLost(network)
            liveData.postValue(false)
        }
    }
}