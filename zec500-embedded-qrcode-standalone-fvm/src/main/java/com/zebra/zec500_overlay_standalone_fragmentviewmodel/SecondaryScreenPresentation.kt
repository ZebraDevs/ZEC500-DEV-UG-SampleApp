package com.zebra.zec500_overlay_standalone_fragmentviewmodel

import android.R
import android.app.Presentation
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Display
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.fragment.app.FragmentTransaction
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main.PairingFragment


class SecondaryScreenPresentation(private val activityContext: Context, display: Display) : Presentation(activityContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val frameLayout = FrameLayout(context)
        frameLayout.id = android.R.id.content
        setContentView(frameLayout)

        //show the PairingFragment
        val fragmentTransaction: FragmentTransaction = MainActivity.maincontext.supportFragmentManager.beginTransaction()
        val pairingFragment = PairingFragment()
        fragmentTransaction.replace(android.R.id.content, pairingFragment)
        fragmentTransaction.commit()



    }

}