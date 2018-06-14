package com.nenbeg.smart.tools.alarm



import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.nenbeg.smart.AccueilActivity
import com.nenbeg.smart.R
import com.nenbeg.smart.allstatic.DEFAULT_ID_ALARM
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor.CALLER_EVENT_EDIT_ONE
import org.json.JSONObject


class MyJobService : JobService() {
    override fun onStopJob(job: JobParameters?): Boolean {


        Log.e("MyJobService", "onStopJob Message alarm set for ->: ")

        createNotification(job!!.extras!!.getString("js"))

        return false; // Answers the question: "Is there still work going on?"
    }

    override fun onStartJob(job: JobParameters?): Boolean {


        Log.e("MyJobService", "onStartJob Message alarm set for ->"+job!!.extras!!.getString("js"))
        val js = JSONObject(job!!.extras!!.getString("js"))
        //createNotification(job!!.extras!!.getString("js"))
        sendNotification(js,124562659856,483489,843)
        return false; // Answers the question: "Should this job be retried?"
    }



    private fun createNotification(messageBody: String?) {

        Log.e("MyJobService", "createNotification Message alarm set for ->: " + messageBody)


        val js = JSONObject(messageBody)

        if (js==null) return

        val intent = Intent(this, AccueilActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("msg", messageBody)
        val resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.navigation_empty_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(js.getString("nom"))
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, mNotificationBuilder.build())
    }


    fun sendNotification(bundle: JSONObject?, idStat: Long, idRem: Long, idAlarm: Int) {


        var mBuilder = getCustomNotifBuilder(bundle!!.getString("nom")!!.toString(), getString(R.string.app_name_short) + "- " + bundle.getString("nom")!!.toString())


        //val notifSend = bbmApp2.getNbreAndLastIdSendInPref()

        ////Log.e("MyAlarmService","OnReceive, this is receive from MyDBinteraction->"+notifSend+"_and bundle->"+bundle);

        //val tok = StringTokenizer(notifSend, "#")




        //TODO : change AccueilActivity to a service setup in manifest and ready to handle the job.

        var intent = Intent(this, AccueilActivity::class.java)

        intent.putExtra("js",bundle.toString())

        intent.putExtra("isFromNotif", true)
        intent.putExtra("idReminder", idRem)
        //intent.action = bundle.getString("action")


        //Si la notif est de type automaticPay, on appelle direct la methode pour payer sans notifier le user
        if (1<2) {
            //if (bundle.getBoolean("isautopay")) {

            //intent.putExtra("callerEvent", MyAlarmService.CALLER_EVENT_DONE)
            intent.putExtra("idStat", idStat)
            intent.putExtra("idAlarm", idAlarm)
            intent.putExtra("updateOp", true)
            //intent.putExtra("newState", getString(R.string.current_state_done))
            //intent.putExtra("idNotif", rem!!.getIdnotif())
            intent.putExtra("isFromNotif", true)
            intent.putExtra("idReminder", idRem)
            intent.putExtra("callForActivity", false)
            intent.putExtra("whoAmi", "intent 1")
            //intent.action = bundle.getString("action")



            val fsEdit= MyFirestoreEditor(this)
            //fsEdit.manageAutoPay()
        } else {

            //Log.e("MyAlarmService","update alarm avec NOtifDejaSend->"+notifDejaSend);



            //Log.e("MyAlarmService","update alarm avec rem is notifMatch->"+rem.getIsnotifmatch());




            var pIntent: PendingIntent? = null

            intent = getIntentPrimary(intent, idStat, 123456, idRem, idAlarm, bundle)


            /*Intent intent2 = new Intent(this,MyBufferedReceiverForEdit.class);
            intent2.putExtras(bundle);
            intent2.putExtra("isFromNotif",true);
            intent2.putExtra("idStat",idStat);
            intent2.putExtra("idAlarm", idAlarm);
            intent2.putExtra("idReminder",idRem);
            intent2.putExtra("idNotif",rem.getIdnotif());
            intent2.putExtra("callForActivity",true);
            intent2.putExtra("actionInActivity","edit");
            intent2.putExtra("updateOp", false);
            intent2.putExtra("whoAmi","intent 2");

            intent2.setAction(bundle.getString("action"));
            intent2.putExtra("callerEvent", MyAlarmService.CALLER_EVENT_EDIT_ONE);*/

            val intent2 = Intent(this, AccueilActivity::class.java)
            intent2.putExtra("js",bundle.toString())
            //intent2.putExtra("callerEvent", getString(R.string.current_state_done))
            intent2.putExtra("idStat", idStat)
            intent2.putExtra("idAlarm", idAlarm)
            intent2.putExtra("updateOp", true)
           // intent2.putExtra("newState", getString(R.string.current_state_done))
            //intent2.putExtra("idNotif", rem!!.getIdnotif())
            intent2.putExtra("isFromNotif", true)
            intent2.putExtra("idReminder", idRem)
            intent2.putExtra("callForActivity", false)
            intent2.putExtra("whoAmi", "intent 1")
            //intent2.action = bundle.getString("action")


            val intent3 = Intent(this, AccueilActivity::class.java)

            intent3.putExtra("js",bundle.toString())
            //intent3.putExtra("callerEvent", getString(R.string.current_state_canceled))
            intent3.putExtra("idStat", idStat)
            intent3.putExtra("idAlarm", idAlarm)
            intent3.putExtra("updateOp", false)
            //intent3.putExtra("newState", getString(R.string.current_state_missed))

            intent3.putExtra("isFromNotif", true)
            intent3.putExtra("idReminder", idRem)
            //intent3.putExtra("idNotif", rem!!.getIdnotif())
            intent3.putExtra("callForActivity", false)
            intent3.putExtra("whoAmi", "intent 3")
            //intent3.action = bundle.getString("action")


            pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val pIntent2 = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
            val pIntent3 = PendingIntent.getBroadcast(this, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT)



            /*mBuilder.addAction(R.drawable.ic_cancel_white_24dp, getString(R.string.action_cancel), pIntent3).setAutoCancel(true)
            mBuilder.addAction(R.drawable.ic_edit_white_24dp, getString(R.string.action_edit), pIntent).setAutoCancel(true)
            mBuilder.addAction(R.drawable.ic_done_white_24dp, getString(R.string.action_confirm), pIntent2).setAutoCancel(true)

            */

            /*NotificationCompat.InboxStyle inboxStyle=new NotificationCompat.InboxStyle();



inboxStyle.setSummaryText(context.getString(R.string.notif_option));

mBuilder.setStyle(inboxStyle);*/

            //mBuilder.setContentText(this.getString(R.string.notif_option))
            mBuilder.setContentIntent(pIntent)

        }


        mBuilder.setAutoCancel(true)

        // Set the intent that will fire when the user taps the notification.
        //mBuilder.setContentIntent(pIntent);

        //mBuilder.setTicker(getText(R.string.notify_for_new_notif))


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Will display the notification in the notification bar
        //notificationManager.notify(""+idRem,Integer.parseInt("" + idAlarm), mBuilder.build());

        notificationManager.notify(DEFAULT_ID_ALARM, mBuilder.build())












    }


    fun getIntentPrimary(intent: Intent, idStat: Long, idNotif: Long, idRem: Long, idAlarm: Int, bundle: JSONObject): Intent {


        val intent2 = Intent(this, AccueilActivity::class.java)
        intent2.putExtra("js",bundle.toString())
        intent2.putExtra("isFromNotif", true)
        intent2.putExtra("idStat", idStat)
        intent2.putExtra("idAlarm", idAlarm)
        intent2.putExtra("idReminder", idRem)
        intent2.putExtra("idNotif", idNotif)
        intent2.putExtra("callForActivity", true)
        intent2.putExtra("actionInActivity", "edit")
        intent2.putExtra("updateOp", false)
        intent2.putExtra("whoAmi", "intent 2")

        //intent2.action = bundle.getString("action")
        intent2.putExtra("callerEvent", CALLER_EVENT_EDIT_ONE)

        /*intent.putExtra("callerEvent", MyAlarmService.CALLER_EVENT_DONE);
        intent.putExtra("idStat",idStat);
        intent.putExtra("idAlarm", idAlarm);
        intent.putExtra("updateOp", true);
        intent.putExtra("newState", getString(R.string.current_state_done));
        intent.putExtra("idNotif",idNotif);
        intent.putExtra("isFromNotif",true);
        intent.putExtra("idReminder", idRem);
        intent.putExtra("callForActivity",false);
        intent.putExtra("whoAmi","intent 1");
        intent.setAction(bundle.getString("action"));*/

        return intent2
    }


    private fun getCustomNotifBuilder(name: String, sms: String): NotificationCompat.Builder {


        var vibrate = true
        var soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        try {

            val res = NotificationCompat.PRIORITY_DEFAULT
            val pref = PreferenceManager.getDefaultSharedPreferences(application)

            val `val` = pref.getString("notifications_new_message_ringtone", "")

            vibrate = pref.getBoolean("notifications_new_message_vibrate", true)

            Log.e("MyAlarmService", " getCustomNotifBuilder uri alarm->" + `val`!!)



            if (!`val`.equals("", ignoreCase = true)) {

                try {
                    soundUri = Uri.parse(`val`)
                } catch (e: Exception) {

                }

            }
        } catch (ex: Exception) {

        }

        //Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(stringValue));


        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder = NotificationCompat.Builder(this)

        mBuilder.setContentTitle(name)
        mBuilder.setContentText(sms)
        //mBuilder.setSmallIcon(R.drawable.logo_notif)
        if (vibrate) mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        mBuilder.setSound(soundUri)
        mBuilder.priority = getNotificationPriority()


        return mBuilder
    }


    fun getNotificationPriority(): Int {

        var res = NotificationCompat.PRIORITY_DEFAULT
        val pref = PreferenceManager.getDefaultSharedPreferences(application)

        val `val` = pref.getString("list_priorite_notif", "1")

        if (`val`!!.equals("4", ignoreCase = true)) {

            res = NotificationCompat.PRIORITY_MAX
        } else if (`val`.equals("3", ignoreCase = true)) {


            res = NotificationCompat.PRIORITY_HIGH
        } else if (`val`.equals("2", ignoreCase = true)) {

            res = NotificationCompat.PRIORITY_DEFAULT
        } else if (`val`.equals("1", ignoreCase = true)) {


            res = NotificationCompat.PRIORITY_LOW
        }


        return res
    }

}