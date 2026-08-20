package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GowriPeriod
import com.example.model.HoraiPeriod
import com.example.model.PanchangResult
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.InauspiciousRed
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GowriHoraiScreen(
    panchang: PanchangResult,
    language: String
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = Gowri, 1 = Subha Horai
    var selectedTimeFilter by remember { mutableIntStateOf(0) } // 0 = Day (பகல்), 1 = Night (இரவு), 2 = All (அனைத்தும்)

    val now = LocalTime.now()
    val currentMinutes = now.hour * 60 + now.minute
    val all24Horai = panchang.horaiDayList + panchang.horaiNightList
    val currentHorai = all24Horai.firstOrNull { horai ->
        val start = horai.startMinutes
        val end = horai.endMinutes
        val normCurrent = if (currentMinutes < (panchang.horaiDayList.firstOrNull()?.startMinutes ?: 360)) {
            currentMinutes + 1440
        } else {
            currentMinutes
        }
        normCurrent in start until end
    } ?: all24Horai.getOrNull((now.hour - 6 + 24) % 24)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Main Tab Selector (கௌரி பஞ்சாங்கம் / சுப ஹோரை)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                modifier = Modifier.testTag("gowri_tab")
            ) {
                Text(
                    text = if (language == "ta") "கௌரி பஞ்சாங்கம்" else "Gowri Panchangam",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            SegmentedButton(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                modifier = Modifier.testTag("horai_tab")
            ) {
                Text(
                    text = if (language == "ta") "சுப ஹோரை (24 மணி)" else "Subha Horai (24 Hr)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-filter (பகல் / இரவு / முழு அட்டவணை)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTimeFilter == 0,
                onClick = { selectedTimeFilter = 0 },
                label = {
                    Text(if (language == "ta") "பகல் (Day)" else "Day")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedTimeFilter == 1,
                onClick = { selectedTimeFilter = 1 },
                label = {
                    Text(if (language == "ta") "இரவு (Night)" else "Night")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedTimeFilter == 2,
                onClick = { selectedTimeFilter = 2 },
                label = {
                    Text(if (language == "ta") "முழுவதும்" else "24h All")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMainTab == 0) {
            // Gowri Panchangam
            val gowriList = when (selectedTimeFilter) {
                0 -> panchang.gowriDayList
                1 -> panchang.gowriNightList
                else -> panchang.gowriDayList + panchang.gowriNightList
            }

            Text(
                text = if (language == "ta")
                    "கௌரி பஞ்சாங்கம் - ${panchang.vaara.nameTamil} (${if (selectedTimeFilter == 0) "பகல்" else if (selectedTimeFilter == 1) "இரவு" else "முழுவதும்"})"
                else
                    "Gowri Panchangam - ${panchang.vaara.nameEnglish} (${if (selectedTimeFilter == 0) "Day" else if (selectedTimeFilter == 1) "Night" else "Full Day"})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(gowriList) { item ->
                    GowriItemCard(item, language)
                }
            }
        } else {
            // Subha Horai
            val horaiList = when (selectedTimeFilter) {
                0 -> panchang.horaiDayList
                1 -> panchang.horaiNightList
                else -> panchang.horaiDayList + panchang.horaiNightList
            }

            // Current Horai Summary Pill
            if (currentHorai != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == "ta") "தற்போதைய நடப்பு ஓரை" else "Current Active Horai",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = if (language == "ta") "${currentHorai.planetTamil} ஓரை" else "${currentHorai.planetEnglish} Horai",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentHorai.isGood) AuspiciousGreen else InauspiciousRed
                        ) {
                            Text(
                                text = if (language == "ta") currentHorai.qualityTamil else currentHorai.qualityEnglish,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = if (language == "ta")
                    "சுப ஓரை அட்டவணை - ${panchang.vaara.nameTamil} (${if (selectedTimeFilter == 0) "பகல் 6 AM - 6 PM" else if (selectedTimeFilter == 1) "இரவு 6 PM - 6 AM" else "24 மணி நேரம்"})"
                else
                    "Subha Horai Schedule - ${panchang.vaara.nameEnglish} (${if (selectedTimeFilter == 0) "Day 6 AM - 6 PM" else if (selectedTimeFilter == 1) "Night 6 PM - 6 AM" else "24 Hours"})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(horaiList) { item ->
                    val isCurrent = currentHorai != null && item.periodIndex == currentHorai.periodIndex
                    HoraiItemCard(item, language, isCurrent)
                }
            }
        }
    }
}

@Composable
private fun GowriItemCard(item: GowriPeriod, language: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isGood) AuspiciousGreen.copy(alpha = 0.08f) else InauspiciousRed.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isGood) AuspiciousGreen.copy(alpha = 0.3f) else InauspiciousRed.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (item.isGood) AuspiciousGreen else InauspiciousRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isGood) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == "ta") item.nameTamil else item.nameEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.timeSlot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.isGood) AuspiciousGreen.copy(alpha = 0.2f) else InauspiciousRed.copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (language == "ta") item.qualityTamil else item.qualityEnglish,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isGood) AuspiciousGreen else InauspiciousRed
                )
            }
        }
    }
}

@Composable
private fun HoraiItemCard(
    item: HoraiPeriod,
    language: String,
    isCurrent: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                item.isGood -> AuspiciousGreen.copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isCurrent) 2.dp else 1.dp,
            when {
                isCurrent -> MaterialTheme.colorScheme.primary
                item.isGood -> AuspiciousGreen.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isCurrent) MaterialTheme.colorScheme.primary else if (item.isGood) AuspiciousGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == "ta") "${item.planetTamil} ஓரை" else "${item.planetEnglish} Horai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = if (language == "ta") "நடப்பு" else "NOW",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = item.timeSlot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.isGood) AuspiciousGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = if (language == "ta") item.qualityTamil else item.qualityEnglish,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isGood) AuspiciousGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
