package net.urainter.overlay.comment

import kotlinx.serialization.Serializable

@Serializable
data class CommentSchema(
    val text: String? = null,
    val icon: String? = null,
    val images: List<String>? = null,
    val videos: List<String>? = null,
    val inlineImages: List<String>? = null,
    val color: String? = null,
    val textStroke: String? = null,
) {
    companion object {
        const val ICON_SEPARATOR = "##ICON##"
        const val COLOR_SEPARATOR = "##COLOR##"
        const val TEXT_STROKE_SEPARATOR = "##TEXT_STROKE##"
        const val INLINE_IMG_SEPARATOR = "##INLINE_IMG##"
        const val IMG_SEPARATOR = "##IMG##"
        const val VIDEO_SEPARATOR = "##VIDEO##"
        const val JSON_INLINE_IMG_SEPARATOR = "##INLINE##"
    }

    fun toCommentString() = buildString {
        icon?.let {
            append(it)
            append(ICON_SEPARATOR)
        }
        color?.let {
            append(it)
            append(COLOR_SEPARATOR)
        }
        textStroke?.let {
            append(it)
            append(TEXT_STROKE_SEPARATOR)
        }

        var commentText = text ?: ""
        inlineImages?.forEach {
            commentText = commentText.replaceFirst(
                JSON_INLINE_IMG_SEPARATOR,
                "$INLINE_IMG_SEPARATOR$it$INLINE_IMG_SEPARATOR"
            )
        }
        append(commentText)

        images?.forEach {
            append(IMG_SEPARATOR)
            append(it)
        }
        videos?.forEach {
            append(VIDEO_SEPARATOR)
            append(it)
        }
    }
}
