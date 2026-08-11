package com.example.data

import kotlinx.coroutines.flow.Flow

class PanchangRepository(private val dao: PanchangDao) {

    val allSavedRecords: Flow<List<SavedPanchangRecord>> = dao.getAllSavedRecords()
    val favoriteRecords: Flow<List<SavedPanchangRecord>> = dao.getFavoriteRecords()

    suspend fun saveRecord(record: SavedPanchangRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun updateRecord(record: SavedPanchangRecord) {
        dao.updateRecord(record)
    }

    suspend fun deleteRecord(id: Long) {
        dao.deleteRecordById(id)
    }

    suspend fun findRecord(dateIso: String, cityId: String): SavedPanchangRecord? {
        return dao.getRecordForDateAndCity(dateIso, cityId)
    }
}
