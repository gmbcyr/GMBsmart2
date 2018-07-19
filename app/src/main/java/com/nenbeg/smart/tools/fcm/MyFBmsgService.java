package com.nenbeg.smart.tools.fcm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nenbeg.smart.MainActivity;
import com.nenbeg.smart.R;
import com.nenbeg.smart.allstatic.AllStaticKt;
import com.nenbeg.smart.tools.alarm.MyJobService;
import com.nenbeg.smart.tools.makecall.CallReceiver;
import com.nenbeg.smart.tools.makecall.WakeMyScreenUp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by GMB on 11/11/2017.
 */

public class MyFBmsgService extends FirebaseMessagingService {
    private static final String TAG = "bbmFCMService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "MyFBmsgServic OnMessageReceived From: " + remoteMessage.getFrom());
       // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Map<String, String> data = remoteMessage.getData();

        try {

            if (data.size() > 0) {
                Log.e(TAG, "MyFBmsgServic OnMessageReceived  Message data payload: " + data+"_from ->"+remoteMessage.getFrom()+"_collapseKey ->"+remoteMessage.getCollapseKey()+"_from ->"+remoteMessage.toString());

                // Send a notification that you got a new message
                //sendNotification(data);
                //insertSquawk(data);

                String typem=data.get(AllStaticKt.getNENB_TYPE_MSG());

                if(AllStaticKt.getNENB_MSG_CRITICAL().equalsIgnoreCase(typem)){

                    //setUpARingingView(2000,getString(R.string.fakecallname),getString(R.string.fakecallnumber));

                }

            }
            //create notification
            //createNotification(remoteMessage.getNotification().getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void updateListAlarm(Map<String, String> data){

        try {

            if (data.size() > 0) {
                Log.e(TAG, "updateListAlarm Message data payload->: " + data);

                // Send a notification that you got a new message
                //sendNotification(data);
                //insertSquawk(data);

                String typem=data.get(AllStaticKt.getNENB_TYPE_MSG());








                if(AllStaticKt.getNENB_MSG_WARNING().equalsIgnoreCase(typem)){


                    JSONObject jsObj = new JSONObject(data.get(AllStaticKt.getNENB_MSG_CONTENT()));

                    //JSONObject jsObj = new JSONObject(data.get("body"));

                    if(jsObj==null) return;




                    JSONArray alarmListNew=jsObj.getJSONArray(AllStaticKt.getNENB_MSG_CONTENT_LIST());

                    Log.e(TAG, "updateListAlarm Message data payload alarmList->: " + alarmListNew);

                    if(alarmListNew==null) return;

                    FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

                    String oldSetup=AllStaticKt.getPreferenceVal(getApplicationContext(),AllStaticKt.getNENB_MSG_PREF_NAME(),AllStaticKt.getNENB_UPDATE_ALARM_LIST_OLD());

                    JSONObject jsOld=null;

                    if(oldSetup!=null && !oldSetup.equalsIgnoreCase("")){

                        jsOld=new JSONObject(oldSetup);
                    }

                    if(jsOld!=null){

                        JSONArray alarmListOld=jsOld.getJSONArray(AllStaticKt.getNENB_MSG_CONTENT_LIST());


                        int tail=alarmListOld.length();




                        for(int i=0;i<tail;i++){

                            JSONObject js=alarmListOld.getJSONObject(i);

                            Log.e(TAG, "updateListAlarm Message alarm UNSET for id->: "+js.getString("id"));

                            dispatcher.cancel(js.getString("id"));
                        }
                    }


                    int tail=alarmListNew.length();



                    for(int i=0;i<tail;i++){

                        JSONObject js=alarmListNew.getJSONObject(i);



                        long now = System.currentTimeMillis();
                        long dateStart = js.getLong("datestart");
                        //long dateStart=now+((i+3)*(1000*60));
                        long diff=(dateStart-now)/1000;
                        int windowStart = Math.round((dateStart-now)/1000);

                        Log.e(TAG, "updateListAlarm Message alarm param BEFORE set id->: "+js.getString("id")+"_nom->" + js.getString("nom")+"_and windowStart->"+windowStart+"_The Date Right now->"+new Date(now)+"_Alarm TO BE SET AT->"+new Date(dateStart)+"_Diff->"+diff);


                        if(windowStart<=0) continue;

                        Bundle bundle=new Bundle();
                        bundle.putString("msg",js.getString("nom"));
                        bundle.putString("js",js.toString());

                        Job synJob = dispatcher.newJobBuilder()
                                .setExtras(bundle)
                                .setService(MyJobService.class)
                                .setTag(js.getString("id"))
                                .setReplaceCurrent(true)
                                .setTrigger(Trigger.executionWindow(windowStart, windowStart + 10))
                                .setConstraints(Constraint.ON_ANY_NETWORK)
                                //.setConstraints(Constraint.DEVICE_CHARGING)
                                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                                .build();
                        dispatcher.mustSchedule(synJob);

                        Log.e(TAG, "updateListAlarm Message alarm set for id->: "+js.getString("id")+"_nom->" + js.getString("nom")+"_and windowStart->"+windowStart+"_Alarm set at->"+new Date(dateStart));
                    }


                }


                AllStaticKt.setPreferenceVal(getApplicationContext(),AllStaticKt.getNENB_MSG_PREF_NAME(),AllStaticKt.getNENB_UPDATE_ALARM_LIST_OLD(),data.get(AllStaticKt.getNENB_MSG_CONTENT()));

            }
            //create notification
            //createNotification(remoteMessage.getNotification().getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createNotification( String messageBody) {
        Intent intent = new Intent( this , MainActivity. class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("msg",messageBody);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                //.setSmallIcon(R.drawable.ic_menu_camera)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }



    public void setUpARingingView(long selectedTimeInMilliseconds, String name, String number){

        WakeMyScreenUp waker=new WakeMyScreenUp(getApplication());
        waker.wakeup(10000);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, CallReceiver.class);

        intent.putExtra("NAME", name);
        intent.putExtra("NUMBER", number);

        PendingIntent fakePendingIntent = PendingIntent.getBroadcast(this, 0,  intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMilliseconds, fakePendingIntent);
        Toast.makeText(getApplicationContext(), "Your call time has been set", Toast.LENGTH_SHORT).show();

        Intent intents = new Intent(this, MainActivity.class);
        startActivity(intents);

    }
}
