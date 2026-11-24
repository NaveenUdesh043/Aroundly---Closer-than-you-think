package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.data.network.ConnectionStatus
import com.aroundly.aroundly.data.network.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

private const val POOR_CONNECTION_GRACE_MS = 1_500L

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _snackbarEvent = MutableSharedFlow<ConnectionStatus>(extraBufferCapacity = 1)
    val snackbarEvent: SharedFlow<ConnectionStatus> = _snackbarEvent.asSharedFlow()

    private var lastStatus: ConnectionStatus? = null

    init {
        connectivityObserver.networkStatus
            .distinctUntilChanged()
            .transformLatest { status ->
                // A network can take a moment to be validated after connecting. Wait briefly
                // before reporting POOR so that a normal reconnect does not flash a warning.
                if (status == ConnectionStatus.POOR) {
                    delay(POOR_CONNECTION_GRACE_MS)
                }
                emit(status)
            }
            .onEach { status ->
                val previousStatus = lastStatus
                lastStatus = status

                // The first value is the state on app start, not a connection change.
                if (previousStatus != null && previousStatus != status) {
                    _snackbarEvent.tryEmit(status)
                }
            }
            .launchIn(viewModelScope)
    }
}
