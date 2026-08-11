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
import androidx.compose.material.icons.filled.Schedule
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GowriHoraiScreen(
    panchang: PanchangResult,
    language: String
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Gowri, 1 = Subha Horai

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
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
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                modifier = Modifier.testTag("horai_tab")
            ) {
                Text(
                    text = if (language == "ta") "சுப ஹோரை" else "Subha Horai",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            Text(
                text = if (language == "ta")
                    "இன்றைய கௌரி பஞ்சாங்கம் (${panchang.vaara.nameTamil})"
                else
                    "Today's Gowri Panchangam (${panchang.vaara.nameEnglish})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(panchang.gowriDayList) { item ->
                    GowriItemCard(item, language)
                }
            }
        } else {
            Text(
                text = if (language == "ta")
                    "இன்றைய சுப ஹோரை அட்டவணை (${panchang.vaara.nameTamil})"
                else
                    "Today's Subha Horai Schedule (${panchang.vaara.nameEnglish})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(panchang.horaiDayList) { item ->
                    HoraiItemCard(item, language)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun HoraiItemCard(item: HoraiPeriod, language: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isGood) AuspiciousGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isGood) AuspiciousGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (item.isGood) AuspiciousGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == "ta") "${item.planetTamil} ஹோரை" else "${item.planetEnglish} Horai",
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
