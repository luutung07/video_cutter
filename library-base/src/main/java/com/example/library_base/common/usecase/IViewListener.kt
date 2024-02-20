package com.example.library_base.common.usecase

interface IViewListener {
    fun onInitial() {
    }

    fun onLoading() {
    }

    fun onFailure() {
    }

    fun onSuccess()
}
