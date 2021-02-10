package com.example.myapplication

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.example.myapplication.core.TAG

class ConnectivityLiveData internal constructor(private val connectivityManager: ConnectivityManager)
    : LiveData<Boolean>() {

    constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager)

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "connected")
            postValue(true)
        }

        override fun onUnavailable() {
            postValue(false)
        }
        override fun onLost(network: Network) {
            Log.d(TAG, "lost connection")
            postValue(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActive() {
        super.onActive()
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnectedOrConnecting == true)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}