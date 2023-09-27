package com.whis.Network.sealed

sealed class ValidationState {
    object Ideal : ValidationState()
    data class Loading(val tag: String,val isLoading:Boolean) : ValidationState()
    data class Success(val tag: String, val data: Any) : ValidationState()
    data class Error(val tag: String, val errorMsg: String) : ValidationState()

}