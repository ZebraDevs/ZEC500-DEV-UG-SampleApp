package com.zebra.zec500_overlay_service

interface DeviceInfoCallback {
    fun onDeviceInfoAvailable(devName: String?)
}