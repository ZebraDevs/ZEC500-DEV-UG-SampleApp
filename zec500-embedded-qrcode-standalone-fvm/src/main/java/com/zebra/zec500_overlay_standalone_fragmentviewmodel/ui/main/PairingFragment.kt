package com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.R
import androidx.activity.enableEdgeToEdge
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PairingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PairingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pairing, container, false)
    }

    override fun onResume() {
        super.onResume()
        val videoView = view?.findViewById<VideoView>(R.id.video_view)
        if (videoView != null && !videoView.isPlaying) {
            videoView.start()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PairingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PairingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val imgview = view.findViewById<ImageView>(R.id.qrImgPairing)
        imgview.setImageBitmap( makeWhitePixelsTransparent( generateQrCode(param1!!)!! ) )

        val txtcaption = view.findViewById<TextView>(R.id.qrTextPairing)
        txtcaption.text = param2!!

        val videoView = view.findViewById<VideoView>(R.id.video_view)

        val videoUri1 = Uri.parse("android.resource://${context?.packageName}/${R.raw.zhc}")

        val videoUri2 = Uri.parse("android.resource://${context?.packageName}/${R.raw.zebra_brand}")

        // Check for an external video file in the public Movies directory
        val externalMoviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val externalVideoFile = File(externalMoviesDir, "NRFParis2025.mp4")
        val videoUri3: Uri? = if (externalVideoFile.exists()) {
            Uri.fromFile(externalVideoFile)
        } else {
            null
        }

        val videoUri = videoUri3 ?: if (Math.random() < 0.5) videoUri1 else videoUri2

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
        }
        videoView.start()



    }

    private fun generateQrCode(content: String): Bitmap? {
        val qrCodeWriter: QRCodeWriter = QRCodeWriter()
        try {
            val width = 250
            val height = 250
            val bitMatrix: BitMatrix  =
                qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0..<width) {
                for (y in 0..<height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.DKGRAY else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }


    private fun makeWhitePixelsTransparent(bitmap: Bitmap): Bitmap {
        // Check if the original bitmap is RGB_565
        if (bitmap.config != Bitmap.Config.RGB_565) {
            // If not RGB_565, you might want to handle this case
            // or assume it already has an alpha channel if ARGB_8888
            // For this specific requirement, we assume the input is RGB_565
            // and needs conversion for transparency.
            // If the input is already ARGB_8888, the logic below will still work.
        }

        // Convert to ARGB_8888 to support alpha channel
        val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val width = argbBitmap.width
        val height = argbBitmap.height

        // Iterate through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = argbBitmap.getPixel(x, y)

                // Check if the pixel is white (in ARGB_8888)
                // Using Color.WHITE constant for clarity
                if (pixel == Color.WHITE) {
                    // Set the alpha to 0 while keeping the color components
                    val transparentWhite = Color.TRANSPARENT
                    argbBitmap.setPixel(x, y, transparentWhite)
                }
            }
        }

        return argbBitmap
    }

}