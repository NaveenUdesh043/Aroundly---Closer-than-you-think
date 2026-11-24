package com.aroundly.aroundly.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

private const val WEAK_CELLULAR_SIGNAL_DBM = -100
private const val SLOW_WIFI_DOWNSTREAM_KBPS = 1_000
private const val INITIAL_STATE_DELAY_MS = 300L

@Singleton
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Emits the quality of the current default network. The current state is emitted as soon as
     * the callback is registered, and later emitted again whenever it changes.
     */
    val networkStatus: Flow<ConnectionStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(resolveStatus(network))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(resolveStatus(network, networkCapabilities))
            }

            override fun onLost(network: Network) {
                // onLost can also fire while switching to another network, so check whether a
                // new default network is already active before reporting a connection loss.
                val activeNetwork = connectivityManager.activeNetwork
                trySend(
                    if (activeNetwork == null) {
                        ConnectionStatus.LOST
                    } else {
                        resolveStatus(activeNetwork)
                    }
                )
            }

            override fun onUnavailable() {
                trySend(ConnectionStatus.LOST)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        // Give onAvailable/onCapabilitiesChanged a chance to arrive first. If there is no
        // network at all, no callback fires, so seed the initial state after a short delay.
        delay(INITIAL_STATE_DELAY_MS)

        val currentNetwork = connectivityManager.activeNetwork
        trySend(
            if (currentNetwork == null) {
                ConnectionStatus.LOST
            } else {
                resolveStatus(currentNetwork)
            }
        )

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun resolveStatus(
        network: Network,
        capabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)
    ): ConnectionStatus {
        if (capabilities == null ||
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ) {
            return ConnectionStatus.LOST
        }

        // Connected to a network (for example a captive portal) but the internet is not reachable.
        val isNotValidated = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isCaptivePortal =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
        if (isNotValidated || isCaptivePortal) {
            return ConnectionStatus.POOR
        }

        val isWeakCellular =
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                capabilities.signalStrength != NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED &&
                capabilities.signalStrength <= WEAK_CELLULAR_SIGNAL_DBM

        val isSlowWifi =
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                capabilities.linkDownstreamBandwidthKbps in 1..SLOW_WIFI_DOWNSTREAM_KBPS

        return if (isWeakCellular || isSlowWifi) {
            ConnectionStatus.POOR
        } else {
            ConnectionStatus.ONLINE
        }
    }
}
