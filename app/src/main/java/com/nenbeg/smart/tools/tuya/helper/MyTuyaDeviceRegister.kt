package com.nenbeg.smart.tools.tuya.helper

import android.content.Context
import android.util.Log
import com.alibaba.wireless.security.open.nocaptcha.INoCaptchaComponent.errorCode
import com.alibaba.wireless.security.open.nocaptcha.INoCaptchaComponent.token
import com.tuya.smart.android.device.api.response.GwDevResp
import com.tuya.smart.sdk.TuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.enums.ActivatorModelEnum


class MyTuyaDeviceRegister(val context:Context) {

    private lateinit var  mTuyaActivator: ITuyaActivator
    private lateinit var activTokenListener:ITuyaActivatorGetToken
    private lateinit var mListener  : ITuyaSmartActivatorListener



    init {

         mListener = object : ITuyaSmartActivatorListener {


            override fun onActiveSuccess(p0: DeviceBean?) {

                Log.e("MyTuyaDeviceRegist","Registration successfull with the device->"+p0.toString())

                mTuyaActivator.stop();
//回调销毁
                mTuyaActivator.onDestroy();
            }

            override fun onError(errorCode: String, errorMsg: String) {

                Log.e("MyTuyaDeviceRegist","Registration FAILED with the msg->"+errorMsg+"_the msg->"+errorCode)

                mTuyaActivator.stop();
//回调销毁
                mTuyaActivator.onDestroy();
            }

            fun onActiveSuccess(gwDevResp: GwDevResp) {


            }

            override fun onStep(step: String, o: Any) {

                Log.e("MyTuyaDeviceRegist","Registration ON STEP with the step->"+step+"_the msg->"+o.toString())
            }
        }


         activTokenListener = object : ITuyaActivatorGetToken {

            override fun onSuccess(p0: String?) {

                Log.e("MyTuyaDeviceRegist"," ITuyaActivatorGetToken TOKEN creation  successfull with token->"+p0.toString())


                mTuyaActivator = TuyaActivator.getInstance().newActivator( ActivatorBuilder()
                        .setSsid("FIOS-YWMRW")
                        .setContext(context)
                        .setPassword("ten010ice009tabs")
                        .setActivatorModel(ActivatorModelEnum.TY_AP)
                        .setTimeOut(100)
                        .setToken(p0)
                        .setListener(mListener))

                mTuyaActivator.start()
            }

            override fun onFailure(p0: String?, p1: String?) {

                Log.e("MyTuyaDeviceRegist","ITuyaActivatorGetToken TOKEN creation  FAILED with error->"+p0.toString())


            }


        }

        //Log.e("MyTuyaDeviceRegist","ITuyaActivatorGetToken Registration start the process with the device->")




    }








    fun registerDevice(){


        val token=TuyaActivator.getInstance().getActivatorToken(activTokenListener)

        Log.e("MyTuyaDeviceRegist"," init TOKEN received with token->"+token.toString())
//开始配置

//停止配置

    }


}