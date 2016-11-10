package com.sam_chordas.android.stockhawk.agera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import com.google.android.agera.BaseObservable
import org.jetbrains.anko.connectivityManager

class ConnectivityListener(private val context: Context) : BaseObservable() {

    val isNetworkConnected: Boolean
        get() = connectivityManager.activeNetworkInfo?.isConnected ?: false

    private val connectivityManager = context.connectivityManager
    private val filter = IntentFilter(CONNECTIVITY_ACTION)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = dispatchUpdate()
    }

    override fun observableActivated() {
        context.registerReceiver(receiver, filter)
    }

    override fun observableDeactivated() {
        context.unregisterReceiver(receiver)
    }
}