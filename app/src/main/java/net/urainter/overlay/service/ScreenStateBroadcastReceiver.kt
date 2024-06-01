package net.urainter.overlay.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import net.urainter.overlay.R
import timber.log.Timber


class ScreenStateBroadcastReceiver(private val overlayService: OverlayService) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ScreenStateBroadcastReceiver.onReceive(): ${intent?.action}")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context ?: return)
        val mqttKeepConnection = sharedPreferences.getBoolean(
            context.getString(R.string.key_mqtt_keep_connection_while_screen_is_off),
            false
        )
        val tcpKeepListening = sharedPreferences.getBoolean(
            context.getString(R.string.key_tcp_keep_listening_while_screen_is_off),
            false
        )

        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            if (!mqttKeepConnection) {
                overlayService.mqttCommentSource?.disconnect()
            }
            if (!tcpKeepListening) {
                overlayService.tcpListenerSource?.stop()
            }
        } else if (intent?.action == Intent.ACTION_SCREEN_ON) {
            if (!mqttKeepConnection) {
                overlayService.mqttCommentSource?.connect()
            }
            if (!tcpKeepListening) {
                overlayService.tcpListenerSource?.start(context)
            }
        }
    }
}
