package com.zebra.zec500_overlay_standalone_fragmentviewmodel;

import static android.os.Looper.getMainLooper;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main.MainViewModel;

import java.util.Map;

public class DeviceFriendlyNameDiscoverer {

    public static final int REQUEST_PERMISSIONS_CODE = 100;
    public String devName = null;
    public String wifiMacAddress = null;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter intentFilter;
    private boolean permissionsGranted = false;
    //private final ActivityResultLauncher<String[]> permissionLauncher;
    private final Context context;
    private final Fragment globalFragment;

    public DeviceFriendlyNameDiscoverer(Fragment fragment) {
        this.context = fragment.requireContext();
        globalFragment = fragment;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.i("DeviceFriendlyNameDiscoverer", "Initializing WifiP2pManager");
            manager = (WifiP2pManager) context.getSystemService(context.WIFI_P2P_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.i("DeviceFriendlyNameDiscoverer", "Initializing WifiP2pManager.Channel");
            channel = manager.initialize(context, getMainLooper(), null);
        }

    }

    public boolean hasPermissions() {
        Log.i("DeviceFriendlyNameDiscoverer", "Checking permissions");
        permissionsGranted = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED;
        Log.i("DeviceFriendlyNameDiscoverer", "Permissions granted: " + permissionsGranted);
        return permissionsGranted;
    }

    public void requestPermissions(Activity activity) {
        Log.i("DeviceFriendlyNameDiscoverer", "Requesting permissions");

        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES},
                REQUEST_PERMISSIONS_CODE);
    }


    public void initiateDiscovery() {
        if (hasPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("DeviceFriendlyNameDiscoverer", "initiateDiscovery-success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e("DeviceFriendlyNameDiscoverer", "initiateDiscovery failed: " + reason);
                        Log.i("DeviceFriendlyNameDiscoverer", "HINTS: ensure WIFI radio is on, and that LOCATIONING SERVICES are on. Just granting location permission is not enough.");
                        //Toast.makeText(MainActivity.this, "Peer discovery failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void requestDeviceInfo(Fragment fragment, DeviceInfoCallback callback) {
        if (channel != null && manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (fragment.requireActivity().checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || globalFragment.requireActivity().checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {

                    return ;
                }
                manager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
                    @Override
                    public void onDeviceInfoAvailable(WifiP2pDevice wifiP2pDevice) {

                        if (wifiP2pDevice != null) {
                            wifiMacAddress = wifiP2pDevice.deviceAddress;
                            devName = wifiP2pDevice.deviceName;
                            Log.i("requestDeviceInfo", "DEVICE FRIENDLY NAME: "+devName + " WIFI-MAC: " + wifiMacAddress);
                            if (callback != null) {
                                callback.onDeviceInfoAvailable(devName);
                            }
                        }

                    }
                });
            }
        }

    }


}
