package com.kosiso.pupilmanager.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kosiso.pupilmanager.utils.Constants.PUPILS_TABLE
import java.sql.Timestamp

@Entity(tableName = PUPILS_TABLE)
data class Pupil(
    @PrimaryKey
    @ColumnInfo(name = "pupilId")
    val pupilId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "pageNumber")
    val pageNumber: Int,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double
)
