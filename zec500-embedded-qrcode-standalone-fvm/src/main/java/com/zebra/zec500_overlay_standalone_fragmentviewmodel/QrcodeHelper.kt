package com.zebra.zec500_overlay_standalone_fragmentviewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.nio.file.Path
import kotlin.math.ceil

class QrcodeHelper {
    companion object {
        fun generateQrCode(
            content: String,
            width: Int = 250,
            height: Int = 250,
            darkColor: Int = Color.DKGRAY,
            lightColor: Int = Color.WHITE,
            exportTo: Path? = null,
            caption: String? = null
        ): Bitmap? {
            return try {
                val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height)

                val qrBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            setPixel(x, y, if (matrix.get(x, y)) darkColor else lightColor)
                        }
                    }
                }

                if (exportTo != null) {
                    val captionText = caption?.trim().orEmpty()

                    val exportBmp: Bitmap = if (captionText.isNotEmpty()) {
                        val pad = (width * 0.06f).toInt().coerceAtLeast(12)
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = darkColor
                            textAlign = Paint.Align.CENTER
                            textSize = (width * 0.085f).coerceAtLeast(18f)
                        }

                        val fm = paint.fontMetrics
                        val textHeight = ceil((fm.descent - fm.ascent).toDouble()).toInt()
                        val captionAreaHeight = textHeight + (pad * 2)

                        Bitmap.createBitmap(width, height + captionAreaHeight, Bitmap.Config.ARGB_8888).apply {
                            val canvas = Canvas(this)
                            canvas.drawColor(lightColor)
                            canvas.drawBitmap(qrBmp, 0f, 0f, null)
                            val textY = height + pad - fm.ascent
                            canvas.drawText(captionText, width / 2f, textY, paint)
                        }
                    } else {
                        qrBmp
                    }

                    val file = exportTo.toFile()
                    file.parentFile?.mkdirs()
                    file.outputStream().use { os ->
                        exportBmp.compress(Bitmap.CompressFormat.PNG, 100, os)
                    }
                }

                qrBmp
            } catch (e: WriterException) {
                e.printStackTrace()
                null
            }
        }
    }
}