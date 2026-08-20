package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.TithiEngine
import com.example.model.CityLocation
import com.example.model.TithiDetail
import java.time.LocalDate

@Composable
fun TithiDetailDialog(
    selectedDate: LocalDate,
    city: CityLocation,
    language: String,
    onDismissRequest: () -> Unit
) {
    val tithiDetail = remember(selectedDate, city) {
        TithiEngine.calculateDailyTithi(selectedDate, city)
    }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Today's Tithi, 1 = 30 Tithis Directory

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brightness2,
                                contentDescription = "Tithi Moon Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == "ta") "திதி முழு விபரங்கள்" else "Tithi Details & Lifecycle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == "ta") "தொடக்க & முடிவு நேரம், அதிதேவதை, பலன்கள்" else "Start, End, Deity & Auspicious Times",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = if (language == "ta") "இன்றைய திதி" else "Today's Tithi",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = if (language == "ta") "30 திதிகள் பட்டியல்" else "30 Tithis Guide",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    TodayTithiView(detail = tithiDetail, language = language)
                } else {
                    All30TithiListView(language = language)
                }
            }
        }
    }
}

@Composable
private fun TodayTithiView(detail: TithiDetail, language: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Live Status Banner if finished
            if (detail.isFinished) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (language == "ta") "⚡ நடப்பு திதி (ACTIVE TITHI NOW)" else "⚡ ACTIVE TITHI NOW",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2E7D32)
                            ) {
                                Text(
                                    text = if (language == "ta") "தற்போது" else "LIVE",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == "ta")
                                "${detail.currentLivePakshaTamil} ${detail.currentLiveNameTamil} • அதிதேவதை: ${detail.currentLiveDeityTamil}"
                            else
                                "${detail.currentLivePakshaEnglish} ${detail.currentLiveNameEnglish} • Deity: ${detail.currentLiveDeityEnglish}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == "ta")
                                "காலை சூரியோதயத்தில் நிலவிய ${detail.pakshaTamil} ${detail.nameTamil} ${detail.endTime} முடிவுற்றது. தற்போது ${detail.currentLiveNameTamil} இயங்குகிறது."
                            else
                                "Sunrise ${detail.nameEnglish} ended at ${detail.endTime}. Currently active: ${detail.currentLiveNameEnglish}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        item {
            // Main Highlight Card (Sunrise / Udaya Tithi)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (detail.isFinished) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == "ta") "${detail.pakshaTamil} ${detail.nameTamil} (திதி: ${detail.index})" else "${detail.pakshaEnglish} ${detail.nameEnglish} (#${detail.index})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (detail.isFinished) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (detail.isFinished) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = if (language == "ta") detail.categoryTamil.split(" ")[0] else detail.categoryEnglish.split(" ")[0],
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "ta") "அதிதேவதை: ${detail.deityTamil} • தத்துவம்: ${detail.elementTamil}" else "Deity: ${detail.deityEnglish} • Element: ${detail.elementEnglish}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (detail.isFinished) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            // Lifecycle Card (Start & End time)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (language == "ta") "⏰ திதி கால அளவு & நேரங்கள்" else "⏰ Tithi Lifecycle & Timings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Start Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "ta") "தொடங்கிய நேரம் (Start Time)" else "Start Time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = detail.startTime,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // End Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "ta") "முடிவு நேரம் (End Time)" else "End Time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = detail.endTime,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "ta") "மொத்த கால அளவு" else "Total Duration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (language == "ta") "~${detail.durationHours} மணி நேரங்கள்" else "~${detail.durationHours} Hours",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            // Auspicious Activities & Vratham
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (language == "ta") "🌟 சுபகாரிய பலன்கள் & வழிபாடு" else "🌟 Auspicious Deeds & Spiritual Merit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == "ta") "திதி குணம் (Nature)" else "Tithi Nature",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (language == "ta") detail.categoryTamil else detail.categoryEnglish,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Auspicious
                    Text(
                        text = if (language == "ta") "செய்யத்தக்க சுபகாரியங்கள்:" else "Recommended Activities:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (language == "ta") detail.auspiciousActivitiesTamil else detail.auspiciousActivitiesEnglish,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Vratham
                    Text(
                        text = if (language == "ta") "விரத பலன்கள் & வழிபாடு:" else "Fasting / Pooja Significance:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD84315)
                    )
                    Text(
                        text = if (language == "ta") detail.vrathamSignificanceTamil else detail.vrathamSignificanceEnglish,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            // Next Tithi Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == "ta") "அடுத்த திதி (Next Tithi)" else "Next Upcoming Tithi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = if (language == "ta") "${detail.nextPakshaTamil} ${detail.nextTithiTamil}" else "${detail.nextPakshaEnglish} ${detail.nextTithiEnglish}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Arrow",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun All30TithiListView(language: String) {
    val tithis = remember {
        val list = mutableListOf<TithiItemData>()
        val pakshas = listOf(Pair("வளர்பிறை", "Sukla Paksha"), Pair("தேய்பிறை", "Krishna Paksha"))
        val baseNames = listOf(
            Pair("பிரதமை", "Prathama"),
            Pair("துவிதியை", "Dvitiya"),
            Pair("திருதியை", "Tritiya"),
            Pair("சதுர்த்தி", "Chaturthi"),
            Pair("பஞ்சமி", "Panchami"),
            Pair("சஷ்டி", "Sashti"),
            Pair("சப்தமி", "Saptami"),
            Pair("அஷ்டமி", "Ashtami"),
            Pair("நவமி", "Navami"),
            Pair("தசமி", "Dasami"),
            Pair("ஏகாதசி", "Ekadashi"),
            Pair("துவாதசி", "Dvadasi"),
            Pair("திரயோதசி", "Trayodasi"),
            Pair("சதுர்த்தசி", "Chaturdasi"),
            Pair("பௌர்ணமி", "Pournami")
        )

        for (p in 0..1) {
            val isSukla = p == 0
            val pNameTa = pakshas[p].first
            val pNameEn = pakshas[p].second
            for (i in 0..14) {
                val index = p * 15 + i + 1
                val nameTa = if (!isSukla && i == 14) "அமாவாசை" else baseNames[i].first
                val nameEn = if (!isSukla && i == 14) "Amavasya" else baseNames[i].second
                val catTa = when (i % 5) {
                    0 -> "நந்தா (ஆனந்தம்)"
                    1 -> "பத்ரா (சுபம்)"
                    2 -> "ஜயா (வெற்றி)"
                    3 -> "ரிக்தா (விலக்குக)"
                    else -> "பூர்ணா (முழுமை)"
                }
                val catEn = when (i % 5) {
                    0 -> "Nanda (Joyous)"
                    1 -> "Bhadra (Auspicious)"
                    2 -> "Jaya (Victory)"
                    3 -> "Rikta (Avoid)"
                    else -> "Purna (Complete)"
                }
                list.add(TithiItemData(index, nameTa, nameEn, pNameTa, pNameEn, catTa, catEn))
            }
        }
        list
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tithis) { tithi ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${tithi.index}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == "ta") "${tithi.pakshaTa} ${tithi.nameTa}" else "${tithi.pakshaEn} ${tithi.nameEn}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == "ta") tithi.categoryTa else tithi.categoryEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class TithiItemData(
    val index: Int,
    val nameTa: String,
    val nameEn: String,
    val pakshaTa: String,
    val pakshaEn: String,
    val categoryTa: String,
    val categoryEn: String
)
