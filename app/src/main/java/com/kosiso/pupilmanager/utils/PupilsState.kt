package com.kosiso.pupilmanager.utils


sealed class PupilsState<out T> {
    object Idle : PupilsState<Nothing>()
    object Loading : PupilsState<Nothing>()
    data class Success<T>(val data: T) : PupilsState<T>()
    data class Error(val message: String) : PupilsState<Nothing>()
}