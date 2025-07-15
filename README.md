
### SCREENSHOTS
![image](https://github.com/user-attachments/assets/92c8505d-9b28-4c60-948e-62f6800e64fc)

![image](https://github.com/user-attachments/assets/63f36d05-b0ba-47f4-896f-e61f2dc40588)




# Deep Dive into the ZEC500 Developer's User Guide SampleApp: the wireless connection workflow

Welcome, developers! In this article, we’ll explore the ZebraDevs/ZEC500-DEV-UG-SampleApp repository—a comprehensive sample project that demonstrates simple it is to integrate into your line of business application a ZEC500 connection workflow.

---

## Project Overview & Structure

The repository is organized into three main modules, each targeting a specific use case:

- **zec500-embedded-qrcode-standalone-fvm**  
  A standalone app that generates and displays QR codes and demonstrates device-friendly name discovery. This module utilizes the Fragment + ViewModel architecture, LiveData, and does not use overlay windows.  
- **zec500-overlay-service**  
  The core overlay service module. It provides the underlying Android Service that can draw overlays—such as QR codes—on top of any app. This is the foundation for any application or module that requires persistent overlays.
- **zec500-overlay-app**  
  A sample consumer of the overlay service, demonstrating how to interact with and benefit from the overlay capabilities provided by the service module. Utilizes Jetpack Compose for its UI.

---

## Key Features & Code Highlights

### 1. QR Code Generation

Both the standalone app and overlay service generate QR codes using the ZXing library. Here’s how the overlay service accomplishes this:

```java
private Bitmap generateQrCode(String content) {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    int width = 300, height = 400;
    BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    for (int x = 0; x < width; x++)
        for (int y = 0; y < height; y++)
            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
    return bitmap;
}
```
In the standalone app, generated QR codes are simply displayed in the app’s UI, while in the overlay service, they are rendered in overlay windows visible atop all other apps.

---

### 2. Device Friendly Name Discovery

In `zec500-embedded-qrcode-standalone-fvm`, the `DeviceFriendlyNameDiscoverer` class leverages WiFi Direct APIs to gather device-friendly names and MAC addresses—a valuable capability for peer-to-peer and provisioning workflows.

```java
public void requestDeviceInfo(Fragment fragment, DeviceInfoCallback callback) {
    manager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
        @Override
        public void onDeviceInfoAvailable(WifiP2pDevice wifiP2pDevice) {
            if (wifiP2pDevice != null) {
                String wifiMacAddress = wifiP2pDevice.deviceAddress;
                String devName = wifiP2pDevice.deviceName;
                if (callback != null) callback.onDeviceInfoAvailable(devName);
            }
        }
    });
}
```
This feature is integrated into the UI via ViewModel and LiveData, so the user is always kept up-to-date.

---

### 3. Standalone App Architecture

The `zec500-embedded-qrcode-standalone-fvm` module is a showcase of clean Android architecture without overlays. It follows the MVVM (Model-View-ViewModel) pattern: Fragments interact with a ViewModel that holds all UI-related data and state using LiveData. User actions—such as generating a QR code or discovering the device name—are handled through the ViewModel, ensuring a clear separation between the UI and business logic. LiveData observers in the Fragment automatically update the UI when the state changes (for example, when a new QR code bitmap is generated or when device info is discovered). This design makes the app robust, scalable, and easy to test, while also providing a smooth and reactive user experience.

---

### 4. Overlay Service Architecture

The `zec500-overlay-service` module is pivotal—it implements an Android foreground service capable of displaying overlay windows. The overlay app module demonstrates how to consume this service, making it easy to add overlay functionality to other projects.

Key architectural features include:

- **Notification Channels & Foreground Service**: Ensures overlays are persistent, user-aware, and comply with modern Android background execution policies.
- **Overlay Permission Management**: Handles permissions needed for SYSTEM_ALERT_WINDOW overlays.
- **Composable UI**: For overlay app consumers, Jetpack Compose enables rapid, modern UI development.

---

### 5. Modern Android Patterns

- **MVVM (Model-View-ViewModel)**: The standalone app cleanly separates logic and UI using ViewModel and LiveData.
- **Jetpack Compose**: Used in overlay app for declarative, reactive UI.
- **Kotlin & Java Interoperability**: Both languages are used across modules, showcasing integration patterns.

---

## Usage Scenarios

- **Standalone QR Code Generation**: If you simply want to display QR codes and device info, use the `zec500-embedded-qrcode-standalone-fvm` module.
- **Advanced Overlay Use**: For persistent, global overlays (such as QR codes or device info shown over any app), use the `zec500-overlay-service` as your foundation. Extend or customize as needed, and see the overlay app for integration patterns.

---

## Screenshots & UI

Check the README for screenshots and UI examples that demonstrate both the standalone and overlay use cases.

---

## Build & Dependencies

The project is configured for recent Android SDKs, with up-to-date dependencies such as AndroidX, Jetpack Compose, and ZXing. Each module’s Gradle configuration ensures compatibility and smooth development:

- Java 11–21 and Kotlin
- Modern AndroidX libraries
- Jetpack Compose (where applicable)

---

## Conclusion

NDZL/ZEC500-DEV-UG-SampleApp is a practical, modular example of how to build both standalone and overlay-driven Android apps featuring QR codes and device discovery. Whether you need a simple QR generator or a robust overlay service foundation, this repo is an excellent starting point.

**Explore the code:** [NDZL/ZEC500-DEV-UG-SampleApp on GitHub](https://github.com/NDZL/ZEC500-DEV-UG-SampleApp)
