package com.kosiso.pupilmanager.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.repository.MainRepository
import com.kosiso.pupilmanager.ui.screen_states.DeletePupilState
import com.kosiso.pupilmanager.ui.screen_states.GetPupilByIdState
import com.kosiso.pupilmanager.ui.screen_states.GetPupilState
import com.kosiso.pupilmanager.utils.PaginationState
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val mainRepo: MainRepository): ViewModel(){

    private val _pupilsList = MutableStateFlow<GetPupilState<List<Pupil>>>(GetPupilState.Idle)
    val pupilsList: StateFlow<GetPupilState<List<Pupil>>> = _pupilsList
    // toast event for pupils list
    private val _toastEventPL = MutableStateFlow<String>("")
    val toastEventPL: StateFlow<String> = _toastEventPL

    private val _pupilById = MutableStateFlow<GetPupilByIdState<Pupil>>(GetPupilByIdState.Idle)
    val pupilById: StateFlow<GetPupilByIdState<Pupil>> = _pupilById
    // toast event for a single pupil
    private val _toastEventP = MutableStateFlow<String>("")
    val toastEventP: StateFlow<String> = _toastEventP

    private val _deletePupil = MutableStateFlow<DeletePupilState<Unit>>(DeletePupilState.Idle)
    val deletePupil: StateFlow<DeletePupilState<Unit>> = _deletePupil

    private val _pagination = MutableStateFlow<PaginationState<Pagination>>(PaginationState.Idle)
    val pagination: StateFlow<PaginationState<Pagination>> = _pagination

    init {
        getPupils(1)
    }

    fun getPupils(page: Int) {
        viewModelScope.launch {

            mainRepo.getPupils(page).collect { response ->
                Log.i("get pupils vm", "${response}")

                when (response) {
                    is PupilsDbResponse.Loading -> {}
                    is PupilsDbResponse.Success -> {
                        val newItems = response.data.items
                        val pagination = Pagination(
                            pageNumber = response.data.pageNumber,
                            totalPages = response.data.totalPages,
                            itemCount = response.data.itemCount
                        )

                        // Updates pupilsList only if:
                        // 1. New items are non-empty, or
                        // 2. New items are empty and current state is Idle or Success with empty items
                        val currentPupilsState = _pupilsList.value
                        val shouldUpdatePupils = newItems.isNotEmpty() ||
                                (newItems.isEmpty() && (currentPupilsState is GetPupilState.Idle ||
                                        (currentPupilsState is GetPupilState.Success && currentPupilsState.data.isEmpty())))

                        if (shouldUpdatePupils) {
                            _pupilsList.value = GetPupilState.Success(newItems)
                            _pagination.value = PaginationState.Success(pagination)
                        }
                    }
                    is PupilsDbResponse.Error -> {
                        _toastEventPL.value = response.message
                    }
                }
            }
        }
    }

    fun getPupilById(pupilId: Int) {
        viewModelScope.launch {
            val pupil = mainRepo.getPupilById(pupilId)
            Log.i("pupil details vm", "pupil: ${pupil}")
            when (pupil) {
                is PupilsDbResponse.Success -> {
                    _pupilById.value = GetPupilByIdState.Success(pupil.data)
                }
                is PupilsDbResponse.Error -> {
                    _toastEventP.value = pupil.message
                }
                else -> {}
            }
        }
    }

    fun createPupil(pupil: Pupil) {

    }

    fun deletePupil(pupilId: Int) {
        viewModelScope.launch {
            mainRepo.deletePupil(pupilId).apply {
                onSuccess {
                    _deletePupil.value = DeletePupilState.Success(Unit)
                    val paginationState = _pagination.value
                    if(paginationState is PaginationState.Success){
                        val currentPage = paginationState.data.pageNumber
                        getPupils(currentPage)
                    }

                }
                onFailure {
                    _deletePupil.value = DeletePupilState.Error(it.message.toString())

                }
            }
        }

    }

}