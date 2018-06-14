package com.nenbeg.smart.tools.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
        Log.d(TAG, "From: " + remoteMessage.getFrom());
       // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Map<String, String> data = remoteMessage.getData();

        try {

            if (data.size() > 0) {
                Log.e(TAG, "onMessageReceived Message data payload: " + data);

                // Send a notification that you got a new message
                //sendNotification(data);
                //insertSquawk(data);

                String typem=data.get(AllStaticKt.getBBM_TYPE_MSG());

                if(AllStaticKt.getBBM_MSG_UPDATE_ALARM().equalsIgnoreCase(typem)){


                    updateListAlarm(data);

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

                String typem=data.get(AllStaticKt.getBBM_TYPE_MSG());

                if(AllStaticKt.getBBM_MSG_UPDATE_ALARM().equalsIgnoreCase(typem)){


                    JSONObject jsObj = new JSONObject(data.get(AllStaticKt.getBBM_MSG_CONTENT()));

                    //JSONObject jsObj = new JSONObject(data.get("body"));

                    if(jsObj==null) return;




                    JSONArray alarmListNew=jsObj.getJSONArray(AllStaticKt.getBBM_MSG_CONTENT_LIST());

                    Log.e(TAG, "updateListAlarm Message data payload alarmList->: " + alarmListNew);

                    if(alarmListNew==null) return;

                    FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

                    String oldSetup=AllStaticKt.getPreferenceVal(getApplicationContext(),AllStaticKt.getBBM_MSG_PREF_NAME(),AllStaticKt.getBBM_UPDATE_ALARM_LIST_OLD());

                    JSONObject jsOld=null;

                    if(oldSetup!=null && !oldSetup.equalsIgnoreCase("")){

                        jsOld=new JSONObject(oldSetup);
                    }

                    if(jsOld!=null){

                        JSONArray alarmListOld=jsOld.getJSONArray(AllStaticKt.getBBM_MSG_CONTENT_LIST());


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


                AllStaticKt.setPreferenceVal(getApplicationContext(),AllStaticKt.getBBM_MSG_PREF_NAME(),AllStaticKt.getBBM_UPDATE_ALARM_LIST_OLD(),data.get(AllStaticKt.getBBM_MSG_CONTENT()));

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
}
