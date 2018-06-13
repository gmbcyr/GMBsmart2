package com.nenbeg.smart.allstatic

import com.google.firebase.auth.FirebaseUser

public val DEFAULT_ID_ALARM = 20558
public val DB_NAME = "nbgdb"

public val PREF_FOR_DATA_PATH = "DB_PATH_PREF"
public val ROOT_USER_PATH = "rootUserDoc"
public val USER_EMAIL = "userEmail"
public val DEVICE_ID = "deviceID"
public val DEVICE_ID_CREATED = "dateDIcreated"

fun generateUserPwd(fbUser: FirebaseUser):String{

    var res=fbUser!!.uid

    if(fbUser!!.email!=null){

        res=res!!.substring(0,(res!!.length-(res!!.length/2)))+fbUser!!.email!!.substring(fbUser!!.email!!.indexOf("@")-1)
    }

    res=res+"Ne2$"

    if(fbUser!!.phoneNumber!=null){

        res=res+fbUser!!.phoneNumber!!.substring(0,2)
    }

    if(res!!.length<12){

        res=res+"4N+8B=G"
    }


    return res
}