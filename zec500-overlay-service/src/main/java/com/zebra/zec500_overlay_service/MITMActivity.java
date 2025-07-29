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

    public static final String ACTION_SET_TRANSPARENT_BACKGROUND = "qr_set_transparent";
    public static final String ACTION_SET_OVERLAY_COLOR = "qr_set_color";
    public static final String ACTION_SET_OVERLAY_SIZE = "qr_set_size";
    public static final String ACTION_SET_OVERLAY_POSITION = "qr_set_position";

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

                String setTransparentBackgroundParam = data.getQueryParameter("SET_TRANSPARENT_BACKGROUND");
                String setOverlayColorParam = data.getQueryParameter("SET_OVERLAY_COLOR");
                String setOverlaySizeParam = data.getQueryParameter("SET_OVERLAY_SIZE");
                String setOverlayPositionParam = data.getQueryParameter("SET_OVERLAY_POSITION");


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

                //for each of the set valriables above, generate code like for the setCaptionParam case
                if(setTransparentBackgroundParam!=null){
                    Log.i("MITMActivity", "SET_TRANSPARENT_BACKGROUND action was called with param <"+setTransparentBackgroundParam+">");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction(ACTION_SET_TRANSPARENT_BACKGROUND);
                    startForegroundService(serviceIntent);
                }
                if(setOverlayColorParam!=null){
                    Log.i("MITMActivity", "SET_OVERLAY_COLOR action was called with param <"+setOverlayColorParam+">");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction(ACTION_SET_OVERLAY_COLOR);
                    serviceIntent.putExtra("overlay_color", setOverlayColorParam);
                    startForegroundService(serviceIntent);
                }
                if(setOverlaySizeParam!=null){
                    Log.i("MITMActivity", "SET_OVERLAY_SIZE action was called with param <"+setOverlaySizeParam+">");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction(ACTION_SET_OVERLAY_SIZE);
                    serviceIntent.putExtra("overlay_size", setOverlaySizeParam);
                    startForegroundService(serviceIntent);
                }
                if(setOverlayPositionParam!=null){
                    Log.i("MITMActivity", "SET_OVERLAY_POSITION action was called with param <"+setOverlayPositionParam+">");
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(
                            "com.zebra.zec500_overlay_service",
                            "com.zebra.zec500_overlay_service.OverlayService"
                    ));
                    serviceIntent.setAction(ACTION_SET_OVERLAY_POSITION);
                    serviceIntent.putExtra("overlay_position", setOverlayPositionParam);
                    startForegroundService(serviceIntent);
                }


            }
        }
        finish();
    }

}
