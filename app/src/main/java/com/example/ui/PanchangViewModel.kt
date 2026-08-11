package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.PanchangApiService
import com.example.data.PanchangDatabase
import com.example.data.PanchangRepository
import com.example.data.SavedPanchangRecord
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.PanchangResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PanchangViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PanchangRepository

    init {
        val database = PanchangDatabase.getDatabase(application)
        repository = PanchangRepository(database.panchangDao())
    }

    val savedRecords: StateFlow<List<SavedPanchangRecord>> = repository.allSavedRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedCity = MutableStateFlow(CityLocation.DEFAULT_CITIES[0])
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _language = MutableStateFlow("ta") // "ta" or "en"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _currentPanchang = MutableStateFlow(
        ThiruGanithaEngine.calculatePanchang(LocalDate.now(), CityLocation.DEFAULT_CITIES[0])
    )
    val currentPanchang: StateFlow<PanchangResult> = _currentPanchang.asStateFlow()

    // API Explorer States
    private val _apiEndpoint = MutableStateFlow("/api/v1/panchangam")
    val apiEndpoint: StateFlow<String> = _apiEndpoint.asStateFlow()

    private val _apiResponseText = MutableStateFlow("")
    val apiResponseText: StateFlow<String> = _apiResponseText.asStateFlow()

    private val _apiStatus = MutableStateFlow(200)
    val apiStatus: StateFlow<Int> = _apiStatus.asStateFlow()

    private val _apiResponseTimeMs = MutableStateFlow(12L)
    val apiResponseTimeMs: StateFlow<Long> = _apiResponseTimeMs.asStateFlow()

    private val _isRecordSavedForToday = MutableStateFlow(false)
    val isRecordSavedForToday: StateFlow<Boolean> = _isRecordSavedForToday.asStateFlow()

    init {
        recalculate()
        runApiExplorer()
    }

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
        recalculate()
    }

    fun setPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
        recalculate()
    }

    fun setNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
        recalculate()
    }

    fun setToday() {
        _selectedDate.value = LocalDate.now()
        recalculate()
    }

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        recalculate()
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == "ta") "en" else "ta"
    }

    fun setApiEndpoint(endpoint: String) {
        _apiEndpoint.value = endpoint
        runApiExplorer()
    }

    private fun recalculate() {
        val panchang = ThiruGanithaEngine.calculatePanchang(_selectedDate.value, _selectedCity.value)
        _currentPanchang.value = panchang
        runApiExplorer()

        viewModelScope.launch {
            val existing = repository.findRecord(panchang.dateIso, panchang.city.id)
            _isRecordSavedForToday.value = existing != null
        }
    }

    fun runApiExplorer() {
        val startTime = System.currentTimeMillis()
        val dateIso = _selectedDate.value.toString()
        val cityId = _selectedCity.value.id

        val (status, json) = when (_apiEndpoint.value) {
            "/api/v1/tithi" -> PanchangApiService.getTithiJson(dateIso)
            "/api/v1/nakshatra" -> PanchangApiService.getNakshatraJson(dateIso)
            "/api/v1/gowri" -> PanchangApiService.getGowriJson(dateIso)
            "/api/v1/horai" -> PanchangApiService.getHoraiJson(dateIso)
            else -> PanchangApiService.getFullPanchangJson(dateIso, cityId, _language.value)
        }

        val elapsed = System.currentTimeMillis() - startTime
        _apiStatus.value = status
        _apiResponseText.value = json
        _apiResponseTimeMs.value = if (elapsed < 1) 8L else elapsed
    }

    fun saveCurrentPanchang(userNote: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val p = _currentPanchang.value
            val record = SavedPanchangRecord(
                dateIso = p.dateIso,
                cityId = p.city.id,
                cityName = p.city.nameTamil,
                tamilDateStr = p.dateDisplayTamil,
                tithiName = p.tithi.nameTamil,
                nakshatraName = p.nakshatra.nameTamil,
                nallaNeramStr = p.nallaNeram.morning.formatted,
                specialEventsStr = p.specialEventsTamil.joinToString(", "),
                userNote = userNote
            )
            repository.saveRecord(record)
            _isRecordSavedForToday.value = true
        }
    }

    fun deleteSavedRecord(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecord(id)
            recalculate()
        }
    }

    fun toggleFavoriteRecord(record: SavedPanchangRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(record.copy(isFavorite = !record.isFavorite))
        }
    }
}
