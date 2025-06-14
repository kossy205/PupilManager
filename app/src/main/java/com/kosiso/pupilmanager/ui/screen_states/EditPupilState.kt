package com.kosiso.pupilmanager.ui.screen_states

sealed class EditPupilState<out T> {
    object Idle : EditPupilState<Nothing>()
    object Loading : EditPupilState<Nothing>()
    data class Success<T>(val data: T) : EditPupilState<T>()
    data class Error(val message: String) : EditPupilState<Nothing>()
}