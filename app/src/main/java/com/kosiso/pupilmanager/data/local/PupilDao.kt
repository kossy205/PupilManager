package com.kosiso.pupilmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kosiso.pupilmanager.data.models.Pupil
import kotlinx.coroutines.flow.Flow

@Dao
interface PupilDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPupil(pupil: Pupil)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPupilList(list: List<Pupil>)

    @Update
    suspend fun updatePupil(pupil: Pupil)

    @Query("SELECT * FROM pupils_table WHERE pupilId = :pupilId")
    suspend fun getPupilById(pupilId: Int): Pupil

    @Query("SELECT * FROM pupils_table WHERE pageNumber = :page")
    fun getPupilsByPage(page: Int): Flow<List<Pupil>>

    @Query("SELECT * FROM pupils_table ORDER BY name ASC")
    fun getAllPupils(): Flow<List<Pupil>>

    @Query("DELETE FROM pupils_table WHERE pupilId = :pupilId")
    suspend fun deletePupilById(pupilId: Int)
}