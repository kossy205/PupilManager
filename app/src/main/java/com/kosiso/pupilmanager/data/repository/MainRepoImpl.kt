package com.kosiso.pupilmanager.data.repository

import android.content.Context
import android.util.Log
import com.kosiso.pupilmanager.data.ApiResponseHelper
import com.kosiso.pupilmanager.data.local.PaginationDao
import com.kosiso.pupilmanager.data.local.PupilDao
import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.models.PupilResponse
import com.kosiso.pupilmanager.data.remote.PupilApi
import com.kosiso.pupilmanager.ui.screens.PupilsScreen
import com.kosiso.pupilmanager.utils.NetworkUtils
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    val pupilDao: PupilDao,
    val paginationDao: PaginationDao,
    val pupilApi: PupilApi,
    val context: Context,
    val apiResponseHelper: ApiResponseHelper): MainRepository {

    override fun getPupils(page: Int): Flow<PupilsDbResponse<PupilResponse>> = flow {
        // fetch from local database
        val localPupils = pupilDao.getPupilsByPage(page).firstOrNull()
        val localPagination = paginationDao.getPagination(page)
        val pupilResponse = PupilResponse(
            items = localPupils ?: emptyList(),
            pageNumber = localPagination?.pageNumber ?: 0,
            itemCount = localPagination?.itemCount ?: 0,
            totalPages = localPagination?.totalPages ?: 0
        )
        Log.i("get pagination room", "$localPagination")
        if (!localPupils.isNullOrEmpty()) {
            Log.i("get pupils room", "$localPupils")
            emit(PupilsDbResponse.Success(pupilResponse))
        } else {
            Log.i("get pupils room", "empty list")
            emit(PupilsDbResponse.Success(pupilResponse))
        }


        val result = makeGetPupilsApiCall(page)
        when (result) {
            is PupilsDbResponse.Success -> {
                emit(result)
            }
            is PupilsDbResponse.Error -> {
                // emits a state to signal API failure for toast
                emit(PupilsDbResponse.Error(result.message, result.code))
            }
            is PupilsDbResponse.Loading -> {
                emit(PupilsDbResponse.Loading)
            }
            else -> {}
        }

    }
    private suspend fun makeGetPupilsApiCall(page: Int): PupilsDbResponse<PupilResponse>{
        return apiResponseHelper.safeApiCall{
            val response = pupilApi.getPupils(page)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // insert into local database
                    val pagination = Pagination(
                        pageNumber = body.pageNumber,
                        totalPages = body.totalPages,
                        itemCount = body.itemCount
                    )
                    insertPupilList(body.items, pagination)
                    PupilsDbResponse.Success(body)
                } else {
                    PupilsDbResponse.Error("Pupils data was null", response.code())
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown API error"
                PupilsDbResponse.Error(errorMessage, response.code())
            }
        }
    }



    override suspend fun insertPupilList(list: List<Pupil>, pagination: Pagination): Result<Unit> {
        return try {
            val updatedPupils = list.map { pupil ->
                pupil.copy(pageNumber = pagination.pageNumber)
            }
            Log.i("pupils to be inserted", "${updatedPupils}")
            Log.i("pagination to be inserted", "$pagination")
            pupilDao.insertPupilList(updatedPupils)
            paginationDao.insertPagination(pagination)
            Result.success(Unit)
        }catch (e: Exception){
            Log.i("insert pupil list", e.message.toString())
            Result.failure(e)
        }
    }



    override suspend fun getPupilById(pupilId: Int): PupilsDbResponse<Pupil> {
        return withContext(Dispatchers.IO) {
            // pupil from room
            val result = getPupilByIdFromLocalDb(pupilId)
            Log.i("pupil details repo", "pupil: $result")
            when {
                result.isSuccess -> {
                    Log.i("pupil details repo success", "pupil: ${result}")
                    PupilsDbResponse.Success(result.getOrThrow())
                }
                result.isFailure -> {
                    // Don't fetch from api, because the Pupil data class has "pageNumber" in it, ...
                    // ...which is part of the roomdb but isn't part of the pupil from server.
                    // If u a single pupil by id from server, it would be impossible to access "pageNumber".
                    // This might cause a crash or some bugs later
                    // pupil from api
//                    getPupilByIdFromServer(pupilId)
                    PupilsDbResponse.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
                else -> PupilsDbResponse.Error("Unexpected result")
            }
        }
    }

    override suspend fun getPupilByIdFromServer(pupilId: Int): PupilsDbResponse<Pupil> {
        return apiResponseHelper.safeApiCall {
            val response = pupilApi.getPupilById(pupilId)
            if (response.isSuccessful) {
                val pupil = response.body()
                if (pupil != null) {
                    Log.i("Pupil by id", "Pupil fetched successfully: $pupil")
                    PupilsDbResponse.Success(pupil)
                } else {
                    Log.e("Pupil by id", "Response body is null")
                    PupilsDbResponse.Error("Pupil data is null")
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "can't get pupils, try again"
                PupilsDbResponse.Error(errorMessage)
            }
        }
    }

    override suspend fun getPupilByIdFromLocalDb(pupilId: Int): Result<Pupil> {
        return withContext(Dispatchers.IO) {
            try {
                val pupil = pupilDao.getPupilById(pupilId)
                Result.success(pupil)
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }



    override suspend fun createPupil(pupil: Pupil): PupilsDbResponse<Unit> {
        return withContext(Dispatchers.IO) {
            // insert into room
            val result = insertPupil(pupil)
            Log.i("create pupil repo", "$result")
            when {
                result.isSuccess -> {
                    Log.i("create pupil repo success", "${result}")
                    // create in serve
                    createPupilInServer(pupil)
                    PupilsDbResponse.Success(result.getOrThrow())
                }
                result.isFailure -> {
                    // create in serve
                    createPupilInServer(pupil)
                    PupilsDbResponse.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
                else -> PupilsDbResponse.Error("Unexpected result")
            }
        }
    }

    override suspend fun createPupilInServer(pupil: Pupil): PupilsDbResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiResponseHelper.safeApiCall {
                    val response = pupilApi.createPupil(pupil)
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.i("create pupil", "$body")
                        PupilsDbResponse.Success(Unit)
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "can't create now, try again"
                        PupilsDbResponse.Error(errorMessage)
                    }
                }
            }catch (e: Exception){
                PupilsDbResponse.Error(e.message.toString())
            }
        }
    }

    override suspend fun insertPupil(pupil: Pupil): Result<Unit> {
        return try {
            pupilDao.insertPupil(pupil)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }



    override suspend fun updatePupil(pupilId: Int, pupil: Pupil): PupilsDbResponse<Unit> {
        return withContext(Dispatchers.IO) {
            // update in room
            val result = updatePupilInLocalDb(pupil)
            Log.i("update pupil repo", "$result")
            when {
                result.isSuccess -> {
                    Log.i("update pupil repo success", "${result}")
                    // update in server
                    updatePupilInServer(pupilId, pupil)
                    PupilsDbResponse.Success(result.getOrThrow())
                }
                result.isFailure -> {
                    // update in serve
                    Log.i("update pupil repo error", "${result}")
                    updatePupilInServer(pupilId, pupil)
                    PupilsDbResponse.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
                else -> PupilsDbResponse.Error("Unexpected result")
            }
        }
    }

    override suspend fun updatePupilInServer(pupilId: Int, pupil: Pupil): PupilsDbResponse<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiResponseHelper.safeApiCall {
                    val response = pupilApi.updatePupil(pupilId, pupil)
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.i("update pupil", "$body")
                        PupilsDbResponse.Success(Unit)
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "can't update now, try again"
                        PupilsDbResponse.Error(errorMessage)
                    }
                }

            }catch (e: Exception){
                PupilsDbResponse.Error(e.message.toString())
            }
        }
    }

    override suspend fun updatePupilInLocalDb(pupil: Pupil): Result<Unit> {
        return try {
            pupilDao.updatePupil(pupil)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }



    override suspend fun deletePupil(pupilId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Delete from local database immediately
                deletePupilFromLocalDb(pupilId)

                if (NetworkUtils.isInternetAvailable(context)) {
                    // Attempt server deletion if online
                    deletePupilFromServer(pupilId)
                } else {
                    // Queue server deletion for when user is back online
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deletePupilFromServer(pupilId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = pupilApi.deletePupil(pupilId)
                if (response.isSuccessful) {
                    val body = response.body()
                    Result.success(Unit)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "can't, try again"
                    Result.failure(Exception(errorMessage))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    override suspend fun deletePupilFromLocalDb(pupilId: Int): Result<Unit> {
        return try {
            pupilDao.deletePupilById(pupilId)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}