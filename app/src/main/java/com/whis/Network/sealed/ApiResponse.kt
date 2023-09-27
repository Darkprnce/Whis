package com.whis.Network.sealed

import kotlinx.coroutines.CoroutineScope

sealed class ApiResponse<out T> {
    data class Loading(val tag: String,val coroutineScope: CoroutineScope? = null) : ApiResponse<Nothing>()
    object None : ApiResponse<Nothing>()
    data class Error<T>(val tag: String,val message: String,val item: T?=null) : ApiResponse<T?>()
    data class Success<T>(val tag: String,val item: T?) : ApiResponse<T?>()
}