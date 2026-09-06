package com.example.focusfrog.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.focusfrog.MainActivity
import com.example.focusfrog.R

object NotificationHelper {

    const val SESSION_COMPLETE_CHANNEL_ID = "session_complete_channel"
    const val DAILY_REMINDER_CHANNEL_ID = "daily_reminder_channel"

    private const val SESSION_COMPLETE_NOTIFICATION_ID = 1001
    private const val DAILY_REMINDER_NOTIFICATION_ID = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sessionChannel = NotificationChannel(
                SESSION_COMPLETE_CHANNEL_ID,
                context.getString(R.string.session_complete_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.session_complete_channel_name)
            }

            val reminderChannel = NotificationChannel(
                DAILY_REMINDER_CHANNEL_ID,
                context.getString(R.string.daily_reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.daily_reminder_channel_name)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(sessionChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    fun isAppInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    fun showSessionCompleteNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, SESSION_COMPLETE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.session_complete_title))
            .setContentText(context.getString(R.string.session_complete_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(SESSION_COMPLETE_NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted or notification posting blocked
        }
    }

    fun showDailyReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, DAILY_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.daily_reminder_title))
            .setContentText(context.getString(R.string.daily_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(DAILY_REMINDER_NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted or notification posting blocked
        }
    }
}
