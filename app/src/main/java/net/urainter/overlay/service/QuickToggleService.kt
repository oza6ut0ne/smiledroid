package net.urainter.overlay.service

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import net.urainter.overlay.ui.MainActivity

class QuickToggleService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = when (OverlayService.isActive.value == true) {
            true -> Tile.STATE_ACTIVE
            false -> Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntentActivityWrapper =
                PendingIntentActivityWrapper(this, 0, intent, 0, false)
            TileServiceCompat.startActivityAndCollapse(this, pendingIntentActivityWrapper)
            return
        }

        if (OverlayService.isActive.value == true) {
            OverlayService.stop(this)
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            OverlayService.start(this)
            qsTile.state = Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }
}
