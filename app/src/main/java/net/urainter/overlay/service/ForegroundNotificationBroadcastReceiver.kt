package net.urainter.overlay.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber


class ForegroundNotificationBroadcastReceiver(private val overlayService: OverlayService) :
    BroadcastReceiver() {

    companion object {
        const val ACTION_TOGGLE_PAUSE = "TOGGLE_PAUSE"
        const val ACTION_SHOW = "SHOW"
        const val ACTION_HIDE = "HIDE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ForegroundNotificationBroadcastReceiver.onReceive(): ${intent?.action}")
        when (intent?.action) {
            "${context?.packageName}.${ACTION_TOGGLE_PAUSE}" -> overlayService.togglePause()
            "${context?.packageName}.${ACTION_SHOW}" -> overlayService.show()
            "${context?.packageName}.${ACTION_HIDE}" -> overlayService.hide()
        }
    }
}
