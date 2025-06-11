package com.kosiso.pupilmanager.data.repository

import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getPupils(page: Int): Flow<PupilsDbResponse<List<Pupil>>>

    suspend fun getPagination(page: Int): Pagination

    suspend fun insertPupilList(list: List<Pupil>, pagination: Pagination): Result<Unit>
}