package com.kosiso.pupilmanager.utils

sealed class PupilsDbResponse<out T> {
    object Loading : PupilsDbResponse<Nothing>()
    data class Success<out T>(val data: T) : PupilsDbResponse<T>()
    data class Error(val message: String, val code: Int? = null) : PupilsDbResponse<Nothing>()
}