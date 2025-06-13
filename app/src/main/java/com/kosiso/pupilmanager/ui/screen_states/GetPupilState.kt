package com.kosiso.pupilmanager.ui.screen_states

sealed class GetPupilState<out T> {
    object Idle : GetPupilState<Nothing>()
    object Loading : GetPupilState<Nothing>()
    data class Success<T>(val data: T) : GetPupilState<T>()
    data class Error(val message: String) : GetPupilState<Nothing>()
}