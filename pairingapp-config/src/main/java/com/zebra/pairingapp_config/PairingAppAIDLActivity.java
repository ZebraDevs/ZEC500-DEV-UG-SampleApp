package com.zebra.pairingapp_config;

import static android.widget.Toast.makeText;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;


public class PairingAppAIDLActivity extends AppCompatActivity  implements ServiceConnection {

    Intent starterIntent;
    private com.zebra.valueadd.IZVAService  iServiceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();

        setContentView(R.layout.activity_hdlauncher);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClickbtn_StatusIcons(View v) {
        callProcessZVA("wwsc_sample_config.json");
    }
    public void onClickbtn_AppsBehaviors(View v) {
        callProcessZVA("wwsc_reset_config.json");
    }



    void callProcessZVA(String jsonConfig){
        try {
            if (iServiceBinder != null) {
                //String dataSet = loadJSONFromSDCard();

                String dataSet = loadJSONFromAsset( jsonConfig );
                String response = iServiceBinder.processZVARequest(dataSet);
                Log.i("callProcessZVA", "processZVARequest response=" + response);
                makeText(this, "processZVARequest response=" + response, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("callProcessZVA", "res " + null);
                makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            makeText(this, "ZVA Excp \n"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindtoZVAService();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        iServiceBinder = com.zebra.valueadd.IZVAService.Stub.asInterface(service);
        Log.e("TAG", "WSC connected");
        makeText(this, "WSC Connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        makeText(getApplicationContext(), "IPC server has disconnected unexpectedly", Toast.LENGTH_LONG).show();
        iServiceBinder = null;

    }
    String pkg="com.zebra.wirelessconnect";
    private void bindtoZVAService() {
        Intent intent = new Intent("com.zebra.wirelessconnect");
        intent.setClassName("com.zebra.wirelessconnect", "com.zebra.wirelessconnect.DeviceManagementService");
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    public String loadJSONFromAsset(String jsonAssetFileName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(jsonAssetFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }




}