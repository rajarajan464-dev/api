package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PanchangViewModel
import com.example.ui.components.HeaderBar
import com.example.ui.screens.ApiExplorerScreen
import com.example.ui.screens.DailyPanchangScreen
import com.example.ui.screens.GowriHoraiScreen
import com.example.ui.screens.MonthlyCalendarScreen
import com.example.ui.screens.SavedRecordsScreen
import com.example.ui.theme.PanchangTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PanchangViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanchangTheme {
                PanchangAppMain(viewModel = viewModel)
            }
        }
    }
}

enum class PanchangTab(val tag: String, val labelTamil: String, val labelEnglish: String, val icon: ImageVector) {
    DAILY("tab_daily", "இன்று", "Daily", Icons.Default.Today),
    GOWRI_HORAI("tab_gowri", "கௌரி/ஹோரை", "Gowri & Horai", Icons.Default.Schedule),
    CALENDAR("tab_calendar", "மாத நாட்காட்டி", "Calendar", Icons.Default.CalendarMonth),
    API_EXPLORER("tab_api", "REST API", "API Explorer", Icons.Default.Code),
    SAVED("tab_saved", "சேமித்தவை", "Saved", Icons.Default.Bookmark)
}

@Composable
fun PanchangAppMain(viewModel: PanchangViewModel) {
    var currentTab by remember { mutableStateOf(PanchangTab.DAILY) }

    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val language by viewModel.language.collectAsState()
    val currentPanchang by viewModel.currentPanchang.collectAsState()

    val apiEndpoint by viewModel.apiEndpoint.collectAsState()
    val apiResponseText by viewModel.apiResponseText.collectAsState()
    val apiStatus by viewModel.apiStatus.collectAsState()
    val apiResponseTimeMs by viewModel.apiResponseTimeMs.collectAsState()

    val savedRecords by viewModel.savedRecords.collectAsState()
    val isRecordSavedForToday by viewModel.isRecordSavedForToday.collectAsState()

    Scaffold(
        topBar = {
            HeaderBar(
                currentCity = selectedCity,
                language = language,
                isSaved = isRecordSavedForToday,
                onCitySelected = { viewModel.setCity(it) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSaveToggle = { viewModel.saveCurrentPanchang() }
            )
        },
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp
            ) {
                PanchangTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.labelEnglish) },
                        label = {
                            Text(
                                text = if (language == "ta") tab.labelTamil else tab.labelEnglish,
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                PanchangTab.DAILY -> {
                    DailyPanchangScreen(
                        panchang = currentPanchang,
                        selectedDate = selectedDate,
                        language = language,
                        onPreviousDay = { viewModel.setPreviousDay() },
                        onNextDay = { viewModel.setNextDay() },
                        onToday = { viewModel.setToday() },
                        onDateSelected = { viewModel.setDate(it) },
                        onSaveNoteRequested = { note -> viewModel.saveCurrentPanchang(note) }
                    )
                }
                PanchangTab.GOWRI_HORAI -> {
                    GowriHoraiScreen(
                        panchang = currentPanchang,
                        language = language
                    )
                }
                PanchangTab.CALENDAR -> {
                    MonthlyCalendarScreen(
                        currentSelectedDate = selectedDate,
                        currentCity = selectedCity,
                        language = language,
                        onDateSelected = { date ->
                            viewModel.setDate(date)
                            currentTab = PanchangTab.DAILY
                        }
                    )
                }
                PanchangTab.API_EXPLORER -> {
                    ApiExplorerScreen(
                        currentEndpoint = apiEndpoint,
                        apiResponseText = apiResponseText,
                        apiStatus = apiStatus,
                        responseTimeMs = apiResponseTimeMs,
                        selectedDate = selectedDate,
                        selectedCity = selectedCity,
                        language = language,
                        onEndpointChanged = { endpoint -> viewModel.setApiEndpoint(endpoint) },
                        onRunApi = { viewModel.runApiExplorer() }
                    )
                }
                PanchangTab.SAVED -> {
                    SavedRecordsScreen(
                        savedRecords = savedRecords,
                        language = language,
                        onDeleteRecord = { id -> viewModel.deleteSavedRecord(id) },
                        onToggleFavorite = { record -> viewModel.toggleFavoriteRecord(record) }
                    )
                }
            }
        }
    }
}
