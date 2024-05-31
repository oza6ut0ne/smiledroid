package net.urainter.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

object ForegroundNotification {

    @RequiresApi(Build.VERSION_CODES.O)
    fun build(context: Context): Notification {
        val channelId = context.getString(R.string.foreground_notification_channel_id)
        val channelName = context.getString(R.string.foreground_notification_channel_name)
        val title = context.getString(R.string.foreground_notification_title)
        val text = context.getString(R.string.foreground_notification_text)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        )

        val pendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(context, channelId).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }
            setOngoing(true)
            setAutoCancel(false)
            setContentIntent(pendingIntent)
            setContentTitle(title)
            setContentText(text)
            setSmallIcon(android.R.drawable.ic_notification_overlay)
            setTicker(context.getText(R.string.app_name))
            setWhen(System.currentTimeMillis())
        }.build()
    }
}
