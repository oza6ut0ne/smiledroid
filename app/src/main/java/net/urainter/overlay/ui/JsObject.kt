package net.urainter.overlay.ui

import android.content.Context
import android.webkit.JavascriptInterface
import androidx.preference.PreferenceManager
import net.urainter.overlay.R

class JsObject(private val context: Context) {
    companion object {
        const val JS_NAME = "jsObject"
        private const val DEFAULT_DURATION = 5000
    }

    @JavascriptInterface
    fun requestDuration(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val default = context.getString(R.string.default_key_basic_duration).toInt()
        return sharedPreferences.getString(
            context.getString(R.string.key_basic_duration),
            context.getString(R.string.default_key_basic_duration)
        )?.toIntOrNull() ?: default
    }

    @JavascriptInterface
    fun requestDefaultDuration() = DEFAULT_DURATION

    @JavascriptInterface
    fun requestTextColorStyle(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val default = context.getString(R.string.default_key_basic_text_color_style)
        return sharedPreferences.getString(
            context.getString(R.string.key_basic_text_color_style),
            default
        ) ?: default
    }

    @JavascriptInterface
    fun requestTextStrokeStyle(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val default = context.getString(R.string.default_key_basic_text_stroke_style)
        return sharedPreferences.getString(
            context.getString(R.string.key_basic_text_stroke_style),
            default
        ) ?: default
    }

    @JavascriptInterface
    fun requestNewlineEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_newline_enabled),
            context.getString(R.string.default_key_basic_newline_enabled).toBooleanStrict()
        )
    }

    @JavascriptInterface
    fun requestIconEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_icon_enabled),
            context.getString(R.string.default_key_basic_icon_enabled).toBooleanStrict()
        )
    }

    @JavascriptInterface
    fun requestInlineImgEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_inline_img_enabled),
            context.getString(R.string.default_key_basic_inline_img_enabled).toBooleanStrict()
        )
    }

    @JavascriptInterface
    fun requestImgEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_img_enabled),
            context.getString(R.string.default_key_basic_img_enabled).toBooleanStrict()
        )
    }

    @JavascriptInterface
    fun requestVideoEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val hardwareAccelerationEnabled = sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_hardware_acceleration_enabled),
            context.getString(R.string.default_key_basic_hardware_acceleration_enabled).toBooleanStrict()
        )
        if (!hardwareAccelerationEnabled) {
            return false
        }

        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_video_enabled),
            context.getString(R.string.default_key_basic_video_enabled).toBooleanStrict()
        )
    }

    @JavascriptInterface
    fun requestRoundIconEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_round_icon_enabled),
            context.getString(R.string.default_key_basic_round_icon_enabled).toBooleanStrict()
        )
    }
}
