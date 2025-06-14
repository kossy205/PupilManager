package com.kosiso.pupilmanager.data.repository

import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.models.PupilResponse
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun createPupil(pupil: Pupil): PupilsDbResponse<Unit>
    fun getPupils(page: Int): Flow<PupilsDbResponse<PupilResponse>>
    suspend fun getPupilById(pupilId: Int): PupilsDbResponse<Pupil>
    suspend fun updatePupil(pupilId: Int, pupil: Pupil): PupilsDbResponse<Unit>
    suspend fun deletePupil(pupilId: Int): Result<Unit>

    // server
    suspend fun createPupilInServer(pupil: Pupil): PupilsDbResponse<Unit>
    suspend fun getPupilByIdFromServer(pupilId: Int): PupilsDbResponse<Pupil>
    suspend fun updatePupilInServer(pupilId: Int, pupil: Pupil): PupilsDbResponse<Unit>
    suspend fun deletePupilFromServer(pupilId: Int): Result<Unit>

    // local db
    suspend fun insertPupilList(list: List<Pupil>, pagination: Pagination): Result<Unit>
    suspend fun insertPupil(pupil: Pupil): Result<Unit>
    suspend fun getPupilByIdFromLocalDb(pupilId: Int): Result<Pupil>
    suspend fun updatePupilInLocalDb(pupil: Pupil): Result<Unit>
    suspend fun deletePupilFromLocalDb(pupilId: Int): Result<Unit>
}
