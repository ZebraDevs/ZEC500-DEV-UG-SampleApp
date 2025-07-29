package com.zebra.zec500_overlay_standalone_fragmentviewmodel // Use your actual package name

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class LetterboxedVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val videoWidth = mVideoWidth
        val videoHeight = mVideoHeight

        if (videoWidth > 0 && videoHeight > 0) {
            val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
            val viewHeight = MeasureSpec.getSize(heightMeasureSpec)
            val viewAspectRatio = viewWidth.toFloat() / viewHeight.toFloat()
            val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()

            var finalWidth = viewWidth
            var finalHeight = viewHeight

            if (videoAspectRatio > viewAspectRatio) {
                // Video is wider than the view, so scale height based on view's width
                finalHeight = (viewWidth / videoAspectRatio).toInt()
            } else {
                // Video is taller than the view, so scale width based on view's height
                finalWidth = (viewHeight * videoAspectRatio).toInt()
            }

            setMeasuredDimension(finalWidth, finalHeight)
        }
    }

    // Private fields mVideoWidth and mVideoHeight are available in VideoView
    // but are not directly accessible. We can get them via reflection if needed,
    // but they are set internally before onMeasure is called after media is prepared.
    // For simplicity, this example assumes they are populated. A more robust
    // solution might involve a listener on the MediaPlayer.

    private val mVideoWidth: Int
        get() {
            return getField("mVideoWidth")
        }

    private val mVideoHeight: Int
        get() {
            return getField("mVideoHeight")
        }

    private fun getField(name: String): Int {
        return try {
            val field = VideoView::class.java.getDeclaredField(name)
            field.isAccessible = true
            field.get(this) as Int
        } catch (e: Exception) {
            0
        }
    }
}