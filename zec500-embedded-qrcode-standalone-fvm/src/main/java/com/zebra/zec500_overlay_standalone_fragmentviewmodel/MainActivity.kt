package com.zebra.zec500_overlay_standalone_fragmentviewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zebra.zec500_overlay_standalone_fragmentviewmodel.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        maincontext = this
    }

    companion object{
        var maincontext: MainActivity = MainActivity()
    }



}