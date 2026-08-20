package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.engine.ThiruGanithaEngine
import com.example.model.CityLocation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object FestivalNotificationHelper {

    const val CHANNEL_ID = "festival_reminders_channel"
    private const val CHANNEL_NAME = "ஸ்ரீ திருக்கணித பஞ்சாங்கம் அறிவிப்புகள்"
    private const val CHANNEL_DESC = "தினசரி பஞ்சாங்கம், சுப முகூர்த்தம் மற்றும் விசேஷ விரத நாட்கள் பற்றிய நினைவூட்டல்கள்"

    private const val PREFS_NAME = "panchang_notification_prefs"
    const val PREF_DAILY_REMINDER = "pref_daily_reminder_enabled"
    const val PREF_FESTIVAL_REMINDER = "pref_festival_reminder_enabled"
    const val PREF_SELECTED_CITY_KEY = "pref_selected_city_key"

    const val ACTION_DAILY_PANCHANG = "com.example.ACTION_DAILY_PANCHANG_REMINDER"
    const val ACTION_FESTIVAL_REMINDER = "com.example.ACTION_FESTIVAL_REMINDER"

    private const val DAILY_NOTIFICATION_ID = 1001

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDailyReminderEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(PREF_DAILY_REMINDER, true)
    }

    fun setDailyReminderEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(PREF_DAILY_REMINDER, enabled).apply()
        if (enabled) {
            scheduleDailyMorningReminder(context)
        } else {
            cancelDailyMorningReminder(context)
        }
    }

    fun isFestivalReminderEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(PREF_FESTIVAL_REMINDER, true)
    }

    fun setFestivalReminderEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(PREF_FESTIVAL_REMINDER, enabled).apply()
        if (enabled) {
            rescheduleAllUpcomingReminders(context)
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showFestivalNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Permission missing for notification: ${e.message}")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing notification: ${e.message}")
        }
    }

    /**
     * Shows today's full daily panchang notification at 6:00 AM
     */
    fun showDailyPanchangNotification(context: Context) {
        try {
            val today = LocalDate.now()
            val city = CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5)
            val p = ThiruGanithaEngine.calculatePanchang(today, city)

            val title = "🕉️ இன்றைய பஞ்சாங்கம் • ${p.tamilMonthTamil} ${p.tamilDay} (${p.vaara.nameTamil})"
            val specialText = if (p.specialEventsTamil.isNotEmpty()) " | விசேஷம்: ${p.specialEventsTamil.joinToString(", ")}" else ""
            val message = "திதி: ${p.tithi.nameTamil} | நட்சத்திரம்: ${p.nakshatra.nameTamil} | நல்ல நேரம்: ${p.nallaNeram.morning.formatted} | ராகு: ${p.rahuKalam.formatted}$specialText"

            showFestivalNotification(
                context = context,
                title = title,
                message = message,
                notificationId = DAILY_NOTIFICATION_ID
            )
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error generating daily panchang notification", e)
        }
    }

    /**
     * Schedules the next 6:00 AM morning panchang alarm
     */
    fun scheduleDailyMorningReminder(context: Context) {
        if (!isDailyReminderEnabled(context)) return

        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FestivalNotificationReceiver::class.java).apply {
            action = ACTION_DAILY_PANCHANG
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = LocalDateTime.now()
        var targetTime = now.toLocalDate().atTime(6, 0)
        if (now.isAfter(targetTime) || now.isEqual(targetTime)) {
            targetTime = targetTime.plusDays(1)
        }

        val triggerMillis = targetTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
            Log.d("NotificationHelper", "Daily 6 AM reminder scheduled for: $targetTime")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Fallback alarm scheduling: ${e.message}")
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun cancelDailyMorningReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FestivalNotificationReceiver::class.java).apply {
            action = ACTION_DAILY_PANCHANG
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleFestivalReminder(
        context: Context,
        title: String,
        message: String,
        triggerAtMillis: Long,
        notificationId: Int
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FestivalNotificationReceiver::class.java).apply {
            action = ACTION_FESTIVAL_REMINDER
            putExtra("FESTIVAL_TITLE", title)
            putExtra("FESTIVAL_MESSAGE", message)
            putExtra("NOTIFICATION_ID", notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelFestivalReminder(context: Context, notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FestivalNotificationReceiver::class.java).apply {
            action = ACTION_FESTIVAL_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Reschedules all upcoming 30-day festival & daily alarms after device reboot
     */
    fun rescheduleAllUpcomingReminders(context: Context) {
        if (isDailyReminderEnabled(context)) {
            scheduleDailyMorningReminder(context)
        }

        if (!isFestivalReminderEnabled(context)) return

        try {
            val today = LocalDate.now()
            val city = CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5)

            for (i in 0..30) {
                val date = today.plusDays(i.toLong())
                val p = ThiruGanithaEngine.calculatePanchang(date, city)

                if (p.isFastingDay || p.isSubhaMuhurtham) {
                    val notifId = date.hashCode()
                    val titleTa = if (p.specialEventsTamil.isNotEmpty()) p.specialEventsTamil.joinToString(", ") else if (p.isSubhaMuhurtham) "சுப முகூர்த்த நாள்" else "சிறப்பு விரத நாள்"
                    val notifTitle = "🔔 திருவிழா நினைவூட்டல்: $titleTa"
                    val notifMsg = "இன்று $titleTa (${p.dateDisplayTamil}). திதி: ${p.tithi.nameTamil}, நட்சத்திரம்: ${p.nakshatra.nameTamil}."

                    val triggerTime = date.atTime(6, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    if (triggerTime > System.currentTimeMillis()) {
                        scheduleFestivalReminder(
                            context = context,
                            title = notifTitle,
                            message = notifMsg,
                            triggerAtMillis = triggerTime,
                            notificationId = notifId
                        )
                    }
                }
            }
            Log.d("NotificationHelper", "All upcoming festival reminders successfully rescheduled")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error rescheduling festival reminders", e)
        }
    }
}

