package com.zebra.zec500_overlay_standalone_fragmentviewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.IOException
import java.io.OutputStream
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
            caption: String? = null,
            context: Context? = null
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

                    saveImageToPicturesViaMediaStore(context!!, exportBmp, exportTo.fileName.toString())

                }

                qrBmp
            } catch (e: WriterException) {
                e.printStackTrace()
                null
            }
        }

        private fun saveImageToPicturesViaMediaStore(context: Context, bitmap: Bitmap, fileName: String) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                // RELATIVE_PATH is only available on Android 10 (API 29) and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    put(MediaStore.MediaColumns.IS_PENDING, 1) // Mark as pending while writing
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let { imageUri ->
                try {
                    val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                    outputStream?.use { stream ->
                        // Compress the bitmap to the output stream
                        if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                            throw IOException("Failed to save bitmap")
                        }
                    }

                    // Once finished, mark IS_PENDING as 0 so other apps can see it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        resolver.update(imageUri, contentValues, null, null)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    // Clean up the empty file if the write failed
                    resolver.delete(imageUri, null, null)
                }
            }
        }
    }
}