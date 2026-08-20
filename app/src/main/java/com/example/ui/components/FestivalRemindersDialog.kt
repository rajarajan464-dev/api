package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.PanchangResult
import com.example.notifications.FestivalNotificationHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class UpcomingFestivalInfo(
    val date: LocalDate,
    val panchang: PanchangResult,
    val titleTamil: String,
    val titleEnglish: String,
    val iconSymbol: String,
    val isMuhurtham: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalRemindersDialog(
    currentCity: CityLocation,
    language: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Permission launcher for Android 13+
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            FestivalNotificationHelper.createNotificationChannel(context)
        }
    }

    // Precalculate upcoming festival days for next 30 days
    val upcomingFestivals = remember(currentCity) {
        val today = LocalDate.now()
        val list = mutableListOf<UpcomingFestivalInfo>()

        for (i in 0..30) {
            val date = today.plusDays(i.toLong())
            val p = ThiruGanithaEngine.calculatePanchang(date, currentCity)

            val isFestival = p.isFastingDay || p.isSubhaMuhurtham

            if (isFestival) {
                val symbol = when {
                    p.isSubhaMuhurtham -> "💍"
                    p.isChaturthi -> "🐘"
                    p.isSashti -> "🔱"
                    p.isPradosham -> "🔱"
                    p.isEkadashi -> "🐚"
                    p.isKarthigai -> "🪔"
                    p.isPurnima -> "🌕"
                    p.isAmavasya -> "🌑"
                    else -> "🕉️"
                }

                val titleTa = if (p.specialEventsTamil.isNotEmpty()) p.specialEventsTamil.joinToString(", ") else "சிறப்பு விரத நாள்"
                val titleEn = if (p.specialEventsEnglish.isNotEmpty()) p.specialEventsEnglish.joinToString(", ") else "Special Fasting Day"

                list.add(
                    UpcomingFestivalInfo(
                        date = date,
                        panchang = p,
                        titleTamil = titleTa,
                        titleEnglish = titleEn,
                        iconSymbol = symbol,
                        isMuhurtham = p.isSubhaMuhurtham
                    )
                )
            }
        }
        list
    }

    // Set of scheduled date strings ISO - ENABLE ALL SPECIAL DAYS BY DEFAULT
    var scheduledDates by remember(upcomingFestivals) {
        mutableStateOf(upcomingFestivals.map { it.date.toString() }.toSet())
    }
    var dailyReminderEnabled by remember {
        mutableStateOf(FestivalNotificationHelper.isDailyReminderEnabled(context))
    }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Schedule all active reminders automatically when permission is present
    LaunchedEffect(hasPermission, upcomingFestivals) {
        if (hasPermission) {
            FestivalNotificationHelper.createNotificationChannel(context)
            if (dailyReminderEnabled) {
                FestivalNotificationHelper.scheduleDailyMorningReminder(context)
            }
            upcomingFestivals.forEach { item ->
                val notifId = item.date.hashCode()
                val notifTitle = if (language == "ta") "🔔 திருவிழா நினைவூட்டல்: ${item.titleTamil}" else "🔔 Festival Reminder: ${item.titleEnglish}"
                val notifMsg = if (language == "ta")
                    "இன்று ${item.titleTamil} (${item.panchang.dateDisplayTamil}). திதி: ${item.panchang.tithi.nameTamil}, நட்சத்திரம்: ${item.panchang.nakshatra.nameTamil}."
                else
                    "Today is ${item.titleEnglish}. Tithi: ${item.panchang.tithi.nameEnglish}, Nakshatra: ${item.panchang.nakshatra.nameEnglish}."

                val triggerTime = item.date.atTime(6, 0).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

                FestivalNotificationHelper.scheduleFestivalReminder(
                    context = context,
                    title = notifTitle,
                    message = notifMsg,
                    triggerAtMillis = if (triggerTime > System.currentTimeMillis()) triggerTime else System.currentTimeMillis() + 5000,
                    notificationId = notifId
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("festival_reminders_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminders",
                                modifier = Modifier.padding(8.dp).size(22.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == "ta") "திருவிழா நினைவூட்டல்" else "Festival Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == "ta") "அடுத்த 30 நாட்களின் விசேஷ நாட்கள்" else "Upcoming Special Days (Next 30 Days)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Permission Warning Banner if on Android 13+ and not granted
                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == "ta") "அறிவிப்புகளைப் பெற அனுமதி தேவை" else "Notification Permission Required",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(if (language == "ta") "அனுமதி" else "Allow", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Immediate Test Notification Trigger
                OutlinedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "ta") "🧪 உடனடி அறிவிப்பு சோதனை" else "🧪 Trigger Instant Test Notification",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (language == "ta") "உங்கள் போனில் சிஸ்டம் நோட்டிபிகேஷன் சோதிக்க" else "Test device system banner immediately",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                val nextFest = upcomingFestivals.firstOrNull()
                                val title = if (language == "ta")
                                    "🔔 நினைவூட்டல்: ${nextFest?.titleTamil ?: "சதுர்த்தி விரதம்"}"
                                else
                                    "🔔 Reminder: ${nextFest?.titleEnglish ?: "Chaturthi Fasting"}"

                                val msg = if (language == "ta")
                                    "இன்று (${nextFest?.panchang?.dateDisplayTamil ?: "இன்றைய நாள்"}) விசேஷ திருவிழா/விரத நாளாகும். சுப காரியங்களை அறிய பஞ்சாங்கம் பார்க்கவும்."
                                else
                                    "Today is a special festival/fasting day. Open app to view auspicious timings."

                                FestivalNotificationHelper.showFestivalNotification(
                                    context = context,
                                    title = title,
                                    message = msg
                                )
                                snackbarMessage = if (language == "ta") "நோட்டிபிகேஷன் அனுப்பப்பட்டது!" else "Test notification posted!"
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == "ta") "அனுப்பு" else "Test", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Morning Notification Switch
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == "ta") "தினசரி காலை 6:00 மணி பஞ்சாங்க நினைவூட்டல்" else "Daily 6:00 AM Panchang Reminder",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = dailyReminderEnabled,
                            onCheckedChange = { isEnabled ->
                                dailyReminderEnabled = isEnabled
                                FestivalNotificationHelper.setDailyReminderEnabled(context, isEnabled)
                                snackbarMessage = if (isEnabled) {
                                    if (language == "ta") "காலை 6 மணி நினைவூட்டல் இயக்கப்பட்டது" else "Daily 6 AM reminder enabled"
                                } else {
                                    if (language == "ta") "காலை நினைவூட்டல் முடக்கம்" else "Daily reminder disabled"
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == "ta") "அடுத்து வரும் விரத/முகூர்த்த நாட்கள்:" else "Upcoming Fasting & Muhurtham Dates:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Upcoming List
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false).heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(upcomingFestivals) { item ->
                        val isScheduled = scheduledDates.contains(item.date.toString())

                        OutlinedCard(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isScheduled) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = item.iconSymbol,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )

                                    Column {
                                        Text(
                                            text = if (language == "ta") item.titleTamil else item.titleEnglish,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        val formattedDate = item.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                        Text(
                                            text = if (language == "ta")
                                                "$formattedDate (${item.panchang.tamilMonthTamil} ${item.panchang.tamilDay}) - ${item.panchang.vaara.nameTamil}"
                                            else
                                                "$formattedDate (${item.panchang.tamilMonthEnglish} ${item.panchang.tamilDay}) - ${item.panchang.vaara.nameEnglish}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = isScheduled,
                                    onCheckedChange = { checked ->
                                        val notifId = item.date.hashCode()
                                        if (checked) {
                                            scheduledDates = scheduledDates + item.date.toString()

                                            val notifTitle = if (language == "ta") "🔔 திருவிழா நினைவூட்டல்: ${item.titleTamil}" else "🔔 Festival Reminder: ${item.titleEnglish}"
                                            val notifMsg = if (language == "ta")
                                                "இன்று ${item.titleTamil} (${item.panchang.dateDisplayTamil}). திதி: ${item.panchang.tithi.nameTamil}, நட்சத்திரம்: ${item.panchang.nakshatra.nameTamil}."
                                            else
                                                "Today is ${item.titleEnglish}. Tithi: ${item.panchang.tithi.nameEnglish}, Nakshatra: ${item.panchang.nakshatra.nameEnglish}."

                                            // Schedule for morning of festival date at 6:00 AM
                                            val triggerTime = item.date.atTime(6, 0).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

                                            FestivalNotificationHelper.scheduleFestivalReminder(
                                                context = context,
                                                title = notifTitle,
                                                message = notifMsg,
                                                triggerAtMillis = if (triggerTime > System.currentTimeMillis()) triggerTime else System.currentTimeMillis() + 5000,
                                                notificationId = notifId
                                            )

                                            snackbarMessage = if (language == "ta") "${item.titleTamil} நினைவூட்டல் அமைக்கப்பட்டது!" else "Reminder set for ${item.titleEnglish}!"
                                        } else {
                                            scheduledDates = scheduledDates - item.date.toString()
                                            FestivalNotificationHelper.cancelFestivalReminder(context, notifId)
                                            snackbarMessage = if (language == "ta") "நினைவூட்டல் ரத்து செய்யப்பட்டது" else "Reminder canceled"
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Feedback Banner
                snackbarMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    ) {
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
