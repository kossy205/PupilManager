package com.kosiso.pupilmanager.ui.screen_states

sealed class ImageState {
    object Loading : ImageState()
    object Success : ImageState()
    object Error : ImageState()
}