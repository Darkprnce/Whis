package com.whis.Network.sealed

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}