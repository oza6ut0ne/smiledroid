package net.urainter.overlay

import android.webkit.JavascriptInterface

object JsObject {
    const val JS_NAME = "jsObject"

    @JavascriptInterface
    fun requestDuration() = 5000

    @JavascriptInterface
    fun requestDefaultDuration() = 5000

    @JavascriptInterface
    fun requestTextColorStyle() = "rgba(255, 255, 255, 1.0)"

    @JavascriptInterface
    fun requestTextStrokeStyle() = "2px rgba(0, 0, 0, 1.0)"

    @JavascriptInterface
    fun requestNewlineEnabled() = true

    @JavascriptInterface
    fun requestIconEnabled() = true

    @JavascriptInterface
    fun requestInlineImgEnabled() = true

    @JavascriptInterface
    fun requestImgEnabled() = true

    @JavascriptInterface
    fun requestVideoEnabled() = true

    @JavascriptInterface
    fun requestRoundIconEnabled() = false
}
