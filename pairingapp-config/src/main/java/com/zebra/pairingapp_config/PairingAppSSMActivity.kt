package com.zebra.pairingapp_config

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

import kotlin.Exception
import kotlin.Int
import kotlin.toString


class PairingAppSSMActivity : AppCompatActivity()  {

    companion object {
        const val TAG = "PairingAppSSMActivity"

    }

    lateinit var tvOut: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        tvOut = findViewById(R.id.textView)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(  "onActivityResult", "--> onActivityResult requestCode: $requestCode resultCode: $resultCode data: ${data?.data.toString()}")

        //Toast.makeText(this, "onActivityResult requestCode: $requestCode resultCode: $resultCode data: ${data?.data.toString()}", Toast.LENGTH_LONG).show()
    }


    fun onClickbtn_SSMRESET(v: View?) {
        try {
            val extJsonPath = copyAssetFileToExternalStorage("wwsc_reset_config.json")?.absolutePath
            SendFileToSSM( extJsonPath!! )

        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_SSMRESET" + e.message)
            tvOut.text = e.message
        }
    }


    fun onClickbtn_SSMCONFIG(v: View?) {
        try {
            val extJsonPath = copyAssetFileToExternalStorage("wwsc_sample_config.json")?.absolutePath
            SendFileToSSM( extJsonPath!! )

        } catch (e: Exception) {
            Log.e("msft", "onClickbtn_SSMCONFIG" + e.message)
            tvOut.text = e.message
        }
    }

    private fun loadTextFileFromAsset(assetFileName: String): String {
        return assets.open(assetFileName).bufferedReader().use { it.readText() }
    }

    private fun copyAssetFileToExternalStorage(
        assetFileName: String,
        relativeDir: String = getExternalFilesDir(null).toString(),
        outputFileName: String = assetFileName
    ): File? {
        val baseDir = relativeDir



        val outFile = File(baseDir, outputFileName)
        if (!outFile.exists()) {
            outFile.createNewFile()
        }

        return try {
            assets.open(assetFileName).use { input ->
                java.io.FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
                Log.i(TAG, "copyAssetFileToExternalStorage: " + outFile.absolutePath)
                tvOut.text = outFile.absolutePath
            }
            outFile
        } catch (e: Exception) {
            Log.e(TAG, "copyAssetFileToExternalStorage: " + e.message)
            tvOut.text = e.message
            null
        }
    }

    private val AUTHORITY_FILE = "content://com.zebra.securestoragemanager.securecontentprovider/files/"
    private val signature = ""

    fun SendFileToSSM(sourcePath: String) {
        val file = File(sourcePath)
        Log.i(TAG, "copyFileViaSSM: " + file)
        val contentUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file)

        this.getApplicationContext().grantUriPermission(
            "com.zebra.securestoragemanager",
            contentUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        ) // Needed to grant permission for SSM to read the uri


        val cpUriQuery = Uri.parse(AUTHORITY_FILE + this.getPackageName())

        try {
            val values = ContentValues()
            values.put(
                "target_app_package",
                String.format(
                    "{\"pkgs_sigs\": [{\"pkg\":\"%s\",\"sig\":\"%s\"}]}",
                    "com.zebra.workstationconnect",
                    signature
                )
            )
            values.put(
                "data_name",
                contentUri.toString()
            ) // Passes the content uri as a input source
            values.put(
                "data_value",
                "com.zebra.wirelessconnect/wireless_connect_config.txt"
            )

            values.put("data_persist_required", false)
            val createdRow = this.getContentResolver().insert(cpUriQuery, values)
            Log.i(TAG, "SSM Insert File: " + createdRow.toString() + " -  " + values)
            tvOut.text = "SSM Insert File: " + createdRow.toString()
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "SSM Insert File - error: " + e.message + "\n\n")
            tvOut.text = e.message
        }
    }


    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        super.onStop()

    }






}