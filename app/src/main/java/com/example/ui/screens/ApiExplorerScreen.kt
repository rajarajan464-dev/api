package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityLocation
import com.example.ui.theme.AuspiciousGreen
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiExplorerScreen(
    currentEndpoint: String,
    apiResponseText: String,
    apiStatus: Int,
    responseTimeMs: Long,
    selectedDate: LocalDate,
    selectedCity: CityLocation,
    language: String,
    onEndpointChanged: (String) -> Unit,
    onRunApi: () -> Unit
) {
    val context = LocalContext.current
    var showEndpointMenu by remember { mutableStateOf(false) }
    var selectedSnippetTab by remember { mutableIntStateOf(0) } // 0 = cURL, 1 = Kotlin, 2 = Python, 3 = JS

    val endpoints = listOf(
        Pair("/api/v1/panchangam", if (language == "ta") "முழு பஞ்சாங்கம் API" else "Full Panchangam JSON"),
        Pair("/api/v1/tithi", if (language == "ta") "திதி API" else "Tithi JSON"),
        Pair("/api/v1/nakshatra", if (language == "ta") "நட்சத்திரம் API" else "Nakshatra JSON"),
        Pair("/api/v1/gowri", if (language == "ta") "கௌரி பஞ்சாங்கம் API" else "Gowri Panchangam JSON"),
        Pair("/api/v1/horai", if (language == "ta") "சுப ஹோரை API" else "Subha Horai JSON")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header: REST API Playground
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "API",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "ta") "திரு கணித பஞ்சாங்கம் REST API" else "Thiru Ganitha Panchangam REST API",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "ta")
                            "மொபைல் மற்றும் இணைய செயலிகளுக்கு திரு கணித பஞ்சாங்க தரவை JSON வடிவில் வழங்கும் REST API."
                        else
                            "Programmatic REST API providing Thiru Ganitha Panchangam JSON for apps & websites.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Endpoint Selector & Controls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "ta") "API எண்ட் பாயிண்ட் தேர்ந்தெடுக்க" else "Select API Endpoint",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box {
                        OutlinedCard(
                            onClick = { showEndpointMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("endpoint_selector_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AuspiciousGreen
                                    ) {
                                        Text(
                                            text = "GET",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = currentEndpoint,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(text = "▼", fontSize = 12.sp)
                            }
                        }

                        DropdownMenu(
                            expanded = showEndpointMenu,
                            onDismissRequest = { showEndpointMenu = false }
                        ) {
                            endpoints.forEach { (ep, desc) ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(ep, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                            Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        onEndpointChanged(ep)
                                        showEndpointMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Execute Request Button
                    Button(
                        onClick = onRunApi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_api_request_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "ta") "API அழைப்பை இயக்கு (Send Request)" else "Execute API Request",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Response Window
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (apiStatus == 200) AuspiciousGreen else Color.Red
                            ) {
                                Text(
                                    text = "STATUS: $apiStatus OK",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${responseTimeMs} ms",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Panchang API Response", apiResponseText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (language == "ta") "JSON நகலெடுக்கப்பட்டது!" else "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_json_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy JSON",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Code / JSON Display Box
                    SelectionContainer {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp, max = 320.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = apiResponseText,
                                color = Color(0xFF9CDCFFE0),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Developer Integration Code Snippets
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "ta") "செயலி இணைப்பு குறியீடுகள் (Code Snippets)" else "Developer Code Snippets",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedSnippetTab,
                        edgePadding = 0.dp
                    ) {
                        Tab(
                            selected = selectedSnippetTab == 0,
                            onClick = { selectedSnippetTab = 0 },
                            text = { Text("cURL") }
                        )
                        Tab(
                            selected = selectedSnippetTab == 1,
                            onClick = { selectedSnippetTab = 1 },
                            text = { Text("Kotlin (Ktor)") }
                        )
                        Tab(
                            selected = selectedSnippetTab == 2,
                            onClick = { selectedSnippetTab = 2 },
                            text = { Text("Python") }
                        )
                        Tab(
                            selected = selectedSnippetTab == 3,
                            onClick = { selectedSnippetTab = 3 },
                            text = { Text("JavaScript") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val snippetCode = when (selectedSnippetTab) {
                        0 -> """curl -X GET "https://api.thiruganitha.org/v1/panchangam?date=${selectedDate}&city=${selectedCity.id}&lang=${language}""""
                        1 -> """val client = HttpClient()
val response: String = client.get("https://api.thiruganitha.org/v1/panchangam") {
    parameter("date", "$selectedDate")
    parameter("city", "${selectedCity.id}")
}.body()"""
                        2 -> """import requests
response = requests.get('https://api.thiruganitha.org/v1/panchangam', params={'date': '$selectedDate', 'city': '${selectedCity.id}'})
data = response.json()"""
                        else -> """fetch('https://api.thiruganitha.org/v1/panchangam?date=$selectedDate&city=${selectedCity.id}')
  .then(res => res.json())
  .then(data => console.log(data));"""
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2D2D2D))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = snippetCode,
                            color = Color(0xFFCE9178),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
