package com.nenbeg.smart.app

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.tuya.smart.android.base.TuyaSmartSdk
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




        if (instanceCour == null) instanceCour = this


        PACKAGE_NAME = applicationContext.packageName



        TuyaSdk.init(this)

    }



    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //MultiDex.install(this)
    }


    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()

        try {
            TuyaUser.getDeviceInstance().onDestroy()
        }
        catch (err:Exception){

            err.printStackTrace()
        }
    }


    companion object {

        var userOwnerEmail:String=""
        var userOwnerUid:String=""
    }


}