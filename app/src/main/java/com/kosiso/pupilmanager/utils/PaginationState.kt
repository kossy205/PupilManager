package com.kosiso.pupilmanager.utils

sealed class PaginationState<out T> {
    object Idle : PaginationState<Nothing>()
    object Loading : PaginationState<Nothing>()
    data class Success<T>(val data: T) : PaginationState<T>()
    data class Error(val message: String) : PaginationState<Nothing>()
}