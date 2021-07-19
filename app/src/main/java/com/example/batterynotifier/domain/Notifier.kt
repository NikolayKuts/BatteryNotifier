package com.example.batterynotifier.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.batterynotifier.R

const val DEFAULT_CHANNEL_ID = "default_channel_id"
const val DEFAULT_NOTIFICATION_ID = 1234567890
const val CHANNEL_NAME = "Battery notifier channel"
const val CHANNEL_DESCRIPTION = "Notifying about battery state"
const val WARNING = "Warning"

class Notifier(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var channelId = DEFAULT_CHANNEL_ID
    private var pendingIntent: PendingIntent? = null

    companion object {
        const val DEFAULT_WATCHER_NOTIFICATION_TEXT = "Your target has dangerous battery level"
        const val DEFAULT_TARGET_NOTIFICATION_TEXT = "You have dangerous battery level"
        const val FRAGMENT_TAG = "fragment_tag"
    }

    fun setChannel(
        id: String = DEFAULT_CHANNEL_ID,
        name: String = CHANNEL_NAME,
        description: String = CHANNEL_DESCRIPTION,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = id
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun <T> setPendingIntent(cls: Class<T>, fragmentTag: String? = null) {
        val intent = Intent(context, cls)
        intent.putExtra(FRAGMENT_TAG, fragmentTag)
        pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun pushNotification(message: String, idNotification: Int = DEFAULT_NOTIFICATION_ID) {
        val notification = createNotification(message)
        NotificationManagerCompat.from(context).notify(idNotification, notification)
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_battery_notification)
            .setContentTitle(WARNING)
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.resources, R.drawable.battery_large_icon)
            )
            .build()
    }
}