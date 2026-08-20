package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.PanchangResult
import com.example.ui.theme.AuspiciousGreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

val FastingAmber = Color(0xFFE65100)
val FastingContainer = Color(0xFFFFF3E0)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyCalendarScreen(
    currentSelectedDate: LocalDate,
    currentCity: CityLocation,
    language: String,
    onDateSelected: (LocalDate) -> Unit
) {
    var yearMonth by remember { mutableStateOf(YearMonth.from(currentSelectedDate)) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MUHURTHAM, VRATHAM

    val baseMonth = remember { YearMonth.of(2000, 1) }
    val initialPage = remember { ChronoUnit.MONTHS.between(baseMonth, yearMonth).toInt() }
    val monthPagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2400 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(monthPagerState.currentPage) {
        val targetYearMonth = baseMonth.plusMonths(monthPagerState.currentPage.toLong())
        if (yearMonth != targetYearMonth) {
            yearMonth = targetYearMonth
        }
    }

    LaunchedEffect(yearMonth) {
        val targetPage = ChronoUnit.MONTHS.between(baseMonth, yearMonth).toInt()
        if (monthPagerState.currentPage != targetPage) {
            monthPagerState.animateScrollToPage(targetPage)
        }
    }

    HorizontalPager(
        state = monthPagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val pageYearMonth = baseMonth.plusMonths(page.toLong())

        MonthlyCalendarPageContent(
            yearMonth = pageYearMonth,
            currentSelectedDate = currentSelectedDate,
            currentCity = currentCity,
            language = language,
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it },
            onDateSelected = onDateSelected,
            onPreviousMonth = {
                coroutineScope.launch {
                    if (monthPagerState.currentPage > 0) {
                        monthPagerState.animateScrollToPage(monthPagerState.currentPage - 1)
                    }
                }
            },
            onNextMonth = {
                coroutineScope.launch {
                    monthPagerState.animateScrollToPage(monthPagerState.currentPage + 1)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonthlyCalendarPageContent(
    yearMonth: YearMonth,
    currentSelectedDate: LocalDate,
    currentCity: CityLocation,
    language: String,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sun, 6 = Sat

    val daysList = remember(yearMonth) {
        val list = mutableListOf<LocalDate?>()
        for (i in 0 until firstDayOfWeek) {
            list.add(null)
        }
        for (d in 1..daysInMonth) {
            list.add(yearMonth.atDay(d))
        }
        list
    }

    // Precalculate month panchang list
    val monthPanchangList = remember(yearMonth, currentCity) {
        (1..daysInMonth).map { day ->
            val date = yearMonth.atDay(day)
            Pair(date, ThiruGanithaEngine.calculatePanchang(date, currentCity))
        }
    }

    val monthlyMuhurthamList = remember(monthPanchangList) {
        monthPanchangList.filter { it.second.isSubhaMuhurtham }
    }

    val monthlyVrathamList = remember(monthPanchangList) {
        monthPanchangList.filter { it.second.isFastingDay }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Month Header with Navigation
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousMonth,
                            modifier = Modifier.testTag("prev_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Month"
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val pStart = ThiruGanithaEngine.calculatePanchang(yearMonth.atDay(1), currentCity)
                            val pEnd = ThiruGanithaEngine.calculatePanchang(yearMonth.atDay(yearMonth.lengthOfMonth()), currentCity)
                            
                            val gregMonthTamil = when (yearMonth.monthValue) {
                                1 -> "ஜனவரி"; 2 -> "பிப்ரவரி"; 3 -> "மார்ச்"; 4 -> "ஏப்ரல்"
                                5 -> "மே"; 6 -> "ஜூன்"; 7 -> "ஜூலை"; 8 -> "ஆகஸ்ட்"
                                9 -> "செப்டம்பர்"; 10 -> "அக்டோபர்"; 11 -> "நவம்பர்"; 12 -> "டிசம்பர்"
                                else -> ""
                            }

                            val tamilMonthsSpanTa = if (pStart.tamilMonthTamil == pEnd.tamilMonthTamil) pStart.tamilMonthTamil else "${pStart.tamilMonthTamil} - ${pEnd.tamilMonthTamil}"
                            val tamilMonthsSpanEn = if (pStart.tamilMonthEnglish == pEnd.tamilMonthEnglish) pStart.tamilMonthEnglish else "${pStart.tamilMonthEnglish} - ${pEnd.tamilMonthEnglish}"

                            Text(
                                text = if (language == "ta")
                                    "$gregMonthTamil ${yearMonth.year} ($tamilMonthsSpanTa | ${pStart.tamilYearTamil} வருடம்)"
                                else
                                    "${yearMonth.month} ${yearMonth.year} ($tamilMonthsSpanEn | ${pStart.tamilYearEnglish} Year)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier.testTag("next_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Month"
                            )
                        }
                    }

                    // Modern Month Slide Hint Badge
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Slide Month",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "ta")
                                    "👈 ஸ்வைப் செய்து அடுத்த / முந்தைய மாதத்திற்கு செல்லலாம் 👉"
                                else
                                    "👈 Swipe left or right for next / previous month 👉",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Traditional Tamil Symbol Legend Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem("💍", if (language == "ta") "முகூர்த்தம்" else "Muhurtham", AuspiciousGreen)
                    LegendItem("🐘", if (language == "ta") "சதுர்த்தி" else "Chaturthi", FastingAmber)
                    LegendItem("🔱", if (language == "ta") "சஷ்டி/பிரதோஷம்" else "Sashti/Pradosham", FastingAmber)
                    LegendItem("🐚", if (language == "ta") "ஏகாதசி" else "Ekadashi", FastingAmber)
                    LegendItem("🪔", if (language == "ta") "கார்த்திகை" else "Karthigai", FastingAmber)
                    LegendItem("🌕/🌑", if (language == "ta") "பௌர்ணமி/அமாவாசை" else "Full/New Moon", MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { onFilterChange("ALL") },
                    label = { Text(if (language == "ta") "அனைத்தும்" else "All Days") }
                )
                FilterChip(
                    selected = selectedFilter == "MUHURTHAM",
                    onClick = { onFilterChange("MUHURTHAM") },
                    label = { Text(if (language == "ta") "💍 சுப முகூர்த்தம் (${monthlyMuhurthamList.size})" else "💍 Muhurtham (${monthlyMuhurthamList.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDCEDC8),
                        selectedLabelColor = AuspiciousGreen
                    )
                )
                FilterChip(
                    selected = selectedFilter == "VRATHAM",
                    onClick = { onFilterChange("VRATHAM") },
                    label = { Text(if (language == "ta") "🕉️ விரத நாட்கள் (${monthlyVrathamList.size})" else "🕉️ Fasting Days (${monthlyVrathamList.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FastingContainer,
                        selectedLabelColor = FastingAmber
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Days of week header (ஞா, தி, செ, பு, வியா, வெ, சனி)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val daysHeaderTamil = listOf("ஞா", "தி", "செ", "பு", "வி", "வெ", "சனி")
                val daysHeaderEng = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                val headers = if (language == "ta") daysHeaderTamil else daysHeaderEng

                headers.forEach { h ->
                    Text(
                        text = h,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Calendar Grid
        item {
            val rows = daysList.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0 until 7) {
                            val date = rowDays.getOrNull(i)
                            Box(modifier = Modifier.weight(1f)) {
                                if (date != null) {
                                    val p = ThiruGanithaEngine.calculatePanchang(date, currentCity)
                                    val isToday = date == LocalDate.now()
                                    val isSelected = date == currentSelectedDate

                                    CalendarDayCell(
                                        date = date,
                                        panchang = p,
                                        isToday = isToday,
                                        isSelected = isSelected,
                                        activeFilter = selectedFilter,
                                        onClick = { onDateSelected(date) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.aspectRatio(0.85f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Monthly Marriage Subha Muhurtham Summary Card
        if (selectedFilter == "ALL" || selectedFilter == "MUHURTHAM") {
            item {
                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AuspiciousGreen.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💍 ", fontSize = 18.sp)
                            Text(
                                text = if (language == "ta")
                                    "இந்த மாத திருமண சுப முகூர்த்த நாட்கள் (${monthlyMuhurthamList.size} நாட்கள்)"
                                else
                                    "Marriage Subha Muhurtham Days This Month (${monthlyMuhurthamList.size} Days)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AuspiciousGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (monthlyMuhurthamList.isEmpty()) {
                            Text(
                                text = if (language == "ta") "இந்த மாதத்தில் சுப முகூர்த்த நாட்கள் இல்லை." else "No Subha Muhurtham days found in this month.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                monthlyMuhurthamList.forEach { (mDate, p) ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onDateSelected(mDate) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AuspiciousGreen.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("💍 ", fontSize = 16.sp)
                                                Column {
                                                    Text(
                                                        text = if (language == "ta")
                                                            "${p.tamilMonthTamil} ${p.tamilDay} (${mDate.dayOfMonth} ${mDate.monthValue.let { when(it){1->"ஜன";2->"பிப்";3->"மார்";4->"ஏப்";5->"மே";6->"ஜூன்";7->"ஜூலை";8->"ஆக";9->"செப்";10->"அக்";11->"நவ";else->"டிச"} }}) - ${p.vaara.nameTamil}"
                                                        else
                                                            "${p.tamilMonthEnglish} ${p.tamilDay} (${mDate.dayOfMonth} ${mDate.month.name.lowercase().take(3)}) - ${p.vaara.nameEnglish}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = if (language == "ta")
                                                            "நட்சத்திரம்: ${p.nakshatra.nameTamil} | திதி: ${p.tithi.nameTamil}"
                                                        else
                                                            "Nakshatra: ${p.nakshatra.nameEnglish} | Tithi: ${p.tithi.nameEnglish}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            AssistChip(
                                                onClick = { onDateSelected(mDate) },
                                                label = { Text(if (language == "ta") "பார்க்க" else "View", fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Monthly Fasting Days (விரத நாட்கள்) Summary Card with Day-Specific Symbols
        if (selectedFilter == "ALL" || selectedFilter == "VRATHAM") {
            item {
                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FastingContainer),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FastingAmber.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🕉️ ", fontSize = 18.sp)
                            Text(
                                text = if (language == "ta")
                                    "இந்த மாத விரத நாட்கள் (${monthlyVrathamList.size} நாட்கள்)"
                                else
                                    "Fasting & Holy Days This Month (${monthlyVrathamList.size} Days)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FastingAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (monthlyVrathamList.isEmpty()) {
                            Text(
                                text = if (language == "ta") "இந்த மாதத்தில் விரத நாட்கள் இல்லை." else "No Fasting days found in this month.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                monthlyVrathamList.forEach { (vDate, p) ->
                                    val symbol = when {
                                        p.isChaturthi -> "🐘"
                                        p.isSashti -> "🔱"
                                        p.isPradosham -> "🔱"
                                        p.isEkadashi -> "🐚"
                                        p.isKarthigai -> "🪔"
                                        p.isPurnima -> "🌕"
                                        p.isAmavasya -> "🌑"
                                        else -> "🕉️"
                                    }

                                    val vrathamName = if (language == "ta") {
                                        p.specialEventsTamil.filter { !it.contains("திருமண") }.joinToString(", ")
                                    } else {
                                        p.specialEventsEnglish.filter { !it.contains("Marriage") }.joinToString(", ")
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onDateSelected(vDate) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, FastingAmber.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "$symbol ",
                                                    fontSize = 16.sp
                                                )
                                                Column {
                                                    Text(
                                                        text = if (language == "ta")
                                                            "${p.tamilMonthTamil} ${p.tamilDay} (${vDate.dayOfMonth} ${vDate.monthValue.let { when(it){1->"ஜன";2->"பிப்";3->"மார்";4->"ஏப்";5->"மே";6->"ஜூன்";7->"ஜூலை";8->"ஆக";9->"செப்";10->"அக்";11->"நவ";else->"டிச"} }}) - $vrathamName"
                                                        else
                                                            "${p.tamilMonthEnglish} ${p.tamilDay} (${vDate.dayOfMonth} ${vDate.month.name.lowercase().take(3)}) - $vrathamName",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = if (language == "ta")
                                                            "திதி: ${p.tithi.nameTamil} (${p.tithi.pakshaTamil}) | நட்சத்திரம்: ${p.nakshatra.nameTamil}"
                                                        else
                                                            "Tithi: ${p.tithi.nameEnglish} | Nakshatra: ${p.nakshatra.nameEnglish}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            AssistChip(
                                                onClick = { onDateSelected(vDate) },
                                                label = { Text(if (language == "ta") "பார்க்க" else "View", fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(symbol: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$symbol ", fontSize = 13.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    panchang: PanchangResult,
    isToday: Boolean,
    isSelected: Boolean,
    activeFilter: String,
    onClick: () -> Unit
) {
    val isMuhurtham = panchang.isSubhaMuhurtham
    val isFasting = panchang.isFastingDay

    val isHighlighted = when (activeFilter) {
        "MUHURTHAM" -> isMuhurtham
        "VRATHAM" -> isFasting
        else -> isMuhurtham || isFasting
    }

    val isDimmed = activeFilter != "ALL" && !isHighlighted

    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        activeFilter == "MUHURTHAM" && isMuhurtham -> Color(0xFFDCEDC8) // Strong Green highlight
        activeFilter == "VRATHAM" && isFasting -> FastingContainer // Strong Saffron/Gold highlight
        isMuhurtham -> Color(0xFFF1F8E9) // Light warm green
        isFasting -> Color(0xFFFFF8E1) // Light warm yellow/gold
        isDimmed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.secondary
        activeFilter == "MUHURTHAM" && isMuhurtham -> AuspiciousGreen
        activeFilter == "VRATHAM" && isFasting -> FastingAmber
        isMuhurtham -> AuspiciousGreen.copy(alpha = 0.8f)
        isFasting -> FastingAmber.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier
            .aspectRatio(0.82f)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(if (isHighlighted || isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Gregorian Date & Marriage Ring Symbol if Muhurtham
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${date.dayOfMonth}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Bold,
                    fontSize = 15.sp,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        isDimmed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        activeFilter == "MUHURTHAM" && isMuhurtham -> AuspiciousGreen
                        activeFilter == "VRATHAM" && isFasting -> FastingAmber
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                if (isMuhurtham) {
                    Text("💍", fontSize = 11.sp)
                }
            }

            // Tamil Solar Month & Date (e.g., ஆடி 28)
            Text(
                text = "${panchang.tamilMonthTamil.take(3)} ${panchang.tamilDay}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
                    isDimmed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    else -> MaterialTheme.colorScheme.primary
                }
            )

            // Dynamic Day-Specific Symbols / Indicators (Display ALL applicable fasting marks)
            val fastingSymbols = remember(panchang) {
                val list = mutableListOf<String>()
                if (panchang.isChaturthi) list.add("🐘")
                if (panchang.isSashti || panchang.isPradosham) list.add("🔱")
                if (panchang.isEkadashi) list.add("🐚")
                if (panchang.isKarthigai) list.add("🪔")
                if (panchang.isPurnima) list.add("🌕")
                if (panchang.isAmavasya) list.add("🌑")
                if (panchang.isFastingDay && list.isEmpty()) list.add("🕉️")
                list
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                fastingSymbols.forEach { sym ->
                    Text(text = sym, fontSize = 9.sp)
                }

                if (isMuhurtham && fastingSymbols.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(AuspiciousGreen)
                    )
                }
            }
        }
    }
}
