package com.nenbeg.smart.tools.makecall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CallReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        val getFakeName = intent?.getStringExtra("FAKENAME")
        val getFakePhoneNumber = intent?.getStringExtra("FAKENUMBER")

        val intentObject = Intent(context?.getApplicationContext(), RingingActivity::class.java)
        intentObject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intentObject.putExtra("myfakename", getFakeName)
        intentObject.putExtra("myfakenumber", getFakePhoneNumber)
        context?.startActivity(intentObject)
    }


}