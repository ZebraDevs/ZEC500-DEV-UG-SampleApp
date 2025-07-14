package com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.DeviceFriendlyNameDiscoverer
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.DeviceInfoCallback
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.R

class MainFragment : Fragment() {

    private lateinit var discoverer: DeviceFriendlyNameDiscoverer

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Log.i("MainFragment", "All permissions granted")
            discoverer.initiateDiscovery()
            discoverer.requestDeviceInfo(this, object : DeviceInfoCallback {
                override fun onDeviceInfoAvailable(devName: String?) {
                    viewModel.onDiscoverDeviceName(devName)
                }
            })
        } else {
            Log.e("MainFragment", "Some permissions denied")
            // Handle denial case
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels(){
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.qrButton).setOnClickListener {
            viewModel.onQrCodeButtonClick()
        }

        view.findViewById<Button>(R.id.qrDoSomething).setOnClickListener {
            viewModel.onDoSomethingClick()
        }

//        view.findViewById<Button>(R.id.pairUsingPresentationDialog).setOnClickListener {
//            viewModel.onPairUsingPresentationDialog()
//        }

        view.findViewById<Button>(R.id.deviceDiscoverFriendlyName).setOnClickListener {
            viewModel.onDiscoverDeviceName("xyz")
        }

        val textView = view.findViewById<TextView>(R.id.tvOut)
        viewModel.textViewState.observe(viewLifecycleOwner) { newText ->
            textView.text = newText
        }

        val imgView = view.findViewById<ImageView>(R.id.qrImg)
        val textQr = view.findViewById<TextView>(R.id.qrText)
        viewModel.imageViewState.observe(viewLifecycleOwner) { bitmp ->
            Log.i("MainFragment", "ImageView received a bitmap: ${bitmp.height}x${bitmp.width}")
            imgView.setImageBitmap(bitmp)
            textQr.text = viewModel._textViewState.value

        }

        viewModel.qrCodeGenerationState.observe(viewLifecycleOwner) { showQrCode ->
            if (showQrCode) {
                Log.i("MainFragment", "QR Code generation state changed: $showQrCode")
            }
        }

        viewModel.qrDoSomethingState.observe(viewLifecycleOwner) { doSomething ->
            if (doSomething) {
                Log.i("MainFragment", "Do something state changed: ${doSomething}")

            }
        }
        viewModel.discoverDeviceNameState.observe(viewLifecycleOwner) { isDiscover ->
            if (isDiscover) {
                Log.i("MainFragment", "isDiscover device friendly name: ${isDiscover}")
                    //viewModel.onDiscoverDeviceNameComplete()


                discoverer = DeviceFriendlyNameDiscoverer(this)

                if (discoverer.hasPermissions()) {
                    discoverer.initiateDiscovery()
                    discoverer.requestDeviceInfo(this, object : DeviceInfoCallback {
                        override fun onDeviceInfoAvailable(devName: String?) {
                            viewModel._textViewState.value = devName
                        }
                    })
                }
                else
                    requestPermissions(this.requireActivity())

            }

        }

        viewModel.navigateToSecond.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                val stringToShow = viewModel._textViewState.value.toString()

                if (stringToShow.isNullOrEmpty() || stringToShow == "null") {
                    viewModel.onDiscoverDeviceName("xyz")

                    viewModel._textViewState.observe(viewLifecycleOwner) { value ->
                        if (!value.isNullOrEmpty()) {
                            val stringToShow = value
                            launch2NDfragment(stringToShow)
                        }
                    }
                }
                else{
                    launch2NDfragment(stringToShow)
                }
            }
        }

        view.findViewById<Button>(R.id.pairWithAnotherFragment).setOnClickListener {
            viewModel.onNavigateToSecond()
        }

    }

    private fun launch2NDfragment(stringToShow: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, PairingFragment.newInstance(stringToShow, stringToShow))
            .addToBackStack(null)
            .commit()
        viewModel.onNavigationComplete()
    }

    fun requestPermissions(activity: Activity) {
        Log.i("DeviceFriendlyNameDiscoverer", "Requesting permissions")

        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
        permissionsLauncher.launch(requiredPermissions)
    }



}

