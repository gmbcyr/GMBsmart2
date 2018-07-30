package com.nenbeg.smart.model

import com.tuya.smart.sdk.bean.DeviceBean

class MyDeviceBean : DeviceBean() {

    var userOwnerEmail:String=""
    var userOwnerUid:String=""
    var location:String=""




    companion object {

        public val FIELD_NAME_USER_OWNER_EMAIL="userOwnerEmail"
        public val FIELD_NAME_USER_OWNER_UIDL="userOwnerUid"
        public val FIELD_NAME_LOCATION="location"
    }
}