package com.example.library_base.common


sealed class BaseResponse<out R> {
    object Loading : BaseResponse<Nothing>()
    data class Success<out T>(val data: T) : BaseResponse<T>()
    data class Error(val code: Int? = null, val msg: String? = "") : BaseResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[code=${code} msg=$msg]"
            else -> "Loading"
        }
    }
}

val <T> BaseResponse<T>.data: T?
    get() = (this as? BaseResponse.Success)?.data



