package com.avaya.axp.client.sample_app_messaging.repository

import android.util.Log
import com.avaya.axp.client.sample_app_messaging.network.NotificationRegistrationService
import com.avaya.axp.client.sample_app_messaging.network.pushNotification.NotificationRegistrationRequest

interface NotificationRegistrationRepository{
    suspend fun saveDeviceRegistration(deviceToken:String,configId:String,sessionId:String):Boolean
}

class NotificationRegistrationRepositoryImpl(private val notificationRegistrationService: NotificationRegistrationService):
    NotificationRegistrationRepository {
        private val TAG = "NotificationRegistrationRepository"
    override suspend fun saveDeviceRegistration(deviceToken: String,configId: String,sessionId: String):Boolean{
        try {
            val request = NotificationRegistrationRequest(configId,sessionId)
            val response = notificationRegistrationService. saveDeviceRegistration(deviceToken,request)
            return response.isSuccessful
        }catch (e:Exception){
            Log.d(TAG,e.message.toString())
           return false
        }
    }
}