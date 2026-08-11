package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
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
import com.example.ui.theme.AuspiciousGreen
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyCalendarScreen(
    currentSelectedDate: LocalDate,
    currentCity: CityLocation,
    language: String,
    onDateSelected: (LocalDate) -> Unit
) {
    var yearMonth by remember { mutableStateOf(YearMonth.from(currentSelectedDate)) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MUHURTHAM, VRA THAM

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Month Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { yearMonth = yearMonth.minusMonths(1) },
                    modifier = Modifier.testTag("prev_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month"
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val samplePanchang = ThiruGanithaEngine.calculatePanchang(yearMonth.atDay(15), currentCity)
                    Text(
                        text = if (language == "ta")
                            "${yearMonth.month.value} - ${yearMonth.year} (${samplePanchang.tamilMonthTamil} - ${samplePanchang.tamilYearTamil})"
                        else
                            "${yearMonth.month} ${yearMonth.year} (${samplePanchang.tamilMonthEnglish})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = { yearMonth = yearMonth.plusMonths(1) },
                    modifier = Modifier.testTag("next_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Filter Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text(if (language == "ta") "அனைத்தும்" else "All Days") }
            )
            FilterChip(
                selected = selectedFilter == "MUHURTHAM",
                onClick = { selectedFilter = "MUHURTHAM" },
                label = { Text(if (language == "ta") "சுப முகூர்த்தம்" else "Muhurtham Days") }
            )
            FilterChip(
                selected = selectedFilter == "VRATHAM",
                onClick = { selectedFilter = "VRATHAM" },
                label = { Text(if (language == "ta") "விரத நாட்கள்" else "Fasting Days") }
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

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(daysList) { date ->
                if (date == null) {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                } else {
                    val p = ThiruGanithaEngine.calculatePanchang(date, currentCity)
                    val isToday = date == LocalDate.now()
                    val isSelected = date == currentSelectedDate

                    val matchesFilter = when (selectedFilter) {
                        "MUHURTHAM" -> p.isSubhaMuhurtham
                        "VRATHAM" -> p.isAmavasya || p.isPurnima || p.isPradosham || p.isEkadashi || p.isKarthigai
                        else -> true
                    }

                    CalendarDayCell(
                        date = date,
                        panchang = p,
                        isToday = isToday,
                        isSelected = isSelected,
                        matchesFilter = matchesFilter,
                        onClick = { onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    panchang: com.example.model.PanchangResult,
    isToday: Boolean,
    isSelected: Boolean,
    matchesFilter: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(0.9f)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isToday -> MaterialTheme.colorScheme.secondaryContainer
                matchesFilter -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${panchang.tamilDay}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
            )

            // Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (panchang.isSubhaMuhurtham) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(AuspiciousGreen)
                    )
                }
                if (panchang.isAmavasya || panchang.isPurnima) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
                if (panchang.isPradosham || panchang.isEkadashi) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8E24AA))
                    )
                }
            }
        }
    }
}
