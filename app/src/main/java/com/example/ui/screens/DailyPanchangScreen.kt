package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ThiruGanithaEngine
import com.example.model.PanchangResult
import com.example.ui.components.NakshatraDetailDialog
import com.example.ui.components.TithiDetailDialog
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.InauspiciousRed
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyPanchangScreen(
    panchang: PanchangResult,
    selectedDate: LocalDate,
    language: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onSaveNoteRequested: (String) -> Unit
) {
    val context = LocalContext.current
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    val baseDate = remember { LocalDate.of(2000, 1, 1) }
    val initialPage = remember { ChronoUnit.DAYS.between(baseDate, selectedDate).toInt() }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 100000 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val targetDate = baseDate.plusDays(pagerState.currentPage.toLong())
        if (targetDate != selectedDate) {
            onDateSelected(targetDate)
        }
    }

    LaunchedEffect(selectedDate) {
        val targetPage = ChronoUnit.DAYS.between(baseDate, selectedDate).toInt()
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageDate = baseDate.plusDays(page.toLong())
        val pagePanchang = if (pageDate == selectedDate) panchang else ThiruGanithaEngine.calculatePanchang(pageDate)

        DailyPanchangPageContent(
            panchang = pagePanchang,
            language = language,
            datePickerDialog = datePickerDialog,
            onPreviousDay = {
                coroutineScope.launch {
                    if (pagerState.currentPage > 0) {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            },
            onNextDay = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            onToday = {
                val todayPage = ChronoUnit.DAYS.between(baseDate, LocalDate.now()).toInt()
                coroutineScope.launch {
                    pagerState.animateScrollToPage(todayPage)
                }
            },
            onSaveNoteRequested = onSaveNoteRequested
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DailyPanchangPageContent(
    panchang: PanchangResult,
    language: String,
    datePickerDialog: DatePickerDialog,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onSaveNoteRequested: (String) -> Unit
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    var showNakshatraDialog by remember { mutableStateOf(false) }
    var showTithiDialog by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }

    if (showTithiDialog) {
        val parsedDate = remember(panchang.dateIso) {
            try {
                LocalDate.parse(panchang.dateIso)
            } catch (e: Exception) {
                LocalDate.now()
            }
        }
        TithiDetailDialog(
            selectedDate = parsedDate,
            city = panchang.city,
            language = language,
            onDismissRequest = { showTithiDialog = false }
        )
    }

    if (showNakshatraDialog) {
        val parsedDate = remember(panchang.dateIso) {
            try {
                LocalDate.parse(panchang.dateIso)
            } catch (e: Exception) {
                LocalDate.now()
            }
        }
        NakshatraDetailDialog(
            selectedDate = parsedDate,
            city = panchang.city,
            language = language,
            onDismissRequest = { showNakshatraDialog = false }
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Navigation Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousDay,
                            modifier = Modifier.testTag("prev_day_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pick Date",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (language == "ta") panchang.dateDisplayTamil else panchang.dateDisplayEnglish,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (language == "ta") panchang.gregorianDateDisplayTamil else panchang.gregorianDateDisplayEnglish,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(
                                onClick = onToday,
                                label = { Text(if (language == "ta") "இன்று" else "Today", fontSize = 11.sp) },
                                modifier = Modifier.testTag("today_chip")
                            )
                            IconButton(
                                onClick = onNextDay,
                                modifier = Modifier.testTag("next_day_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Day"
                                )
                            }
                        }
                    }

                    // Modern Slide Hint Badge
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Slide Date",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "ta")
                                    "👈 ஸ்வைப் செய்து அடுத்த / முந்தைய நாளுக்கு செல்லலாம் 👉"
                                else
                                    "👈 Swipe left or right for next / previous date 👉",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Hero Card: Tamil Year, Month, Day, Ayana, Sunrise/Sunset
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == "ta")
                                        "தமிழ் தேதி"
                                    else
                                        "Tamil Date",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = if (language == "ta")
                                        "${panchang.tamilMonthTamil} ${panchang.tamilDay}, ${panchang.tamilYearTamil} வருடம்"
                                    else
                                        "${panchang.tamilMonthEnglish} ${panchang.tamilDay}, ${panchang.tamilYearEnglish} Year",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == "ta")
                                        "ஆங்கில தேதி: ${panchang.gregorianDateDisplayTamil}"
                                    else
                                        "Gregorian: ${panchang.gregorianDateDisplayEnglish}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                                border = MaterialTheme.colorScheme.secondary.let {
                                    androidx.compose.foundation.BorderStroke(1.dp, it)
                                }
                            ) {
                                Text(
                                    text = if (language == "ta") panchang.vaara.nameTamil else panchang.vaara.nameEnglish,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sun & Moon Info Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SunMoonItem(
                                icon = Icons.Default.WbSunny,
                                label = if (language == "ta") "சூரியோதயம்" else "Sunrise",
                                value = panchang.sunrise,
                                iconColor = Color(0xFFFFD54F)
                            )
                            SunMoonItem(
                                icon = Icons.Default.NightsStay,
                                label = if (language == "ta") "சூரிய அஸ்தமனம்" else "Sunset",
                                value = panchang.sunset,
                                iconColor = Color(0xFFFF8A65)
                            )
                            SunMoonItem(
                                icon = Icons.Default.Brightness3,
                                label = if (language == "ta") "சந்திரோதயம்" else "Moonrise",
                                value = panchang.moonrise,
                                iconColor = Color(0xFFE0E0E0)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ayana & Ritu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == "ta")
                                    "அயனம்: ${panchang.ayanaTamil} | ருது: ${panchang.rituTamil}"
                                else
                                    "Ayana: ${panchang.ayanaEnglish} | Ritu: ${panchang.rituEnglish}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Special Events & Festival Badges (if any)
        if (panchang.specialEventsTamil.isNotEmpty() || panchang.isSubhaMuhurtham) {
            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                var isReminderSet by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (panchang.isSubhaMuhurtham) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.secondaryContainer
                    ),
                    border = if (panchang.isSubhaMuhurtham) androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.AuspiciousGreen) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🕉️ ", fontSize = 20.sp)
                                Text(
                                    text = if (language == "ta") "இன்றைய விசேஷங்கள் & விரதம்" else "Today's Festivals & Fasting",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    isReminderSet = !isReminderSet
                                    val title = if (language == "ta")
                                        "🔔 திருவிழா நினைவூட்டல்: ${panchang.specialEventsTamil.firstOrNull() ?: "விசேஷ நாள்"}"
                                    else
                                        "🔔 Festival Reminder: ${panchang.specialEventsEnglish.firstOrNull() ?: "Special Day"}"

                                    val msg = if (language == "ta")
                                        "இன்று (${panchang.dateDisplayTamil}) ${panchang.specialEventsTamil.joinToString(", ")}. திதி: ${panchang.tithi.nameTamil}"
                                    else
                                        "Today (${panchang.dateDisplayEnglish}) is ${panchang.specialEventsEnglish.joinToString(", ")}. Tithi: ${panchang.tithi.nameEnglish}"

                                    com.example.notifications.FestivalNotificationHelper.showFestivalNotification(
                                        context = context,
                                        title = title,
                                        message = msg,
                                        notificationId = panchang.dateIso.hashCode()
                                    )
                                },
                                modifier = Modifier.testTag("set_reminder_btn")
                            ) {
                                Icon(
                                    imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                    contentDescription = "Set Reminder",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val events = if (language == "ta") panchang.specialEventsTamil else panchang.specialEventsEnglish
                            events.forEach { event ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(event, fontWeight = FontWeight.Bold) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (event.contains("திருமண") || event.contains("Marriage")) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface,
                                        labelColor = if (event.contains("திருமண") || event.contains("Marriage")) com.example.ui.theme.AuspiciousGreen else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pancha Angam Section Title
        item {
            Text(
                text = if (language == "ta") "பஞ்ச அங்கம் (5 உறுப்புகள்)" else "Pancha Angam (5 Main Limbs)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Pancha Angam Cards (Tithi, Nakshatra, Vaara, Yoga, Karana)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tithi
                val tithiMain = if (panchang.tithi.isFinished) {
                    if (language == "ta") "${panchang.tithi.currentLiveNameTamil} (நடப்பு)" else "${panchang.tithi.currentLiveNameEnglish} (Live)"
                } else {
                    if (language == "ta") panchang.tithi.nameTamil else panchang.tithi.nameEnglish
                }
                val tithiBadge = if (panchang.tithi.isFinished) {
                    if (language == "ta") "${panchang.tithi.currentLivePakshaTamil} (தற்போது)" else "${panchang.tithi.currentLivePakshaEnglish} (Active)"
                } else {
                    if (language == "ta") panchang.tithi.pakshaTamil else panchang.tithi.pakshaEnglish
                }
                val tithiSub = if (panchang.tithi.isFinished) {
                    if (language == "ta")
                        "${panchang.tithi.nameTamil} [${panchang.tithi.endTime} வரை முடிந்தது] ➔ தற்போது ${panchang.tithi.currentLiveNameTamil} (${panchang.tithi.currentLivePakshaTamil}) நடக்கிறது"
                    else
                        "${panchang.tithi.nameEnglish} [Ended at ${panchang.tithi.endTime}] ➔ Now Active: ${panchang.tithi.currentLiveNameEnglish} (${panchang.tithi.currentLivePakshaEnglish})"
                } else {
                    if (language == "ta")
                        "${if (panchang.tithi.startTime.isNotEmpty()) panchang.tithi.startTime + " முதல் " else ""}• ${panchang.tithi.endTime} • அடுத்தது: ${panchang.tithi.nextNameTamil}"
                    else
                        "${if (panchang.tithi.startTime.isNotEmpty()) "From " + panchang.tithi.startTime + " " else ""}• ${panchang.tithi.endTime} • Next: ${panchang.tithi.nextNameEnglish}"
                }

                AngamCard(
                    title = if (language == "ta") "1. திதி (Tithi) • விபரம் அறிய தட்டவும்" else "1. Tithi • Tap for Details",
                    mainValue = tithiMain,
                    badgeText = tithiBadge,
                    subDetails = tithiSub,
                    icon = Icons.Default.Brightness2,
                    badgeColor = if (panchang.tithi.isFinished) AuspiciousGreen else (if (panchang.tithi.pakshaEnglish.contains("Sukla")) AuspiciousGreen else MaterialTheme.colorScheme.secondary),
                    onClick = { showTithiDialog = true }
                )

                // Nakshatra
                val nakshatraMain = if (panchang.nakshatra.isFinished) {
                    if (language == "ta")
                        "${panchang.nakshatra.currentLiveNameTamil} (பாதம் ${panchang.nakshatra.currentLivePada}) • நடப்பு"
                    else
                        "${panchang.nakshatra.currentLiveNameEnglish} (Pada ${panchang.nakshatra.currentLivePada}) • Live"
                } else {
                    if (language == "ta")
                        "${panchang.nakshatra.nameTamil} (பாதம் ${panchang.nakshatra.pada})"
                    else
                        "${panchang.nakshatra.nameEnglish} (Pada ${panchang.nakshatra.pada})"
                }

                val nakshatraBadge = if (panchang.nakshatra.isFinished) {
                    if (language == "ta") "${panchang.nakshatra.currentLiveRasiTamil} (தற்போது)" else "${panchang.nakshatra.currentLiveRasiEnglish} (Active)"
                } else {
                    if (language == "ta") panchang.nakshatra.rasiTamil else panchang.nakshatra.rasiEnglish
                }

                val nakshatraSub = if (panchang.nakshatra.isFinished) {
                    if (language == "ta")
                        "${panchang.nakshatra.nameTamil} (பாதம் ${panchang.nakshatra.pada}) [${panchang.nakshatra.endTime} வரை முடிந்தது] ➔ தற்போது ${panchang.nakshatra.currentLiveNameTamil} (பாதம் ${panchang.nakshatra.currentLivePada}) • அதிபதி: ${panchang.nakshatra.currentLiveRulingPlanetTamil}"
                    else
                        "${panchang.nakshatra.nameEnglish} (Pada ${panchang.nakshatra.pada}) [Ended at ${panchang.nakshatra.endTime}] ➔ Now Active: ${panchang.nakshatra.currentLiveNameEnglish} • Ruler: ${panchang.nakshatra.currentLiveRulingPlanetEnglish}"
                } else {
                    if (language == "ta")
                        "அதிபதி: ${panchang.nakshatra.rulingPlanetTamil} • ${panchang.nakshatra.endTime} • அடுத்தது: ${panchang.nakshatra.nextNameTamil} (பாதம் ${panchang.nakshatra.nextPada}, ${panchang.nakshatra.nextRasiTamil})"
                    else
                        "Ruler: ${panchang.nakshatra.rulingPlanetEnglish} • ${panchang.nakshatra.endTime} • Next: ${panchang.nakshatra.nextNameEnglish} (Pada ${panchang.nakshatra.nextPada}, ${panchang.nakshatra.nextRasiEnglish})"
                }

                AngamCard(
                    title = if (language == "ta") "2. நட்சத்திரம் (Nakshatra) • விபரம் அறிய தட்டவும்" else "2. Nakshatra • Tap for Details",
                    mainValue = nakshatraMain,
                    badgeText = nakshatraBadge,
                    subDetails = nakshatraSub,
                    icon = Icons.Default.Star,
                    badgeColor = if (panchang.nakshatra.isFinished) AuspiciousGreen else MaterialTheme.colorScheme.primary,
                    onClick = { showNakshatraDialog = true }
                )

                // Vaara
                AngamCard(
                    title = if (language == "ta") "3. கிழமை (Vaara)" else "3. Vaara",
                    mainValue = if (language == "ta") panchang.vaara.nameTamil else panchang.vaara.nameEnglish,
                    badgeText = if (language == "ta") panchang.vaara.planetTamil else panchang.vaara.planetEnglish,
                    subDetails = if (language == "ta")
                        "கிழமை அதிபதி: ${panchang.vaara.planetTamil}"
                    else
                        "Ruler Planet: ${panchang.vaara.planetEnglish}",
                    icon = Icons.Default.WbSunny,
                    badgeColor = MaterialTheme.colorScheme.tertiary
                )

                // Yoga
                AngamCard(
                    title = if (language == "ta") "4. யோகம் (Yoga)" else "4. Yoga",
                    mainValue = if (language == "ta") panchang.yoga.nameTamil else panchang.yoga.nameEnglish,
                    badgeText = if (language == "ta") panchang.yoga.natureTamil else panchang.yoga.natureEnglish,
                    subDetails = panchang.yoga.endTime,
                    icon = Icons.Default.SelfImprovement,
                    badgeColor = if (panchang.yoga.natureEnglish == "Auspicious") AuspiciousGreen else InauspiciousRed
                )

                // Karana
                AngamCard(
                    title = if (language == "ta") "5. கரணம் (Karana)" else "5. Karana",
                    mainValue = if (language == "ta") panchang.karana.nameTamil else panchang.karana.nameEnglish,
                    badgeText = if (language == "ta") panchang.karana.typeTamil else panchang.karana.typeEnglish,
                    subDetails = if (language == "ta") "வகை: ${panchang.karana.typeTamil}" else "Type: ${panchang.karana.typeEnglish}",
                    icon = Icons.Default.Shield,
                    badgeColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Auspicious Timings Section (சுப நேரங்கள்)
        item {
            TimingSectionCard(
                title = if (language == "ta") "சுப நேரங்கள் (Auspicious Timings)" else "Auspicious Timings",
                isAuspicious = true,
                items = listOf(
                    Pair(
                        if (language == "ta") "நல்ல நேரம் (காலை)" else "Nalla Neram (Morning)",
                        panchang.nallaNeram.morning.formatted
                    ),
                    Pair(
                        if (language == "ta") "நல்ல நேரம் (மாலை)" else "Nalla Neram (Evening)",
                        panchang.nallaNeram.evening.formatted
                    ),
                    Pair(
                        if (language == "ta") "கௌரி நல்ல நேரம் (காலை)" else "Gowri Nalla Neram (Morn)",
                        panchang.gowriNallaNeram.morning.formatted
                    ),
                    Pair(
                        if (language == "ta") "கௌரி நல்ல நேரம் (மாலை)" else "Gowri Nalla Neram (Eve)",
                        panchang.gowriNallaNeram.evening.formatted
                    )
                )
            )
        }

        // Inauspicious Timings Section (அசுப நேரங்கள்)
        item {
            TimingSectionCard(
                title = if (language == "ta") "அசுப நேரங்கள் (Inauspicious Timings)" else "Inauspicious Timings",
                isAuspicious = false,
                items = listOf(
                    Pair(
                        if (language == "ta") "ராகு காலம் (Rahu Kalam)" else "Rahu Kalam",
                        panchang.rahuKalam.formatted
                    ),
                    Pair(
                        if (language == "ta") "யம கண்டம் (Yamagandam)" else "Yamagandam",
                        panchang.yamagandam.formatted
                    ),
                    Pair(
                        if (language == "ta") "குளிகை (Kuligai)" else "Kuligai",
                        panchang.kuligai.formatted
                    ),
                    Pair(
                        if (language == "ta") "துர்முஹூர்த்தம் (Durmuhurtham)" else "Durmuhurtham",
                        panchang.durmuhurtham.formatted
                    )
                )
            )
        }

        // Astrological Extra Details Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "ta") "ஜோதிட தகவல்கள் (Astrological Notes)" else "Astrological Notes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailTextColumn(
                            label = if (language == "ta") "சந்திராஷ்டம நட்சத்திரம்" else "Chandrashtama Star",
                            value = if (language == "ta") panchang.chandrashtamaStarTamil else panchang.chandrashtamaStarEnglish
                        )
                        DetailTextColumn(
                            label = if (language == "ta") "சந்திராஷ்டம ராசி" else "Chandrashtama Rasi",
                            value = if (language == "ta") panchang.chandrashtamaRasiTamil else panchang.chandrashtamaRasiEnglish
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailTextColumn(
                            label = if (language == "ta") "சூரிய ராசி" else "Sun Sign",
                            value = if (language == "ta") panchang.sunSignTamil else panchang.sunSignEnglish
                        )
                        DetailTextColumn(
                            label = if (language == "ta") "சந்திர ராசி" else "Moon Sign",
                            value = if (language == "ta") panchang.moonSignTamil else panchang.moonSignEnglish
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailTextColumn(
                            label = if (language == "ta") "நேத்திரம்" else "Nethiram",
                            value = "${panchang.nethiram}"
                        )
                        DetailTextColumn(
                            label = if (language == "ta") "ஜீவன்" else "Jeevan",
                            value = panchang.jeevan
                        )
                    }
                }
            }
        }

        // Save Panchangam Button with Note
        item {
            OutlinedButton(
                onClick = { showNoteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_note_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Add Note"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == "ta") "இந்த நாளுக்கு குறிப்பு சேர்க்க / சேமிக்க" else "Add Note & Save to Records"
                )
            }
        }
    }

    // Add Note Dialog
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text(if (language == "ta") "குறிப்பு சேர்க்க" else "Add Personal Note") },
            text = {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text(if (language == "ta") "குறிப்புகள் / நினைவூட்டல்" else "Personal Notes/Reminder") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSaveNoteRequested(noteInput)
                    showNoteDialog = false
                    noteInput = ""
                }) {
                    Text(if (language == "ta") "சேமி" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text(if (language == "ta") "ரத்து" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SunMoonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun AngamCard(
    title: String,
    mainValue: String,
    badgeText: String,
    subDetails: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Text(
                    text = mainValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subDetails,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimingSectionCard(
    title: String,
    isAuspicious: Boolean,
    items: List<Pair<String, String>>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAuspicious) AuspiciousGreen.copy(alpha = 0.06f) else InauspiciousRed.copy(alpha = 0.06f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAuspicious) AuspiciousGreen.copy(alpha = 0.3f) else InauspiciousRed.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAuspicious) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = title,
                    tint = if (isAuspicious) AuspiciousGreen else InauspiciousRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isAuspicious) AuspiciousGreen else InauspiciousRed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTextColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
