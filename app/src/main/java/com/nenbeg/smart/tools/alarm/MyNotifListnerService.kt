package com.nenbeg.smart.tools.alarm

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.content.IntentFilter
import android.content.Intent
import android.service.notification.StatusBarNotification
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.tuya.smart.common.i
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.nenbeg.smart.MainActivity
import com.nenbeg.smart.tools.makecall.CallReceiver
import com.nenbeg.smart.tools.makecall.WakeMyScreenUp
import com.tuya.smart.common.bs


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class MyNotifListnerService : NotificationListenerService() {


    private val TAG = this.javaClass.simpleName
    private var nlservicereciver: NLServiceReceiver? = null


    /*override fun onCreate() {
        super.onCreate()
        nlservicereciver = NLServiceReceiver()
        val filter = IntentFilter()
        filter.addAction("com.google.firebase.MESSAGING_EVENT")
        registerReceiver(nlservicereciver, filter)
        //registerReceiver(nlservicereciver,null)
    }*/


    /*override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(nlservicereciver)
    }*/


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }


    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val pack=sbn.packageName

        Log.e(TAG, getExtraFromNotif(sbn))

        Log.e(TAG, "ID :" + sbn.id + "\t" + sbn.notification.tickerText + "\t" + pack)
        val i = Intent("com.google.firebase.MESSAGING_EVENT")
        i.putExtra("notification_event", "onNotificationPosted :" + sbn.packageName + "\n")

        if("com.nenbeg.smart".equals(pack)){

            setUpARingingView(10000,"nenbeg.critical","8132589562")

        }


        //sendBroadcast(i)

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getExtraFromNotif(sbn: StatusBarNotification):String{

        val pack=sbn.packageName

        return "**********MyNotifListnerService  onNotificationPosted_with the package->"+ pack+"_category->"+sbn.notification!!.category+"\n_and the extra->"+sbn.notification!!.extras

    }


    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "**********MyNotifListnerService onNOtificationRemoved")
        Log.i(TAG, "ID :" + sbn.id + "\t" + sbn.notification.tickerText + "\t" + sbn.packageName)
        val i = Intent("com.google.firebase.MESSAGING_EVENT")
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.packageName + "\n")

        //sendBroadcast(i)
    }


    private fun setUpARingingView(selectedTimeInMilliseconds: Long, name: String, number: String) {

        val waker = WakeMyScreenUp(application)
        waker.wakeup(10000)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, CallReceiver::class.java)

        intent.putExtra("NAME", name)
        intent.putExtra("NUMBER", number)

        val fakePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMilliseconds, fakePendingIntent)
        Toast.makeText(applicationContext, "Your call time has been set", Toast.LENGTH_SHORT).show()

        val intents = Intent(this, MainActivity::class.java)
        startActivity(intents)

    }


    internal inner class NLServiceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("command") == "clearall") {
                this@MyNotifListnerService.cancelAllNotifications()
            } else if (intent.getStringExtra("command") == "list") {
                val i1 = Intent("com.google.firebase.MESSAGING_EVENT")
                i1.putExtra("notification_event", "=====================")
                sendBroadcast(i1)
                var i = 1
                for (sbn in this@MyNotifListnerService.getActiveNotifications()) {
                    val i2 = Intent("com.google.firebase.MESSAGING_EVENT")
                    i2.putExtra("notification_event", i.toString() + " " + sbn.getPackageName() + "\n")
                    sendBroadcast(i2)
                    i++
                }
                val i3 = Intent("com.google.firebase.MESSAGING_EVENT")
                i3.putExtra("notification_event", "===== Notification List ====")
                sendBroadcast(i3)

            }

        }
    }
}