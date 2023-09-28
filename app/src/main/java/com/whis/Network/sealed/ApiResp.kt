package com.whis.Network.sealed

import kotlinx.coroutines.CoroutineScope

sealed class ApiResp<out T> {
    data class Loading(val tag: String,val coroutineScope: CoroutineScope? = null) : ApiResp<Nothing>()
    object None : ApiResp<Nothing>()
    data class Error<T>(val tag: String,val message: String,val item: T?=null) : ApiResp<T?>()
    data class Success<T>(val tag: String,val item: T?) : ApiResp<T?>()
}