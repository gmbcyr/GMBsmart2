package com.nenbeg.smart.tools.makecall;

import android.content.Context;
import android.os.PowerManager;

public class WakeMyScreenUp {

    Context context;


    public  WakeMyScreenUp(Context cont){

        context=cont;
    }

    public void wakeup(long stayOnTime){

        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn=pm.isScreenOn();

        if(!isScreenOn){

            PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE,"NENBEG:critical");
            wl.acquire(stayOnTime);

            PowerManager.WakeLock wl_cpu=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NENBEG:critical");

            wl_cpu.acquire(stayOnTime);

        }
    }
}
