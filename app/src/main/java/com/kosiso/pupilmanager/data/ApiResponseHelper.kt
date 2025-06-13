package com.kosiso.pupilmanager.data

import android.util.Log
import com.kosiso.pupilmanager.utils.PupilsDbResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ApiResponseHelper @Inject constructor() {

    suspend fun <T> safeApiCall(apiCall: suspend () -> PupilsDbResponse<T>): PupilsDbResponse<T> {
        return try {
            withContext(Dispatchers.IO) {
                apiCall()
            }
        } catch (e: HttpException) {
            Log.i("HttpException error", e.message())
            PupilsDbResponse.Error(
                message = e.message() ?: "HTTP error",
                code = e.code()
            )
        } catch (e: IOException) {
            Log.i("IOException error", "IOException error")
            PupilsDbResponse.Error("Network error: Seems you're offline")
        } catch (e: Exception) {
            Log.i("Exception error", e.message.toString())
            PupilsDbResponse.Error("Unexpected error: ${e.message ?: "Unknown error"}")
        }
    }

}
