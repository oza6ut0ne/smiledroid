package net.urainter.overlay.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.urainter.overlay.R
import net.urainter.overlay.ui.MainActivity

object ForegroundNotification {
    const val NOTIFICATION_ID = 1
    private const val REQUEST_CODE_MAIN_ACTIVITY = 0
    private const val REQUEST_CODE_TOGGLE_PAUSE = 1
    private const val REQUEST_CODE_SHOW = 2
    private const val REQUEST_CODE_HIDE = 3
    private var isPause = false
    private var isShown = true

    fun build(context: Context): Notification {
        val channelId = context.getString(R.string.foreground_notification_channel_id)
        val channelName = context.getString(R.string.foreground_notification_channel_name)
        val title = context.getString(R.string.foreground_notification_title)
        val text = context.getString(R.string.foreground_notification_text)

        NotificationManagerCompat.from(context).createNotificationChannel(
            NotificationChannelCompat.Builder(
                channelId,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            ).setName(channelName).build()
        )

        val mainActivityPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_MAIN_ACTIVITY,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseActionTitle = if (isPause) {
            context.getString(R.string.foreground_notification_action_unpause)
        } else {
            context.getString(R.string.foreground_notification_action_pause)
        }
        val togglePauseAction = createBroadcastAction(
            context,
            ForegroundNotificationBroadcastReceiver.ACTION_TOGGLE_PAUSE,
            REQUEST_CODE_TOGGLE_PAUSE,
            pauseActionTitle
        )
        val showHideAction = if (isShown) {
            createBroadcastAction(
                context,
                ForegroundNotificationBroadcastReceiver.ACTION_HIDE,
                REQUEST_CODE_HIDE,
                context.getString(R.string.foreground_notification_action_hide)
            )
        } else {
            createBroadcastAction(
                context,
                ForegroundNotificationBroadcastReceiver.ACTION_SHOW,
                REQUEST_CODE_SHOW,
                context.getString(R.string.foreground_notification_action_show)
            )
        }

        return NotificationCompat.Builder(context, channelId).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }
            setOngoing(true)
            setOnlyAlertOnce(true)
            setAutoCancel(false)
            setContentIntent(mainActivityPendingIntent)
            setContentTitle(title)
            setContentText(text)
            setSmallIcon(android.R.drawable.ic_notification_overlay)
            setTicker(context.getText(R.string.app_name))
            setWhen(System.currentTimeMillis())
            addAction(togglePauseAction)
            addAction(showHideAction)
        }.build()
    }

    private fun createBroadcastAction(
        context: Context,
        name: String,
        requestCode: Int,
        title: String
    ): NotificationCompat.Action {
        val intent = Intent().apply {
            action =
                "${context.packageName}.$name"
        }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        return NotificationCompat.Action.Builder(null, title, pendingIntent).build()
    }

    fun togglePause(context: Context): Notification {
        isPause = !isPause
        return build(context)
    }

    fun toggleShown(context: Context): Notification {
        isShown = !isShown
        return build(context)
    }
}
