package com.lifecore.app.automation

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val title = inputData.getString(KEY_TITLE)?.takeIf { it.isNotBlank() } ?: "LIFECORE reminder"
        val message = inputData.getString(KEY_MESSAGE)?.takeIf { it.isNotBlank() }
            ?: "It's time for your reminder."

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "LIFECORE reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(applicationContext)
        }
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        manager.notify(id.toInt(), notification)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "reminder_title"
        const val KEY_MESSAGE = "reminder_message"
        const val UNIQUE_WORK_NAME = "lifecore_next_reminder"
        private const val CHANNEL_ID = "lifecore_reminders"
    }
}
