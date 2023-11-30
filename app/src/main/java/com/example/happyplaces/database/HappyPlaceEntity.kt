package com.example.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "happyPlace-table")
data class HappyPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val title: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: String?,
    val longitude: String?,
    val image: String?
    )
