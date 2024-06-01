package net.urainter.overlay.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.preference.PreferenceManager
import net.urainter.overlay.R
import net.urainter.overlay.comment.CommentBroadcastReceiver
import net.urainter.overlay.comment.source.MqttCommentSource
import net.urainter.overlay.comment.source.TcpListenerSource
import net.urainter.overlay.ui.OverlayView

class OverlayService : Service() {
    companion object {
        private const val ACTION_START = "START"
        private const val ACTION_STOP = "STOP"
        var isActive = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private lateinit var overlayView: OverlayView
    private var commentBroadcastReceiver: CommentBroadcastReceiver? = null
    private var screenBroadcastReceiver: ScreenStateBroadcastReceiver? = null
    var mqttCommentSource: MqttCommentSource? = null
    var tcpListenerSource: TcpListenerSource? = null

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = ForegroundNotification.build(this)
            startForeground(1, notification)
        }
        overlayView = OverlayView.create(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    isActive = true
                    overlayView.show(this)
                    startCommentSources()
                }

                ACTION_STOP -> {
                    isActive = false
                    overlayView.hide()
                    shutdownCommentSources()
                    stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (isActive) {
            isActive = false
            overlayView.hide()
            shutdownCommentSources()
        }
    }

    override fun onBind(intent: Intent?) = null

    private fun startCommentSources() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean(getString(R.string.key_mqtt_enabled), false)) {
            mqttCommentSource =
                MqttCommentSource(this) { overlayView.showComment(it) }.apply {
                    connect()
                }
        }

        if (sharedPreferences.getBoolean(getString(R.string.key_tcp_enabled), false)) {
            tcpListenerSource =
                TcpListenerSource { overlayView.showComment(it) }.apply {
                    start(this@OverlayService)
                }
        }

        commentBroadcastReceiver =
            CommentBroadcastReceiver { overlayView.showComment(it) }.also { receiver ->
                val filter = IntentFilter().apply {
                    addAction("${this@OverlayService.packageName}.${CommentBroadcastReceiver.ACTION_NAME}")
                }
                registerReceiver(receiver, filter)
            }

        screenBroadcastReceiver =
            ScreenStateBroadcastReceiver(this).also { receiver ->
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_SCREEN_ON)
                }
                registerReceiver(receiver, filter)
            }
    }

    private fun shutdownCommentSources() {
        commentBroadcastReceiver?.let { unregisterReceiver(it) }
        screenBroadcastReceiver?.let { unregisterReceiver(it) }
        mqttCommentSource?.disconnect()
        mqttCommentSource = null
        tcpListenerSource?.stop()
        tcpListenerSource = null
    }
}
