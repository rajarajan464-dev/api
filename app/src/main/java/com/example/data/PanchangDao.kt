package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PanchangDao {

    @Query("SELECT * FROM saved_panchang_records ORDER BY timestamp DESC")
    fun getAllSavedRecords(): Flow<List<SavedPanchangRecord>>

    @Query("SELECT * FROM saved_panchang_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRecords(): Flow<List<SavedPanchangRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SavedPanchangRecord): Long

    @Update
    suspend fun updateRecord(record: SavedPanchangRecord)

    @Query("DELETE FROM saved_panchang_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT * FROM saved_panchang_records WHERE dateIso = :dateIso AND cityId = :cityId LIMIT 1")
    suspend fun getRecordForDateAndCity(dateIso: String, cityId: String): SavedPanchangRecord?
}
