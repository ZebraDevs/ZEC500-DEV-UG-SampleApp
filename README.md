### Jan.2026 update - QRCODE EXPORT FEATURE ADDED

- starting v1.7 the `zec500-embedded-qrcode-standalone-fvm` module was added an export feature for the generated qrcode
- new, unified qrcode generation location is now [QrcodeHelper](https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/blob/master/zec500-embedded-qrcode-standalone-fvm/src/main/java/com/zebra/zec500_overlay_standalone_fragmentviewmodel/QrcodeHelper.kt)
- suggested usage: see e.g. [this line](https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/blob/5b5174ab1c14c0670fcdaadcfea71893c78cec50/zec500-embedded-qrcode-standalone-fvm/src/main/java/com/zebra/zec500_overlay_standalone_fragmentviewmodel/ui/main/PairingFragment.kt#L97)
  - define an export path (folder and filename), e.g. `val exportedQrcodeFileName = ...`
  - ensure the export path points to a public folder. e.g. `Environment.DIRECTORY_PICTURES`
  - when calling the _generateQrCode_ API, ensure to add the parameters `exportTo = exportedQrcodeFileName, caption = txtcaption.text.toString()`
    
    The caption is printed below the qrcode graphics in the exported image
- the exported image is a PNG format
- how the exported qrcode appear in File app

    <img width="393" height="192" alt="image" src="https://github.com/user-attachments/assets/2c6b480f-8583-4736-88a0-b205b3804b84" />

- and when open from File

  <img width="258" height="519" alt="image" src="https://github.com/user-attachments/assets/aefbe22d-4e19-412b-bd6c-fcae767ca3a4" />

---

### EXTERNAL VIDEO SUPPORT
- Starting v1.6, the following path can be used to playback an external video
`/sdcard/Movies/NRFParis2025.mp4`
- adb can be used to upload the file:
  `adb push "C:\Users\...your..path...\NRFParis2025.mp4" /sdcard/Movies`
- If no valid file is found at that location, internal videos are played back

### SCREENSHOTS
![image](https://github.com/user-attachments/assets/92c8505d-9b28-4c60-948e-62f6800e64fc)

![image](https://github.com/user-attachments/assets/63f36d05-b0ba-47f4-896f-e61f2dc40588)


ZEC500-BLOG-IS-HERE
https://github.com/NDZL/-blog-A14/wiki/@-ZEC500-BLOG-IS-HERE

---

## SERVICE-BASED WORKFLOWS

### Hardware setup (for the 2-display scenario)
- You need an external display to see this service opening a web page on a different screen
- Either using Workstation Connect / Wireless WSC
- Or working on a KC50/TD50 pair

### Install the service
- Build https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/tree/master/zec500-overlay-service
- Install the APK, e.g. with `adb install -g com.zebra.zec500_overlay_service.apk`: no icons will be added to the launcher
  - Grant all the required permissions if installed otherwise, by manually navigating through Settings/Apps/All apps/Search... "overlay" 
  - Permissions page must look like this
  
    <img width="237" height="395" alt="image" src="https://github.com/user-attachments/assets/2258e35f-3f68-41ce-b8cc-bf63366ebedf" />
  - Also, ensure the "Display over other apps" is granted
  
    <img width="263" height="445" alt="image" src="https://github.com/user-attachments/assets/053b8d36-b428-4190-8845-7161379f2d91" />

### Install Another Browser (for the 2-display scenario)
- Opening a web page on a different screen requires another browser to be installed along with Chrome
- In this sample app, Firefox was used, and its components are hard-coded here https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/blob/210ce59ffe2459af168bef38ec4368f5e4db5fdd/zec500-overlay-service/src/main/java/com/zebra/zec500_overlay_service/MITMActivity.java#L234
- Work around that code line to manage additional browsers

### Publish the Web App consuming the service and Open it
- Publish https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/blob/master/zec500-overlay-app-js/zec500.html on a public web server
  - e.g. https://cxnt48.com/zec500.html
- zec500.html is using "https://cxnt48.com/radar" as target web page for the external display. Edit it at your convenience.

## Observe the 2-display scenario's behavior

<img width="481" height="551" alt="image" src="https://github.com/user-attachments/assets/c1b86c44-b968-4af0-9649-3ac327edbb34" />


- If Chrome is used to open zec500.html page
  - then clicking on the link "Open Browser on 2nd Display (Chrome-default)" _fails_ to steadily open another webpage on the external display. This is the _re-parenting_ browser's feature in action.
  - while clicking on "Open Browser on 2nd Display (Firefox-must be installed)" it usually opens a webpage on the external display



https://github.com/user-attachments/assets/94d38401-010a-4a42-b86a-a6763bf041a7




The other links in that HMTL page can be used to display the ZEC500 Connection QrCode as an overlay.




