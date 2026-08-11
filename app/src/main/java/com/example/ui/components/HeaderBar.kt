package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.model.CityLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBar(
    currentCity: CityLocation,
    language: String,
    isSaved: Boolean,
    onCitySelected: (CityLocation) -> Unit,
    onToggleLanguage: () -> Unit,
    onSaveToggle: () -> Unit
) {
    var showCityMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == "ta") "திரு கணித பஞ்சாங்கம்" else "Thiru Ganitha Panchangam",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = if (language == "ta") "அஸ்ட்ரானமி கணிப்பு engine & REST API" else "Drik Astronomical Engine & REST API",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Toggle
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier.testTag("lang_toggle_button")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "ta") "EN" else "தம",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Save / Bookmark button
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.testTag("bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Panchang",
                            tint = if (isSaved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // City Selection Chip
            Box {
                Surface(
                    onClick = { showCityMenu = true },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("city_selector_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "ta") "${currentCity.nameTamil} (${currentCity.nameEnglish})" else "${currentCity.nameEnglish} (${currentCity.nameTamil})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                DropdownMenu(
                    expanded = showCityMenu,
                    onDismissRequest = { showCityMenu = false }
                ) {
                    CityLocation.DEFAULT_CITIES.forEach { city ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (language == "ta") "${city.nameTamil} - ${city.nameEnglish}" else "${city.nameEnglish} - ${city.nameTamil}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onCitySelected(city)
                                showCityMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
