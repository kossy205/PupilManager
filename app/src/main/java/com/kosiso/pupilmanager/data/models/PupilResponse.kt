package com.kosiso.pupilmanager.data.models

data class PupilResponse(
    val items: List<Pupil>,
    val pageNumber: Int,
    val itemCount: Int,
    val totalPages: Int
)
