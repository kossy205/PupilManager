package com.kosiso.pupilmanager.data.remote

import com.kosiso.pupilmanager.data.models.Pupil
import com.kosiso.pupilmanager.data.models.PupilResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PupilApi {
    @GET("/pupils")
    suspend fun getPupils(@Query("page") page: Int = 1): Response<PupilResponse>

    @GET("/pupils/{pupilId}")
    suspend fun getPupilById(@Path("pupilId") pupilId: Int): Response<Pupil>

    @POST("/pupils")
    suspend fun createPupil(@Body() pupil: Pupil): Response<Pupil>

    @PUT("/pupils/{pupilId}")
    suspend fun updatePupil(
        @Path("pupilId") pupilId: Int,
        @Body pupil: Pupil): Response<Pupil>

    @DELETE("/pupils/{pupilId}")
    suspend fun deletePupil(@Path("pupilId") pupilId: Int): Response<Unit>

}