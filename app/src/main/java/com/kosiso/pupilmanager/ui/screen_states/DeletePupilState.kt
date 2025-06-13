package com.kosiso.pupilmanager.ui.screen_states

sealed class DeletePupilState<out T> {
    object Idle : DeletePupilState<Nothing>()
    object Loading : DeletePupilState<Nothing>()
    data class Success<T>(val data: T) : DeletePupilState<T>()
    data class Error(val message: String) : DeletePupilState<Nothing>()
}