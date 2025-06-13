package com.kosiso.pupilmanager.data.repository

import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.models.PupilResponse
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getPupils(page: Int): Flow<PupilsDbResponse<PupilResponse>>
    suspend fun getPupilById(pupilId: Int): PupilsDbResponse<Pupil>
    suspend fun deletePupil(pupilId: Int): Result<Unit>


    suspend fun getPupilByIdFromServer(pupilId: Int): PupilsDbResponse<Pupil>
    suspend fun deletePupilFromServer(pupilId: Int): Result<Unit>

    suspend fun getPupilByIdFromLocalDb(pupilId: Int): Result<Pupil>
    suspend fun deletePupilFromLocalDb(pupilId: Int): Result<Unit>
    suspend fun insertPupilList(list: List<Pupil>, pagination: Pagination): Result<Unit>
}
