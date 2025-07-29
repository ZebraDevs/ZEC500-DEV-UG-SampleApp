package com.zebra.zec500_overlay_service;

import android.app.ComponentCaller;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/*USED TO CAPTURE CUSTOM SCHEMA CALLS AND START THE OVERLAY SERVICE INDIRECTLY
* THIS ACTIVITY IS TRANSPARENT
* AND TERMINATES ITSELF AS SOON AS IF HAS STARTED THE SERVICE*/

public class MITMActivity extends AppCompatActivity {

    public static final String ACTION_SHOW_QR = "com.zebra.zec500_overlay_service.SHOW_QR";
    public static final String ACTION_HIDE_QR = "com.zebra.zec500_overlay_service.HIDE_QR";
    public static final String EXTRA_QR_BITMAP = "qr_bitmap";

    public static final String ACTION_SET_CAPTION_TEXT = "com.zebra.zec500_overlay_service.SET_CAPTION_TEXT";
    public static final String EXTRA_CAPTION_TEXT = "qr_bitmap";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_qr);
        Log.i("MITMActivity", "onCreate: MITMActivity started");

        handleIntent(getIntent());

    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onNewIntent(@NonNull Intent intent, @NonNull ComponentCaller caller) {
        super.onNewIntent(intent, caller);
        Log.i("MITMActivity", "onNewIntent: MITMActivity received");
        handleIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            if ("zec500".equals(data.getScheme())) {
                // Get the path
                String path = data.getPath();
                // Get the host
                String host = data.getHost();
                // Get parameters
                boolean isShowQRParam = data.getQueryParameterNames().contains("SHOW_QR");
                String setCaptionParam = data.getQueryParameter("SET_CAPTION_TEXT");

                if( isShowQRParam ){
                    Log.i("MITMActivity", "SHOW_QR action was called");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction(ACTION_SHOW_QR);
                    startForegroundService(serviceIntent);
                }

                //sleep 300ms
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(setCaptionParam!=null){
                    Log.i("MITMActivity", "SET_CAPTION_TEXT action was called with param <"+setCaptionParam+">");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction( ACTION_SET_CAPTION_TEXT );
                    serviceIntent.putExtra(EXTRA_CAPTION_TEXT, setCaptionParam);
                    startForegroundService(serviceIntent);
                }

            }
        }
        finish();
    }

}
