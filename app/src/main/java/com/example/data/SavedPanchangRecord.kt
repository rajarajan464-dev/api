package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_panchang_records")
data class SavedPanchangRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateIso: String,
    val cityId: String,
    val cityName: String,
    val tamilDateStr: String,
    val tithiName: String,
    val nakshatraName: String,
    val nallaNeramStr: String,
    val specialEventsStr: String,
    val userNote: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
