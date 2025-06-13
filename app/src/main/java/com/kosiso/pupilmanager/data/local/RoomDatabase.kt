package com.kosiso.pupilmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kosiso.pupilmanager.data.models.Pagination
import com.kosiso.pupilmanager.data.models.Pupil

@Database(
    entities = [Pupil::class, Pagination::class],
    version = 11
)
abstract class RoomDatabase: RoomDatabase() {

    abstract fun pupilDao(): PupilDao
    abstract fun paginationDao(): PaginationDao

}