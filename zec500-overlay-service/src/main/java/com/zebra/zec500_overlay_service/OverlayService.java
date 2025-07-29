package com.zebra.zec500_overlay_service;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;




/*
NOTES FOR THE DEVELOPER

ADD
    <queries>
        <package android:name="com.zebra.zec500_overlay_service" />
    </queries>
TO THE CALLING APP IF YOU WANT THIS SERVICE TO BE STARTED WITH THE CLASSNAME INTENT
            val serviceIntent = Intent()
            serviceIntent.component = ComponentName(
                "com.zebra.zec500_overlay_service",
                "com.zebra.zec500_overlay_service.OverlayService"
            )
            startForegroundService(serviceIntent)
 */

public class OverlayService extends Service {


    private WindowManager windowManager;
    private View floatingView;
    private ImageView closeArea;
    private ImageView qrImageView;

    private TextView qrTextView;
    private String wifip2pDeviceName="";

    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public static final String ACTION_SHOW_QR = "com.zebra.zec500_overlay_service.SHOW_QR";
    public static final String  ACTION_HIDE_QR = "com.zebra.zec500_overlay_service.HIDE_QR";
    public static final String ACTION_SET_CAPTION_TEXT = "com.zebra.zec500_overlay_service.SET_CAPTION_TEXT";
    public static final String EXTRA_QR_BITMAP = "qr_bitmap";
    public static final String EXTRA_CAPTION_TEXT = "qr_bitmap";
    public static final String ACTION_SET_TRANSPARENT_BACKGROUND = "qr_set_transparent";
    public static final String ACTION_SET_OVERLAY_COLOR = "qr_set_color";
    public static final String ACTION_SET_OVERLAY_SIZE = "qr_set_size";
    public static final String ACTION_SET_OVERLAY_POSITION = "qr_set_position";

    String EXTRA_COLOR = "qr_color";

    String EXTRA_SIZE = "qr_size";

    String EXTRA_POSITION = "qr_position";

    int QRColor = Color.BLACK;
    int QRsize = 300;


    public OverlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        DeviceFriendlyNameDiscoverer discoverer = new DeviceFriendlyNameDiscoverer(this);
        discoverer.initiateDiscovery();


        Log.i("OverlayService", "onStartCommand called");
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SHOW_QR:
                    //String qrData = intent.getStringExtra(EXTRA_QR_BITMAP);
                    Log.i("OverlayService", "calling showOverlayView");
                    discoverer.requestDeviceInfo( new DeviceInfoCallback() {

                          @Override
                          public void onDeviceInfoAvailable(@Nullable String devName) {
                              Log.i("onDeviceInfoAvailable", "DEVICE NAME: "+devName);
                              wifip2pDeviceName = devName;
                              showOverlayView(floatingView, wifip2pDeviceName);
                              qrTextView.setText(wifip2pDeviceName);
                          }
                      }

                    );

                    break;
                case ACTION_SET_CAPTION_TEXT:
                    String capTxt = intent.getStringExtra(EXTRA_CAPTION_TEXT);
                    Log.i("OverlayService", "setting overlay text "+capTxt);
                    qrTextView.setText(capTxt);
                    break;
                case ACTION_HIDE_QR:
                    Log.i("OverlayService", "calling hideOverlayView");
                    hideOverlayView(floatingView);
                    break;
                case ACTION_SET_TRANSPARENT_BACKGROUND:
                    Log.i("OverlayService", "calling ACTION_SET_TRANSPARENT_BACKGROUND");
                    // Set the background of the floating view to transparent is now the default behavior
                    break;

                case ACTION_SET_OVERLAY_COLOR:
                    Log.i("OverlayService", "calling ACTION_SET_OVERLAY_COLOR");
                    QRColor = intent.getIntExtra(EXTRA_COLOR, Color.BLACK);
                    invalidateOverlayView(floatingView, wifip2pDeviceName);
                    break;
                case ACTION_SET_OVERLAY_SIZE:
                    Log.i("OverlayService", "calling ACTION_SET_OVERLAY_SIZE");
                    QRsize = intent.getIntExtra(EXTRA_SIZE, 300);
                    invalidateOverlayView(floatingView, wifip2pDeviceName);
                    break;
                case ACTION_SET_OVERLAY_POSITION:
                    Log.i("OverlayService", "calling ACTION_SET_OVERLAY_POSITION");
                    //generate a random number between 50 and 500
                    params.x = (int) (Math.random() * (500 - 50 + 1)) + 50;
                    params.y = (int) (Math.random() * (500 - 50 + 1)) + 50;
                    invalidateOverlayView(floatingView, wifip2pDeviceName);
                    break;

                        //other APIs can be added here. e.g. qrcode dimension, color, etc.
            }
        }
        return START_STICKY;
    }

    public void requestPermissions(Activity activity) {
        Log.i("DeviceFriendlyNameDiscoverer", "Requesting permissions");

        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES},
                1001);
    }

    void showOverlayView(View floatingView1, String qrcodeData){
        if (floatingView1.getWindowToken() != null) {
            return;
        }
        else{
            Bitmap qrbmp = generateQrCode(qrcodeData);
            if (qrbmp != null) {
                qrImageView.setImageBitmap( BitmapUtils.makeWhitePixelsTransparent(qrbmp) );
            }
            windowManager.addView(floatingView1, params);
        }
    }

    void hideOverlayView(View floatingView1){
        if (floatingView1.getWindowToken() != null) {
            windowManager.removeView(floatingView1);
        }
    }


    void invalidateOverlayView(View floatingView1, String qrcodeData){
        if (floatingView1.getWindowToken() != null) {
            windowManager.removeView(floatingView1);
            Bitmap qrbmp = generateQrCode(qrcodeData);
            if (qrbmp != null) {
                qrImageView.setImageBitmap( BitmapUtils.makeWhitePixelsTransparent(qrbmp) );
            }
            windowManager.addView(floatingView1, params);
        }
    }



    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "OverlayServiceChannel";

    private Bitmap generateQrCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            int width = QRsize;
            int height = QRsize;
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? QRColor : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    public class BitmapUtils {

        public static Bitmap makeWhitePixelsTransparent(Bitmap bitmap) {
            // Check if the original bitmap is RGB_565
            if (bitmap.getConfig() != Bitmap.Config.RGB_565) {
                // If not RGB_565, you might want to handle this case
                // or assume it already has an alpha channel if ARGB_8888
                // For this specific requirement, we assume the input is RGB_565
                // and needs conversion for transparency.
                // If the input is already ARGB_8888, the logic below will still work.
            }

            // Convert to ARGB_8888 to support alpha channel
            Bitmap argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            int width = argbBitmap.getWidth();
            int height = argbBitmap.getHeight();

            // Iterate through pixels
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = argbBitmap.getPixel(x, y);

                    // Check if the pixel is white (in ARGB_8888)
                    // Using Color.WHITE constant for clarity
                    if (pixel == Color.WHITE) {
                        // Set the alpha to 0 while keeping the color components
                        int transparentWhite = Color.TRANSPARENT;
                        argbBitmap.setPixel(x, y, transparentWhite);
                    }
                }
            }

            return argbBitmap;
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Log.i("OverlayService", "onCreate called");

        // Inflate the floating view layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_image_layout, null);
        closeArea = floatingView.findViewById(R.id.close_area);
        qrImageView = floatingView.findViewById(R.id.qrImg);
        qrTextView = floatingView.findViewById(R.id.qrText);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        // Make the floating view draggable
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long lastTapTime = 0;
            private static final long DOUBLE_TAP_TIMEOUT = 300; // milliseconds


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        //closeArea.setVisibility(View.VISIBLE); // Show X mark
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                                hideOverlayView(v);
                            return true;
                        }
                        lastTapTime = currentTime;
/*                        closeArea.setVisibility(View.INVISIBLE); // Hide X mark
                        // Check if dropped on X mark
                        int[] location = new int[2];
                        closeArea.getLocationOnScreen(location);
                        if (event.getRawX() > location[0] && event.getRawX() < location[0] + closeArea.getWidth() &&
                                event.getRawY() > location[1] && event.getRawY() < location[1] + closeArea.getHeight()) {
                            stopSelf(); // Close the service
                        }*/
                        return true;

                }
                return false;
            }
        });

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification(),  FOREGROUND_SERVICE_TYPE_LOCATION
        );



    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Overlay Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        // For Android 8.0+, we need a notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Overlay Active")
                    .setContentText("Tap to return to app")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        } else {
            // For older versions
            return new Notification.Builder(this)
                    .setContentTitle("Overlay Active")
                    .setContentText("Tap to return to app")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }
}