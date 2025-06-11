package com.kosiso.pupilmanager.data.repository

import android.content.Context
import android.util.Log
import com.kosiso.pupilmanager.data.ApiResponseHelper
import com.kosiso.pupilmanager.data.local.PaginationDao
import com.kosiso.pupilmanager.data.local.PupilDao
import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.remote.PupilApi
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import com.kosiso.pupilmanager.utils.PupilsDbResponse.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    val pupilDao: PupilDao,
    val paginationDao: PaginationDao,
    val pupilApi: PupilApi,
    val context: Context,
    val apiResponseHelper: ApiResponseHelper): MainRepository {

    override fun getPupils(page: Int): Flow<PupilsDbResponse<List<Pupil>>> = flow {
        // fetch from local database
        val localPupils = pupilDao.getPupilsByPage(page).firstOrNull()
        if (!localPupils.isNullOrEmpty()) {
            Log.i("get pupils room", "$localPupils")
            emit(PupilsDbResponse.Success(localPupils))
        } else {
            Log.i("get pupils room", "empty list")
            emit(PupilsDbResponse.Success(emptyList()))
        }

        val result = makeApiCall(page)
        when (result) {
            is PupilsDbResponse.Success -> {
                // only emit if API call is successful and contains items
                if (result.data.isNotEmpty()) {
                    emit(result)
                }
            }
            is PupilsDbResponse.Error -> {
                // emits a state to signal API failure for toast
                emit(Error(result.message, result.code))
            }
            else -> {}
        }

    }

    private suspend fun makeApiCall(page: Int): PupilsDbResponse<List<Pupil>>{
        return apiResponseHelper.safeApiCall{
            val response = pupilApi.getPupils(page)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {

                    // insert into local database
                    val pupils = body.items.map { it.copy(pageNumber = body.pageNumber) }
                    val pagination = Pagination(
                        pageNumber = body.pageNumber,
                        totalPages = body.totalPages,
                        itemCount = body.itemCount
                    )
                    insertPupilList(pupils, pagination)
                    PupilsDbResponse.Success(body.items)
                } else {
                    PupilsDbResponse.Error("Pupils data was null", response.code())
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown API error"
                PupilsDbResponse.Error(errorMessage, response.code())
            }
        }
    }


//    override suspend fun getPagination(page: Int): Pagination {
//        val localPagination = paginationDao.getPagination(page)
//        if (localPagination != null) {
//            return localPagination
//        }
//    }

    override suspend fun insertPupilList(list: List<Pupil>, pagination: Pagination): Result<Unit> {
        return try {
            pupilDao.insertPupilList(list)
            paginationDao.insertPagination(pagination)
            Result.success(Unit)
        }catch (e: Exception){
            Log.i("insert pupil list", e.message.toString())
            Result.failure(e)
        }
    }
}