package com.avaya.axp.omni.sample.messaging

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApplication:Application(){
    override fun onCreate() {
        Log.d("MyApplication", "onCreate")
        FirebaseApp.initializeApp(this)
        super.onCreate()
    }
}
