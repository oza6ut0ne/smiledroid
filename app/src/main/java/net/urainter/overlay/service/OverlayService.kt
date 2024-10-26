package net.urainter.overlay.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
        private val _isActive = MutableLiveData(false)
        val isActive: LiveData<Boolean>
            get() = _isActive

        fun start(context: Context) {
            if (isActive.value == true) {
                return
            }
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_START
            }
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> context.startForegroundService(intent)
                else -> context.startService(intent)
            }
        }

        fun stop(context: Context) {
            if (isActive.value == false) {
                return
            }
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private lateinit var overlayView: OverlayView
    private var foregroundNotificationBroadcastReceiver: ForegroundNotificationBroadcastReceiver? = null
    private var commentBroadcastReceiver: CommentBroadcastReceiver? = null
    private var screenBroadcastReceiver: ScreenStateBroadcastReceiver? = null
    var mqttCommentSource: MqttCommentSource? = null
    var tcpListenerSource: TcpListenerSource? = null

    override fun onCreate() {
        val notification = ForegroundNotification.build(this)
        startForeground(ForegroundNotification.NOTIFICATION_ID, notification)
        overlayView = OverlayView.create(this)
        foregroundNotificationBroadcastReceiver =
            ForegroundNotificationBroadcastReceiver(this).also { receiver ->
                val filter = IntentFilter().apply {
                    addAction("${this@OverlayService.packageName}.${ForegroundNotificationBroadcastReceiver.ACTION_TOGGLE_PAUSE}")
                    addAction("${this@OverlayService.packageName}.${ForegroundNotificationBroadcastReceiver.ACTION_SHOW}")
                    addAction("${this@OverlayService.packageName}.${ForegroundNotificationBroadcastReceiver.ACTION_HIDE}")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(receiver, filter, RECEIVER_EXPORTED)
                } else {
                    registerReceiver(receiver, filter)
                }
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    _isActive.value = true
                    overlayView.show()
                    startCommentSources()
                }

                ACTION_STOP -> {
                    _isActive.value = false
                    overlayView.hide()
                    shutdownCommentSources()
                    stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (isActive.value == true) {
            _isActive.value = false
            overlayView.hide()
            shutdownCommentSources()
        }
        foregroundNotificationBroadcastReceiver?.let { unregisterReceiver(it) }
    }

    override fun onBind(intent: Intent?) = null

    private fun startCommentSources() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean(
                getString(R.string.key_mqtt_enabled),
                getString(R.string.default_key_mqtt_enabled).toBooleanStrict()
            )
        ) {
            mqttCommentSource = MqttCommentSource(this) { overlayView.showComment(it) }.apply {
                connect()
            }
        }

        if (sharedPreferences.getBoolean(
                getString(R.string.key_tcp_enabled),
                getString(R.string.default_key_tcp_enabled).toBooleanStrict()
            )
        ) {
            tcpListenerSource = TcpListenerSource { overlayView.showComment(it) }.apply {
                start(this@OverlayService)
            }
        }

        commentBroadcastReceiver =
            CommentBroadcastReceiver { overlayView.showComment(it) }.also { receiver ->
                val filter = IntentFilter().apply {
                    addAction("${this@OverlayService.packageName}.${CommentBroadcastReceiver.ACTION_NAME}")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(receiver, filter, RECEIVER_EXPORTED)
                } else {
                    registerReceiver(receiver, filter)
                }
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
        commentBroadcastReceiver = null
        screenBroadcastReceiver?.let { unregisterReceiver(it) }
        screenBroadcastReceiver = null
        mqttCommentSource?.disconnect()
        mqttCommentSource = null
        tcpListenerSource?.stop()
        tcpListenerSource = null
    }

    fun togglePause() {
        overlayView.togglePause()
        val notification = ForegroundNotification.togglePause(this)
        startForeground(ForegroundNotification.NOTIFICATION_ID, notification)
    }

    fun show() {
        if (overlayView.isShown) {
            return
        }
        val notification = ForegroundNotification.toggleShown(this)
        startForeground(ForegroundNotification.NOTIFICATION_ID, notification)
        overlayView.show()
    }

    fun hide() {
        if (!overlayView.isShown) {
            return
        }
        val notification = ForegroundNotification.toggleShown(this)
        startForeground(ForegroundNotification.NOTIFICATION_ID, notification)
        overlayView.hide()
    }
}
