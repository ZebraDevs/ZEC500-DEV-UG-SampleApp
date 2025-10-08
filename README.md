
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

### Install the service
- Build https://github.com/ZebraDevs/ZEC500-DEV-UG-SampleApp/tree/master/zec500-overlay-service
- Install the APK, e.g. with `adb install -g XXXXXXX`: no icons will be added to the launcher
  - Grant all the required permissions if installed otherwise, by manually navigating through Settings/Apps/All apps/Search... "overlay" 
  - Permissions page must look like this
  
    <img width="237" height="395" alt="image" src="https://github.com/user-attachments/assets/2258e35f-3f68-41ce-b8cc-bf63366ebedf" />
  - Also, ensure the "Display over other apps" is granted
    <img width="263" height="445" alt="image" src="https://github.com/user-attachments/assets/053b8d36-b428-4190-8845-7161379f2d91" />
    


