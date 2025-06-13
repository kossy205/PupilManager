package com.kosiso.pupilmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagination")
data class Pagination(
    @PrimaryKey
    val pageNumber: Int = 1,
    val totalPages: Int,
    val itemCount: Int
)