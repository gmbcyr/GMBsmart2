package com.nenbeg.smart.model

import com.google.firebase.Timestamp

import java.io.Serializable
import java.util.*

/**
 * This is use to save all details informations when a device triggers an alarm
 */

class DeviceEventDetails() {

     var idDevice:String=""
     var timeEvent:Timestamp= Timestamp(Date())
     var typeEvt:String=""



    constructor(idDevice:String,timeEvent:Timestamp ,typeEvt:String):this(){

        this.idDevice=idDevice
        this.timeEvent=timeEvent
        this.typeEvt=typeEvt
    }







    companion object {
        private const val serialVersionUID = 20180617104400L
    }

}