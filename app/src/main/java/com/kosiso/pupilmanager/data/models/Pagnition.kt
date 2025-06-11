package com.kosiso.pupilmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagination")
data class Pagination(
    @PrimaryKey val id: Int = 1,
    val pageNumber: Int,
    val totalPages: Int,
    val itemCount: Int
)