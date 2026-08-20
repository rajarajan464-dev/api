package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class FestivalNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d("NotificationReceiver", "Received intent action: $action")

        when (action) {
            FestivalNotificationHelper.ACTION_DAILY_PANCHANG -> {
                FestivalNotificationHelper.showDailyPanchangNotification(context)
                // Schedule for tomorrow 6 AM
                FestivalNotificationHelper.scheduleDailyMorningReminder(context)
            }
            FestivalNotificationHelper.ACTION_FESTIVAL_REMINDER -> {
                val festivalTitle = intent.getStringExtra("FESTIVAL_TITLE") ?: "🔔 விசேஷ விரத நாள்"
                val festivalMessage = intent.getStringExtra("FESTIVAL_MESSAGE") ?: "இன்றைய சுப முகூர்த்தம் மற்றும் பஞ்சாங்க விபரங்களை அறிய பார்க்கவும்."
                val notificationId = intent.getIntExtra("NOTIFICATION_ID", System.currentTimeMillis().toInt())

                FestivalNotificationHelper.showFestivalNotification(
                    context = context,
                    title = festivalTitle,
                    message = festivalMessage,
                    notificationId = notificationId
                )
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d("NotificationReceiver", "Device rebooted/app updated, rescheduling all reminders")
                FestivalNotificationHelper.rescheduleAllUpcomingReminders(context)
            }
        }
    }
}

