package com.zebra.zec500_overlay_app

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlin.properties.Delegates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Zec500OverlayServiceConsumerActivity : AppCompatActivity()  {

    private lateinit var ctx: Context
    private lateinit var startForResult: androidx.activity.result.ActivityResultLauncher<Intent>


    companion object {
        const val TAG = "Zec500OverlayServiceConsumerActivity"
        lateinit var metrics: DisplayMetrics
        var density by Delegates.notNull<Int>()

        lateinit var mediaProjection: MediaProjection
        lateinit var imageReader: ImageReader
        lateinit var virtualDisplay: VirtualDisplay
    }

    //RESULTS FROM LAUNCHES ACTIVITY ARE HANDLED HERE EITHER FROM registerForActivityResult or onActivityResult method


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        density = metrics.densityDpi

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
                Log.i( "registerForActivityResult", "--> registerForActivityResult/onActivityResult RESULT_OK data=${result.data?.data.toString()} resultCode=${result.resultCode}")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(  "onActivityResult", "--> onActivityResult requestCode: $requestCode resultCode: $resultCode data: ${data?.data.toString()}")

        //Toast.makeText(this, "onActivityResult requestCode: $requestCode resultCode: $resultCode data: ${data?.data.toString()}", Toast.LENGTH_LONG).show()
    }



    val ACTION_SHOW_QR: String = "com.zebra.zec500_overlay_service.SHOW_QR"
    val ACTION_HIDE_QR: String = "com.zebra.zec500_overlay_service.HIDE_QR"
    //val EXTRA_QR_BITMAP: String = "qr_bitmap"

    val ACTION_SET_CAPTION_TEXT: String = "com.zebra.zec500_overlay_service.SET_CAPTION_TEXT"
    val EXTRA_CAPTION_TEXT: String = "qr_bitmap"

    val ACTION_SET_TRANSPARENT_BACKGROUND = "qr_set_transparent"


    fun onClickbtn_ZECSCHEMA(v: View?) {
        try {

            val schemaIntent = Intent()

            schemaIntent.action = Intent.ACTION_VIEW
            schemaIntent.data = Uri.parse("zec500://scan-to-pair?SHOW_QR&SET_CAPTION_TEXT=ABC123")
            startActivity(schemaIntent)


        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_ZECSCHEMA" + e.message)
        }
    }
    fun onClickbtn_OVERLAYSERVICE(v: View?) {
        try {

            val serviceIntent = Intent()
            serviceIntent.component = ComponentName(
                "com.zebra.zec500_overlay_service",
                "com.zebra.zec500_overlay_service.OverlayService"
            )
            serviceIntent.action = ACTION_SHOW_QR
            //serviceIntent.putExtra(EXTRA_QR_BITMAP, "TEST VALUE")
            startForegroundService(serviceIntent)

        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_OVERLAYSERVICE " + e.message)
        }
    }




    fun onClickbtn_SETTRANSPARENT(v: View?) {
        try {

            val serviceIntent = Intent()
            serviceIntent.component = ComponentName(
                "com.zebra.zec500_overlay_service",
                "com.zebra.zec500_overlay_service.OverlayService"
            )
            serviceIntent.action = ACTION_SET_TRANSPARENT_BACKGROUND


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_SETTRANSPARENT " + e.message)
        }
    }


    fun onClickbtn_HIDEOVERLAY(v: View?) {
        try {

            val serviceIntent = Intent()
            serviceIntent.component = ComponentName(
                "com.zebra.zec500_overlay_service",
                "com.zebra.zec500_overlay_service.OverlayService"
            )
            serviceIntent.action = ACTION_HIDE_QR


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_HIDEOVERLAY " + e.message)
        }
    }






    override fun onStart() {
        super.onStart()


        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }


    override fun onStop() {
        super.onStop()


        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }






}