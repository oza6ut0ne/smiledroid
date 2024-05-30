package net.urainter.overlay.comment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class CommentBroadcastReceiver(private val onCommentCallback: (rawMessage: String) -> Unit) :
    BroadcastReceiver() {
    companion object {
        const val ACTION_NAME = "POST_COMMENT"
        const val EXTRA_NAME = "comment"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "${context?.packageName}.$ACTION_NAME") {
            intent.getStringExtra(EXTRA_NAME)?.let { onCommentCallback(it) }
        }
    }
}
