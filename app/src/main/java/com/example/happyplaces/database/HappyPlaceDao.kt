package com.example.happyplaces.database

import androidx.room.*

@Dao
interface HappyPlaceDao {

    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Update
    suspend fun update(happyPlaceEntity: HappyPlaceEntity)

    @Query("SELECT * FROM `happyPlace-table`-table ")
    suspend fun fetchAllHappyPlace():List<HappyPlaceEntity>

}