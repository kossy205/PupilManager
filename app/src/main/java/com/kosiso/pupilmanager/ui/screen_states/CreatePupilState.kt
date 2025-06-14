package com.kosiso.pupilmanager.ui.screen_states

sealed class CreatePupilState<out T> {
    object Idle : CreatePupilState<Nothing>()
    object Loading : CreatePupilState<Nothing>()
    data class Success<T>(val data: T) : CreatePupilState<T>()
    data class Error(val message: String) : CreatePupilState<Nothing>()
}