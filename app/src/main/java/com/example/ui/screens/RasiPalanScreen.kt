package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
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
import com.example.engine.RasiPalanEngine
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.RasiPalanItem
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RasiPalanScreen(
    selectedDate: LocalDate,
    currentCity: CityLocation,
    language: String,
    onDateSelected: (LocalDate) -> Unit
) {
    var mode by remember { mutableStateOf("DAILY") } // "DAILY" or "MONTHLY"
    var selectedRasiIndex by remember { mutableStateOf<Int?>(null) } // null = All Rasis

    val panchang = remember(selectedDate, currentCity) {
        ThiruGanithaEngine.calculatePanchang(selectedDate, currentCity)
    }

    val dailyRasiList = remember(selectedDate, currentCity) {
        RasiPalanEngine.calculateDailyRasiPalan(selectedDate, currentCity)
    }

    val currentYearMonth = remember(selectedDate) { YearMonth.from(selectedDate) }
    val monthlyRasiList = remember(currentYearMonth, currentCity) {
        RasiPalanEngine.calculateMonthlyRasiPalan(currentYearMonth, currentCity)
    }

    val activeList = if (mode == "DAILY") dailyRasiList else monthlyRasiList
    val displayedList = if (selectedRasiIndex != null) {
        activeList.filter { it.rasiIndex == selectedRasiIndex }
    } else {
        activeList
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode & Navigation Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (mode == "DAILY") {
                                    onDateSelected(selectedDate.minusDays(1))
                                } else {
                                    onDateSelected(selectedDate.minusMonths(1))
                                }
                            }
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Rasi Palan",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == "ta") "ராசி பலன்" else "Rasi Palan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = if (mode == "DAILY") {
                                    if (language == "ta")
                                        "${panchang.dateDisplayTamil} | ${panchang.gregorianDateDisplayTamil}"
                                    else
                                        "${panchang.dateDisplayEnglish} | ${panchang.gregorianDateDisplayEnglish}"
                                } else {
                                    if (language == "ta")
                                        "${panchang.tamilMonthTamil} மாத பலன் (${panchang.tamilYearTamil} வருடம்)"
                                    else
                                        "${panchang.tamilMonthEnglish} Month Horoscope (${panchang.tamilYearEnglish} Year)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                if (mode == "DAILY") {
                                    onDateSelected(selectedDate.plusDays(1))
                                } else {
                                    onDateSelected(selectedDate.plusMonths(1))
                                }
                            }
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Daily vs Monthly Segmented Switch
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = mode == "DAILY",
                            onClick = { mode = "DAILY" },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == "ta") "இன்றைய பலன்" else "Daily Horoscope", fontSize = 12.sp)
                            }
                        }
                        SegmentedButton(
                            selected = mode == "MONTHLY",
                            onClick = { mode = "MONTHLY" },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == "ta") "மாத பலன்" else "Monthly Horoscope", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Rasi Selector Chips (12 Rasis + All)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedRasiIndex == null,
                        onClick = { selectedRasiIndex = null },
                        label = { Text(if (language == "ta") "அனைத்து 12 ராசிகள்" else "All 12 Rasis") },
                        modifier = Modifier.testTag("all_rasis_chip")
                    )
                }
                items(dailyRasiList) { item ->
                    FilterChip(
                        selected = selectedRasiIndex == item.rasiIndex,
                        onClick = {
                            selectedRasiIndex = if (selectedRasiIndex == item.rasiIndex) null else item.rasiIndex
                        },
                        label = {
                            Text("${item.symbol} ${if (language == "ta") item.nameTamil else item.nameEnglish}")
                        },
                        modifier = Modifier.testTag("rasi_chip_${item.rasiIndex}")
                    )
                }
            }
        }

        // Rasi Cards List
        items(displayedList) { item ->
            RasiPalanCard(
                item = item,
                language = language
            )
        }
    }
}

@Composable
fun RasiPalanCard(
    item: RasiPalanItem,
    language: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rasi_card_${item.rasiIndex}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChandrashtama)
                Color(0xFFFFEBEE)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Emoji Symbol + Rasi Name + Rating Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.symbol,
                                fontSize = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == "ta") item.nameTamil else item.nameEnglish,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == "ta") "அதிபதி: ${item.lordTamil}" else "Lord: ${item.lordEnglish}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(item.ratingStars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Chandrashtama Alert Badge if applicable
            if (item.isChandrashtama) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFD32F2F)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "ta") "⚠️ இன்று சந்திராஷ்டமம் எச்சரிக்கை நாள்!" else "⚠️ Chandrashtama Caution Day!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // General Prediction
            Text(
                text = if (language == "ta") item.generalPredictionTa else item.generalPredictionEn,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            // Finance & Career Section
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = if (language == "ta") "💰 தொழில் / தனம்: " else "💰 Career / Finance: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (language == "ta") item.financeCareerTa else item.financeCareerEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Family & Health Section
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = if (language == "ta") "❤️ குடும்பம் / ஆரோக்கியம்: " else "❤️ Family & Health: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (language == "ta") item.familyHealthTa else item.familyHealthEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Lucky Factors Badge Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (language == "ta") "எண்: ${item.luckyNumber}" else "No: ${item.luckyNumber}",
                            fontSize = 11.sp
                        )
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (language == "ta") "நிறம்: ${item.luckyColorTa}" else "Color: ${item.luckyColorEn}",
                            fontSize = 11.sp
                        )
                    }
                )
            }

            // Remedy Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🪔 ",
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (language == "ta") "பரிகாரம்: ${item.remedyTa}" else "Remedy: ${item.remedyEn}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
