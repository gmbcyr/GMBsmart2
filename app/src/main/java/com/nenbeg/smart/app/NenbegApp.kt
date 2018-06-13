package com.nenbeg.smart.app

import android.app.Application
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.sdk.TuyaUser

class NenbegApp : Application() {

    private var instanceCour:NenbegApp?=null
    lateinit var PACKAGE_NAME: String

    @Synchronized
    fun getInstance(): NenbegApp? {

        return instanceCour
    }

    override fun onCreate() {
        super.onCreate()

        TuyaSdk.init(this)

        if(1>3){
            TuyaUser.getDeviceInstance().onDestroy()
        }

        if (instanceCour == null) instanceCour = this


        PACKAGE_NAME = applicationContext.packageName
    }


}