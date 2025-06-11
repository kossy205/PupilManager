package com.kosiso.pupilmanager.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.repository.MainRepository
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import com.kosiso.pupilmanager.utils.PupilsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val mainRepo: MainRepository): ViewModel(){
    private val _pupilsList = MutableStateFlow<PupilsState<List<Pupil>>>(PupilsState.Idle)
    val pupilsList: StateFlow<PupilsState<List<Pupil>>> = _pupilsList
    // toast event for pupils list
    private val _toastEventPL = MutableStateFlow<String>("")
    val toastEventPL: StateFlow<String> = _toastEventPL

    init {
        getPupils(1)
    }

    fun getPupils(page: Int = 1) {
        viewModelScope.launch {
            mainRepo.getPupils(page).collect { response ->
                Log.i("get pupils vm", "${response}")

                when (response) {
                    is PupilsDbResponse.Loading -> {
                        _pupilsList.value = PupilsState.Loading
                    }
                    is PupilsDbResponse.Success -> {
                        _pupilsList.value = PupilsState.Success(response.data)
                    }
                    is PupilsDbResponse.Error -> {
                        _toastEventPL.value = response.message
                    }
                }
            }
        }
    }

}