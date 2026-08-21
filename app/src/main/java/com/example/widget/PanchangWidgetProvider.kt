package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import com.example.model.HoraiPeriod
import com.example.model.PanchangResult
import com.example.model.TimeRange
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * ஹோம் ஸ்கிரீன் விட்ஜெட் (HomeScreen AppWidget).
 * வால்பேப்பர் தெரியும்படியான அழகான ஒளிபுகும் (Transparent) வடிவம்.
 * ஆப் திறக்காத போதும் நேரலை (Live) தகவல்கள் மற்றும் ஓரை மாறும் நேரம்,
 * திதி/நட்சத்திர முடிவு, ராகு/எமகண்டம் போன்றவற்றுக்கு தானாகவே (Auto-Update)
 * புதுப்பித்துக் கொள்ளும் வசதி.
 */
class PanchangWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleNextWidgetUpdate(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
        scheduleNextWidgetUpdate(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateAllWidgets(context)
        scheduleNextWidgetUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelScheduledUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAllWidgets(context)
        scheduleNextWidgetUpdate(context)
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.widget.ACTION_UPDATE_PANCHANG_WIDGET"
        const val ACTION_SCHEDULED_TICK = "com.example.widget.ACTION_SCHEDULED_TICK"
        private const val WIDGET_ALARM_REQUEST_CODE = 9988

        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
                val componentName = ComponentName(context, PanchangWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                }
            } catch (e: Exception) {
                Log.e("PanchangWidget", "Error in updateAllWidgets", e)
            }
        }

        /**
         * 00:01 நள்ளிரவு நாள் மாற்றம், ஒவ்வொரு மணி நேர ஓரை தொடக்கம் மற்றும் முடிவு,
         * ராகு/எமகண்டம்/குளிகை/நல்ல நேரம் தொடங்கும் அல்லது முடியும் நேரம்,
         * திதி/நட்சத்திர முடிவு நேரம் போன்றவற்றை மிகத் துல்லியமாகக் கணக்கிட்டு,
         * அடுத்த மிகச்சரியான நேர மாற்றத்திற்கு (Specific Time Alarm) தானாக அலாரம் அமைக்கிறது.
         * இதனால் ஆப் திறக்காமலே விட்ஜெட் குறித்த நேரத்தில் தானாகவே புதுப்பிக்கப்படும்.
         */
        fun scheduleNextWidgetUpdate(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val now = LocalDateTime.now()
                val zoneId = ZoneId.systemDefault()
                val currentMillis = now.atZone(zoneId).toInstant().toEpochMilli()

                val candidateTriggerTimes = mutableListOf<LocalDateTime>()

                // 1. நள்ளிரவு 00:01:05 நாள் மாற்ற நேரம் (Midnight 00:01 Day Rollover)
                val today = LocalDate.now()
                val tomorrow = today.plusDays(1)

                // Today 00:01:05 (if still in the future)
                candidateTriggerTimes.add(today.atTime(0, 1, 5))
                // Tomorrow 00:01:05 (guaranteed future midnight checkpoint)
                candidateTriggerTimes.add(tomorrow.atTime(0, 1, 5))
                // Tomorrow 00:00:05
                candidateTriggerTimes.add(tomorrow.atTime(0, 0, 5))

                // 2. அடுத்த மணி நேரத்தின் தொடக்கம் (Top of next hour: e.g., 01:00:05, 02:00:05...)
                val nextHour = now.truncatedTo(ChronoUnit.HOURS).plusHours(1).plusSeconds(5)
                candidateTriggerTimes.add(nextHour)

                // 3. இன்றைய மற்றும் நாளைய பஞ்சாங்க நேர மாற்றங்களை கணக்கிடுதல்
                val defaultCity = CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5)

                fun addTransitionsForDate(date: LocalDate) {
                    try {
                        val panchang = ThiruGanithaEngine.calculatePanchang(date, defaultCity)

                        fun addRange(range: TimeRange) {
                            parseTimeToMinutes(range.startTime)?.let { min ->
                                candidateTriggerTimes.add(date.atTime(min / 60, min % 60, 5))
                            }
                            parseTimeToMinutes(range.endTime)?.let { min ->
                                candidateTriggerTimes.add(date.atTime(min / 60, min % 60, 5))
                            }
                        }

                        addRange(panchang.rahuKalam)
                        addRange(panchang.yamagandam)
                        addRange(panchang.kuligai)
                        addRange(panchang.nallaNeram.morning)
                        addRange(panchang.nallaNeram.evening)
                        addRange(panchang.gowriNallaNeram.morning)
                        addRange(panchang.gowriNallaNeram.evening)

                        // Add all 24 Horai transition points for the day
                        val all24Horai = panchang.horaiDayList + panchang.horaiNightList
                        for (h in all24Horai) {
                            val startMin = h.startMinutes
                            val endMin = h.endMinutes

                            val startTime = if (startMin < 1440) {
                                date.atTime(startMin / 60, startMin % 60, 5)
                            } else {
                                date.plusDays(1).atTime((startMin - 1440) / 60, (startMin - 1440) % 60, 5)
                            }
                            candidateTriggerTimes.add(startTime)

                            val endTime = if (endMin < 1440) {
                                date.atTime(endMin / 60, endMin % 60, 5)
                            } else {
                                date.plusDays(1).atTime((endMin - 1440) / 60, (endMin - 1440) % 60, 5)
                            }
                            candidateTriggerTimes.add(endTime)
                        }

                        // Tithi & Nakshatra end times
                        if (panchang.tithi.endTimeMinutes in 1..1439) {
                            candidateTriggerTimes.add(date.atTime(panchang.tithi.endTimeMinutes / 60, panchang.tithi.endTimeMinutes % 60, 5))
                        }
                        if (panchang.nakshatra.endTimeMinutes in 1..1439) {
                            candidateTriggerTimes.add(date.atTime(panchang.nakshatra.endTimeMinutes / 60, panchang.nakshatra.endTimeMinutes % 60, 5))
                        }
                    } catch (e: Exception) {
                        Log.e("PanchangWidget", "Error adding transitions for $date", e)
                    }
                }

                // Add transitions for today and tomorrow
                addTransitionsForDate(today)
                addTransitionsForDate(tomorrow)

                // Filter only future candidates at least 3 seconds from now
                val futureCandidates = candidateTriggerTimes
                    .map { it.atZone(zoneId).toInstant().toEpochMilli() }
                    .filter { it > currentMillis + 3000L }
                    .sorted()

                var nextTriggerMillis = futureCandidates.firstOrNull() ?: (currentMillis + 15 * 60 * 1000L)

                // அதிகபட்ச இடைவெளி 15 நிமிடத்திற்கு மிகாமல் இருத்தல் (Max fallback 15 mins)
                val maxIntervalMillis = currentMillis + (15 * 60 * 1000L)
                if (nextTriggerMillis > maxIntervalMillis) {
                    nextTriggerMillis = maxIntervalMillis
                }

                val intent = Intent(context, PanchangWidgetProvider::class.java).apply {
                    action = ACTION_SCHEDULED_TICK
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    WIDGET_ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                    }
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
                Log.d("PanchangWidget", "Next widget alarm scheduled for: ${java.util.Date(nextTriggerMillis)}")
            } catch (e: Exception) {
                Log.e("PanchangWidget", "Error scheduling widget update alarm", e)
            }
        }

        private fun cancelScheduledUpdates(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, PanchangWidgetProvider::class.java).apply {
                    action = ACTION_SCHEDULED_TICK
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    WIDGET_ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }
            } catch (e: Exception) {
                Log.e("PanchangWidget", "Error cancelling alarm", e)
            }
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.panchang_widget_layout)

            try {
                val today = LocalDate.now()
                val nowTime = LocalTime.now()
                val currentMinutes = nowTime.hour * 60 + nowTime.minute

                // Default city: Chennai
                val defaultCity = CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5)
                val panchang: PanchangResult = ThiruGanithaEngine.calculatePanchang(today, defaultCity)

                // 1. தமிழ் தேதி & கிழமை
                val tamilDateText = "${panchang.tamilMonthTamil} ${panchang.tamilDay} • ${panchang.vaara.nameTamil}"
                views.setTextViewText(R.id.tv_widget_tamil_date, tamilDateText)

                // 2. நட்சத்திரம் (பாதத்துடன் - நடப்பு நட்சத்திரம்)
                val nakshatraText = if (panchang.nakshatra.isFinished) {
                    "${panchang.nakshatra.currentLiveNameTamil} (பாதம் ${panchang.nakshatra.currentLivePada})"
                } else {
                    "${panchang.nakshatra.nameTamil} (பாதம் ${panchang.nakshatra.pada})"
                }
                views.setTextViewText(R.id.tv_widget_nakshatra, nakshatraText)

                // 3. நடப்பு திதி
                val tithiText = if (panchang.tithi.isFinished) {
                    "${panchang.tithi.currentLivePakshaTamil} ${panchang.tithi.currentLiveNameTamil}"
                } else {
                    "${panchang.tithi.pakshaTamil} ${panchang.tithi.nameTamil}"
                }
                views.setTextViewText(R.id.tv_widget_tithi, tithiText)

                // 4. நடப்பு ஓரை (Current 24-Hour Hora derived from Sunrise)
                val all24Horai = panchang.horaiDayList + panchang.horaiNightList
                val currentHora = getCurrentHora(all24Horai, currentMinutes)
                val horaDisplay = if (currentHora != null) {
                    "${currentHora.planetTamil} ஓரை"
                } else {
                    "சுப ஓரை"
                }
                views.setTextViewText(R.id.tv_widget_current_hora, horaDisplay)
                if (currentHora != null && currentHora.isGood) {
                    views.setTextColor(R.id.tv_widget_current_hora, Color.parseColor("#A5D6A7")) // Soft Subam Green
                } else {
                    views.setTextColor(R.id.tv_widget_current_hora, Color.parseColor("#FFAB91")) // Soft Asubam Coral
                }

                // 5. நடப்பு நேரத்தில் நடக்கும் காலம் கண்டறிதல் (Active Period Detector)
                val activeStatus = getCurrentlyActiveTiming(panchang, currentMinutes)

                if (activeStatus != null) {
                    // தற்சமயம் ராகு / எமகண்டம் / குளிகை / நல்ல நேரம் நடக்கிறது -> விசிபுல் செய்
                    views.setViewVisibility(R.id.layout_widget_active_banner, View.VISIBLE)
                    views.setTextViewText(R.id.tv_widget_active_title, activeStatus.title)
                    views.setTextViewText(R.id.tv_widget_active_time, activeStatus.timeRange)
                    if (activeStatus.isInauspicious) {
                        views.setTextColor(R.id.tv_widget_active_title, Color.parseColor("#FF8A80"))
                        views.setTextColor(R.id.tv_widget_active_time, Color.parseColor("#FFCDD2"))
                    } else {
                        views.setTextColor(R.id.tv_widget_active_title, Color.parseColor("#A5D6A7"))
                        views.setTextColor(R.id.tv_widget_active_time, Color.parseColor("#C8E6C9"))
                    }
                    views.setViewVisibility(R.id.tv_widget_upcoming_note, View.GONE)
                } else {
                    // தற்சமயம் எதுவும் நடக்கவில்லை -> பேனரை மறை (Gone)
                    views.setViewVisibility(R.id.layout_widget_active_banner, View.GONE)

                    // அடுத்த வரவிருக்கும் சுப/அசுப நேரக் குறிப்பு
                    val upcoming = getUpcomingTimingNote(panchang, currentMinutes)
                    views.setTextViewText(R.id.tv_widget_upcoming_note, upcoming)
                    views.setViewVisibility(R.id.tv_widget_upcoming_note, View.VISIBLE)
                }

                // விட்ஜெட்டைத் தொடும்போது ஆப்பைத் திறக்கும் செயல்பாடு
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            } catch (e: Exception) {
                Log.e("PanchangWidget", "Error computing panchang for widget", e)
                views.setTextViewText(R.id.tv_widget_tamil_date, "ஸ்ரீ திருக்கணித பஞ்சாங்கம்")
                views.setTextViewText(R.id.tv_widget_nakshatra, "விபரங்களை அறிய தட்டவும்")
                views.setTextViewText(R.id.tv_widget_tithi, "சுப திதி")
                views.setTextViewText(R.id.tv_widget_current_hora, "சுப ஓரை")
            }

            try {
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e("PanchangWidget", "Error updating RemoteViews", e)
            }
        }

        private fun getCurrentHora(dayList: List<HoraiPeriod>, currentMinutes: Int): HoraiPeriod? {
            if (dayList.isEmpty()) return null
            val baseSunrise = dayList.firstOrNull()?.startMinutes ?: 360
            val normCurrent = if (currentMinutes < baseSunrise) currentMinutes + 1440 else currentMinutes
            return dayList.firstOrNull { horai ->
                normCurrent >= horai.startMinutes && normCurrent < horai.endMinutes
            } ?: dayList.firstOrNull()
        }

        data class ActiveTiming(
            val title: String,
            val timeRange: String,
            val isInauspicious: Boolean
        )

        private fun parseTimeToMinutes(timeStr: String): Int? {
            return try {
                val clean = timeStr.trim().uppercase(Locale.ENGLISH)
                val isPm = clean.contains("PM")
                val isAm = clean.contains("AM")
                val timeOnly = clean.replace("AM", "").replace("PM", "").trim()
                val parts = timeOnly.split(":")
                var hour = parts[0].toInt()
                val min = parts[1].toInt()
                if (isPm && hour < 12) hour += 12
                if (isAm && hour == 12) hour = 0
                hour * 60 + min
            } catch (_: Exception) {
                null
            }
        }

        private fun isTimeInsideRange(currentMinutes: Int, range: TimeRange): Boolean {
            val startMin = parseTimeToMinutes(range.startTime) ?: return false
            val endMin = parseTimeToMinutes(range.endTime) ?: return false
            return if (startMin <= endMin) {
                currentMinutes in startMin..endMin
            } else {
                // Crossing midnight
                currentMinutes >= startMin || currentMinutes <= endMin
            }
        }

        private fun getCurrentlyActiveTiming(panchang: PanchangResult, currentMinutes: Int): ActiveTiming? {
            // 1. Check Rahu Kalam
            if (isTimeInsideRange(currentMinutes, panchang.rahuKalam)) {
                return ActiveTiming(
                    title = "⚠️ ராகு காலம் நடக்கிறது",
                    timeRange = panchang.rahuKalam.formatted,
                    isInauspicious = true
                )
            }

            // 2. Check Yamagandam
            if (isTimeInsideRange(currentMinutes, panchang.yamagandam)) {
                return ActiveTiming(
                    title = "⚠️ எமகண்டம் நடக்கிறது",
                    timeRange = panchang.yamagandam.formatted,
                    isInauspicious = true
                )
            }

            // 3. Check Kuligai
            if (isTimeInsideRange(currentMinutes, panchang.kuligai)) {
                return ActiveTiming(
                    title = "⏳ குளிகை காலம் நடக்கிறது",
                    timeRange = panchang.kuligai.formatted,
                    isInauspicious = true
                )
            }

            // 4. Check Morning Nalla Neram
            if (isTimeInsideRange(currentMinutes, panchang.nallaNeram.morning)) {
                return ActiveTiming(
                    title = "✨ நல்ல நேரம் நடக்கிறது",
                    timeRange = panchang.nallaNeram.morning.formatted,
                    isInauspicious = false
                )
            }

            // 5. Check Evening Nalla Neram
            if (isTimeInsideRange(currentMinutes, panchang.nallaNeram.evening)) {
                return ActiveTiming(
                    title = "✨ நல்ல நேரம் நடக்கிறது",
                    timeRange = panchang.nallaNeram.evening.formatted,
                    isInauspicious = false
                )
            }

            // 6. Check Gowri Nalla Neram (Morning & Evening)
            if (isTimeInsideRange(currentMinutes, panchang.gowriNallaNeram.morning)) {
                return ActiveTiming(
                    title = "🌟 கௌரி நல்ல நேரம் நடக்கிறது",
                    timeRange = panchang.gowriNallaNeram.morning.formatted,
                    isInauspicious = false
                )
            }

            if (isTimeInsideRange(currentMinutes, panchang.gowriNallaNeram.evening)) {
                return ActiveTiming(
                    title = "🌟 கௌரி நல்ல நேரம் நடக்கிறது",
                    timeRange = panchang.gowriNallaNeram.evening.formatted,
                    isInauspicious = false
                )
            }

            return null
        }

        private fun getUpcomingTimingNote(panchang: PanchangResult, currentMinutes: Int): String {
            val morningStart = parseTimeToMinutes(panchang.nallaNeram.morning.startTime) ?: 0
            val eveningStart = parseTimeToMinutes(panchang.nallaNeram.evening.startTime) ?: 0
            val rahuStart = parseTimeToMinutes(panchang.rahuKalam.startTime) ?: 0

            return when {
                currentMinutes < morningStart -> "அடுத்த நல்ல நேரம்: ${panchang.nallaNeram.morning.formatted}"
                currentMinutes < rahuStart -> "அடுத்த ராகு காலம்: ${panchang.rahuKalam.formatted}"
                currentMinutes < eveningStart -> "அடுத்த நல்ல நேரம்: ${panchang.nallaNeram.evening.formatted}"
                else -> "இன்றைய சிறப்பு: ${panchang.specialEventsTamil.firstOrNull() ?: "ஸ்ரீ திருக்கணித பஞ்சாங்கம்"}"
            }
        }
    }
}

