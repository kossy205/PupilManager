package com.kosiso.pupilmanager.ui.screen_states

sealed class GetPupilByIdState<out T> {
    object Idle : GetPupilByIdState<Nothing>()
    object Loading : GetPupilByIdState<Nothing>()
    data class Success<T>(val data: T) : GetPupilByIdState<T>()
    data class Error(val message: String) : GetPupilByIdState<Nothing>()
}