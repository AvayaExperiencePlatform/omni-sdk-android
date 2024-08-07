package com.avaya.axp.omni.sample.calling

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.avaya.axp.omni.sample.calling.config.SdkManager.Companion.instantiateSdkManager

class SampleApplication : Application() {

    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        super.onCreate()

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

        instantiateSdkManager()
    }
}
