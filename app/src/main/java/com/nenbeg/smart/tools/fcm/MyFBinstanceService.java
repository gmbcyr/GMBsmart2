package com.nenbeg.smart.tools.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nenbeg.smart.app.NenbegInstallation;
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GMB on 11/11/2017.
 */

public class MyFBinstanceService extends FirebaseInstanceIdService {

    private static final String TAG = "NBG_FB_ID_Service";

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        FirebaseInstanceId instanceId= FirebaseInstanceId.getInstance();
        String refreshedToken = instanceId.getToken();
        //Log the token
        String id=instanceId.getId();
        long registrationTIme=instanceId.getCreationTime();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        Map<String,String> json=new HashMap<>();
        try{


            NenbegInstallation bb=new NenbegInstallation();

            String idDD=bb.id(getApplicationContext());

            json.put("idtoken",id);
            json.put("token",refreshedToken);
            json.put("timeRegis",registrationTIme+"");
            json.put("dateRegisReadable",new Date(registrationTIme).toString());
            json.put("deviceID",idDD);
            json.put("deviceName",bb.getDeviceName());
            json.put("deviceModel",bb.getDeviceModel());
            json.put("dateCreated",System.currentTimeMillis()+"");
            json.put("dateCreatedReadable",new Date(System.currentTimeMillis())+"");

            sendRegistrationToServer(idDD,json);
        }
        catch (Exception ex){

            Log.e(TAG, "+++++++++++++++++Refreshed token Error: ");
            ex.printStackTrace();
        }


    }
    private void sendRegistrationToServer(String id,Object data) {
        //Implement this method if you want to store the token on your server
        Log.e(TAG, "+++++++++++++++++Refreshed token sendRegistrationToServer: ");

        MyFirestoreEditor editor=new MyFirestoreEditor(getApplicationContext());

        editor.saveInitParam(id,data,null,true,null);
    }
}
