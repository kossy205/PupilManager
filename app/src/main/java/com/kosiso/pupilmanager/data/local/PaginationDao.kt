package com.kosiso.pupilmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kosiso.pupilmanager.data.models.Pagination

@Dao
interface PaginationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPagination(pagination: Pagination)

    @Query("SELECT * FROM pagination WHERE pageNumber = :pageNumber")
    suspend fun getPagination(pageNumber: Int): Pagination?
}