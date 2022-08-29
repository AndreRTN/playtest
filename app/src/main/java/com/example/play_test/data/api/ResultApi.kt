package com.example.play_test.data.api

sealed class ResultApi<out R> {
    data class Success<T>(val data: T?) : ResultApi<T>()
    data class Error(val exception: String) : ResultApi<Nothing>()
    object Loading : ResultApi<Nothing>()
}
