package net.urainter.overlay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.input.InputManager
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.preference.PreferenceManager
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import net.urainter.overlay.comment.CommentSchema
import kotlinx.serialization.json.Json
import net.urainter.overlay.R
import timber.log.Timber
import java.io.File
import kotlin.random.Random


class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    companion object {
        private const val HTML_URL = "https://appassets.androidplatform.net/assets/dist/html/index.html"
        private const val WEB_VIEW_CACHE_DIR_NAME = "web_cache"
        private const val ASSETS_PATH = "/assets/"
        private const val RESOURCES_PATH = "/res/"
        private const val INTERNAL_STORAGE_PATH = "/cache/"

        fun create(context: Context): OverlayView {
            return View.inflate(context, R.layout.overlay_view, null) as OverlayView
        }
    }

    private lateinit var webView: WebView
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams = WindowManager.LayoutParams(
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        },
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            layoutParams.alpha =
                (context.getSystemService(Context.INPUT_SERVICE) as InputManager).maximumObscuringOpacityForTouch
        }

        val webViewCacheDir = File(context.cacheDir, WEB_VIEW_CACHE_DIR_NAME).apply { mkdirs() }
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler(ASSETS_PATH, WebViewAssetLoader.AssetsPathHandler(context))
            .addPathHandler(RESOURCES_PATH, WebViewAssetLoader.ResourcesPathHandler(context))
            .addPathHandler(
                INTERNAL_STORAGE_PATH,
                WebViewAssetLoader.InternalStoragePathHandler(context, webViewCacheDir)
            )
            .build()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val hardwareAccelerationEnabled = sharedPreferences.getBoolean(
            context.getString(R.string.key_basic_hardware_acceleration_enabled),
            context.getString(R.string.default_key_basic_hardware_acceleration_enabled).toBooleanStrict()
        )
        val layerType = when (hardwareAccelerationEnabled) {
            true -> View.LAYER_TYPE_HARDWARE
            false -> View.LAYER_TYPE_SOFTWARE
        }

        @SuppressLint("SetJavaScriptEnabled")
        webView = findViewById<WebView>(R.id.web_view).apply {
            setLayerType(layerType, null)
            setBackgroundColor(Color.TRANSPARENT)
            settings.javaScriptEnabled = true
            addJavascriptInterface(JsObject(context), JsObject.JS_NAME)
            webViewClient = object : WebViewClientCompat() {
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }
            }
            loadUrl(HTML_URL)
        }
    }

    fun showComment(rawMessage: String) {
        val text = try {
            val format = Json { ignoreUnknownKeys = true }
            val comment = format.decodeFromString<CommentSchema>(rawMessage)
            Timber.d(comment.toString())
            comment.toCommentString()
        } catch (e: Exception) {
            rawMessage
        }
        val encoded = Base64.encodeToString(text.toByteArray(), Base64.NO_WRAP)
        val offsetTopRatio = Random.nextFloat()
        val script =
            """main.handleComment({id: 0, text: "$encoded", offsetTopRatio: $offsetTopRatio}, {isSingleWindow: true, numDisplays: 1})"""
        Timber.d("script: $script")
        webView.run { post { evaluateJavascript(script, null) } }
    }

    fun togglePause() {
        webView.run { post { evaluateJavascript("main.togglePause()", null) } }
    }

    @Synchronized
    fun show() {
        if (!isShown) {
            windowManager.addView(this, layoutParams)
        }
    }

    @Synchronized
    fun hide() {
        if (isShown) {
            windowManager.removeView(this)
        }
    }
}
