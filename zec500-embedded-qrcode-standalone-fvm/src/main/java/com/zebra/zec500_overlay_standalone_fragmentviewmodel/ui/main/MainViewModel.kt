package com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.Display
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.MainActivity
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.R
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.SecondaryScreenPresentation

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _qrCodeGenerationState = MutableLiveData<Boolean>()
    val qrCodeGenerationState: LiveData<Boolean> = _qrCodeGenerationState

    private val _qrDoSomethingState = MutableLiveData<Boolean>()
    val qrDoSomethingState: LiveData<Boolean> = _qrDoSomethingState

    private val _discoverDeviceName = MutableLiveData<Boolean>()
    val discoverDeviceNameState: LiveData<Boolean> = _discoverDeviceName

    public val _textViewState = MutableLiveData<String>()
    val textViewState: LiveData<String> = _textViewState

    private val _textQrState = MutableLiveData<String>()
    val textQrState: LiveData<String> = _textQrState

    private val _imageViewState = MutableLiveData<Bitmap?>()
    val imageViewState: LiveData<Bitmap> = _imageViewState as LiveData<Bitmap>

    private val _navigateToSecond = MutableLiveData<Boolean>()
    val navigateToSecond: LiveData<Boolean> = _navigateToSecond



    fun onDiscoverDeviceName(devName: String?) {
        _discoverDeviceName.value = true
    }
    fun onDiscoverDeviceNameComplete() {
        _discoverDeviceName.value = false
    }


    fun onNavigateToSecond() {
        _navigateToSecond.value = true
    }

    fun onNavigationComplete() {
        _navigateToSecond.value = false
    }

    fun onQrCodeButtonClick() {
        _qrCodeGenerationState.value = true
        _textQrState.value = _textViewState.value
        _imageViewState.value = generateQrCode(_textViewState.value.toString())
        Log.i("MainViewModel", "QR Code button clicked")
    }

    fun onPairUsingPresentationDialog() {
        Log.i("MainViewModel", "onPairUsingPresentationDialog")

        val displayManager = application.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val secondaryDisplay = displayManager.displays
            .firstOrNull { display -> display.displayId != Display.DEFAULT_DISPLAY }

        if (secondaryDisplay != null) {
            //Presentation only works on secondary displays! https://developer.android.com/reference/android/app/Presentation
            val presentation = SecondaryScreenPresentation(MainActivity.maincontext, secondaryDisplay)
            presentation.show()
        }
        else{
            onNavigateToSecond()
        }

    }

    fun onDoSomethingClick() {
        Log.i("MainViewModel", "Do something button clicked")

        _textViewState.value = getDeviceVersion()

        _qrDoSomethingState.value = true
    }


    fun getDeviceVersion(): String {
        //val info = WebView.getCurrentWebViewPackage()
        return "Device info " + Build.FINGERPRINT
    }

    private fun generateQrCode(content: String): Bitmap? {
        val qrCodeWriter: QRCodeWriter = QRCodeWriter()
        try {
            val width = 300
            val height = 400
            val bitMatrix: BitMatrix  =
                qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0..<width) {
                for (y in 0..<height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }
}