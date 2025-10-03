package com.zebra.zec500_overlay_service;

import android.app.ActivityOptions;
import android.app.ComponentCaller;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;


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

            else if("wsc".equals(data.getScheme())){
                //Launch Chrome with the provided address
                Log.i("MITMActivity", "wsc schema detected, launching chrome with address: "+data.toString());
                String browser = "CHROME"; //default to chrome
                if(data.getQueryParameterNames().contains("URL2NDDISPLAY")){
                    String targetUrl = data.getQueryParameter("URL2NDDISPLAY");


                    if(data.getQueryParameterNames().contains("FIREFOX"))
                        browser = "FIREFOX";


                    try {
                        launchChromeOn2ndDisplay(targetUrl, browser);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        launchChromeOn2ndDisplay("https://www.zebra.com", browser);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        finish();
    }

    public void launchChromeOn2ndDisplay(String targetAddress, String browser) throws RemoteException {
        //Launch on 2nd display if available
        ActivityOptions ao = ActivityOptions.makeBasic();
        int other_display_id = 0;
        int cur_display_id = getDisplay().getDisplayId();
        if(cur_display_id>0){
            other_display_id = cur_display_id;
        } else {
            DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = displayManager.getDisplays();
            for (Display _d : displays) {
                if (_d.getDisplayId() >0 ) {
                    other_display_id = _d.getDisplayId();
                    break;
                }
            }
        }

        ao.setLaunchDisplayId(other_display_id);



        Bundle bao = ao.toBundle();

        Log.i("MITMActivity", "launchChromeOn2ndDisplay: Launching Chrome with address: "+targetAddress+" on display ID: "+other_display_id);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetAddress));


        if(browser.equals("CHROME"))
            intent.setComponent(new ComponentName("com.android.chrome", "com.google.android.apps.chrome.Main"));
        else
            intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.fenix.IntentReceiverActivity"));

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);

        // Make tab/task more distinct
        intent.putExtra("create_new_tab", true);
        intent.putExtra("com.android.browser.application_id",
                getPackageName() + ":ffx:" + System.nanoTime());

        startActivity(intent, bao);


    }

}
