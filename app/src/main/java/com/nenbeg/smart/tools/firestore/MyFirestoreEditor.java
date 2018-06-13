package com.nenbeg.smart.tools.firestore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.gmb.bbm2.tools.firestore.onActionDone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.nenbeg.smart.R;
import com.nenbeg.smart.allstatic.AllStaticKt;
import com.nenbeg.smart.app.NenbegApp;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by GMB on 11/15/2017.
 */

public class MyFirestoreEditor {

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    public static final int CALLER_EVENT_REBOOT=0;
    public static final int CALLER_EVENT_NEW=1;
    public static final int CALLER_EVENT_UPDATE=2;
    public static final int CALLER_EVENT_DONE=3;
    public static final int CALLER_EVENT_CANCELED=4;
    public static final int CALLER_EVENT_MISSED=5;
    public static final int CALLER_EVENT_DELETE=6;
    public static final int CALLER_EVENT_DELETE_ALL=7;
    public static final int CALLER_EVENT_REFILL=8;
    public static final int CALLER_EVENT_CANCEL_ALL=9;
    public static final int CALLER_EVENT_NOTIF_ON_TIME=11;
    public static final int CALLER_EVENT_LIST=12;
    public static final int CALLER_EVENT_EDIT_ONE=13;
    public static final int CALLER_EVENT_EXPORT_DB=14;
    public static final int CALLER_EVENT_IMPORT_DB=15;

    public static final int LIMIT_NOTIFICATONS_TO_HANDLE=10;
    public static final long AN_HOUR_TIME=60*60*1000;

    private static final String TAG = "MyFirestoreEditor";

    public static String COLLECTION_CATEGORY = "Categories";
    public static String COLLECTION_STATEMENT = "BBMstatements";
    public static String COLLECTION_ONTIMENOTIF = "OntimeNotifs";
    public static String COLLECTION_REMINDER = "Reminders";
    public static String COLLECTION_MY_DEVICES="MyDevices";
    public static String COLLECTION_MY_USERS="my-users";
    public static String COLLECTION_MY_USERS_DEVICES="my-users-devices";
    public static String COLLECTION_BUDGET = "Budgets";
    public static String COLLECTION_MY_SETTINGS = "MySettings";

    Context context;
    String dbUserId = "";
    String deviceID="";
    FirebaseFirestore db;
    GregorianCalendar cal;
    ArrayList<DocumentSnapshot> mArrayList;
    NenbegApp bbmApp2;


    public MyFirestoreEditor(Context context) {
        this.context = context;

        bbmApp2=  new NenbegApp ().getInstance();

        dbUserId = getUserID() /*+ new BBMinstallation().id(context)*/;

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        mArrayList = new ArrayList<>();

    }


    protected String getID(String collectionName) {

        String basicRef = getBasicRef();
        CollectionReference collectionRef = db.collection(basicRef + collectionName);

        String id = collectionRef.getId();

        return id;
    }


    public CollectionReference getCollectionRef(String collectionName) {

        String basicRef = getBasicRef();
        CollectionReference collectionRef = db.collection(basicRef + collectionName);

        return collectionRef;
    }


    public Query getQueryFromCollection(String collectionName, int LIMIT) {

        String basicRef = getBasicRef();
        Log.e("MyFirEditor","this is query text before->"+basicRef+collectionName);
        Query q = db.collection(basicRef + collectionName)
                //.orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);

        Log.e("MyFirEditor","this is query text->"+q.toString());
        return q;


    }


    public Query getQueryForNotifParm(String collectionName,String idCat,String idBbm,Long dat1,
                                      long dat2,String status,String sens,boolean paidMatters,
                                      boolean paid,String sortBy,String sortType,int LIMIT) {

        String basicRef = getBasicRef();
        Log.e("MyFirEditor","this is query text before->"+basicRef+collectionName);
        Query q = db.collection(basicRef + collectionName)
                //.orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);


       /* if(dat1>0){

            q=q.whereGreaterThanOrEqualTo(AllStaticKt.getCOL_NAME_TIMESTART(),dat1);

        }

        if(dat2>0){

            q=q.whereLessThanOrEqualTo(AllStaticKt.getCOL_NAME_TIMESTART(),dat2);
        }

        if(AllStaticKt.isNotEmpty(idCat)){

            q=q.whereEqualTo(AllStaticKt.getCOL_NAME_ID_CAT(),idCat);
        }

        if(AllStaticKt.isNotEmpty(idBbm)){

            q=q.whereEqualTo(AllStaticKt.getCOL_NAME_ID_STATE(),idBbm);
        }

        if(AllStaticKt.isNotEmpty(status)){

            q=q.whereEqualTo(AllStaticKt.getCOL_NAME_CURRENT_STATE(),status);
        }

        if(AllStaticKt.isNotEmpty(sens)){

            q=q.whereEqualTo(AllStaticKt.getCOL_NAME_SENS(),sens);
        }

        if(paidMatters){

            q=q.whereEqualTo(AllStaticKt.getCOL_NAME_PAID_OP(),paid);
        }*/

        Log.e("MyFirEditor","this is query text->"+q.toString());
        return q;


    }





    public Task<QuerySnapshot> getListNotifWithParam(String collectionName,String idCat,String idBbm,Long dat1,
                                                     long dat2,String status,String sens,boolean paidMatters,
                                                     boolean paid,String sortBy,String sortType,int LIMIT, final onActionDone listner) {
        //Log.e("getListItems","getListItems this is cat list from firestore->"+colRef);
        mArrayList=new ArrayList<>();
        Query q =getQueryForNotifParm(collectionName,idCat,idBbm,dat1,dat2,status,sens,paidMatters,paid,sortBy,sortType,LIMIT);

        return q.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "getListItems onSuccess: LIST EMPTY");
                            return;
                        } else {

                            Log.d(TAG, "getListItems onSuccess: " + documentSnapshots.size());
                            if(listner!=null) listner.onListDone(documentSnapshots);
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "getListItems Error getting data!!!", Toast.LENGTH_LONG).show();
                        if(listner!=null) listner.onListDone(null);
                    }
                });

    }

    public String getBasicRef() {

        return AllStaticKt.getDB_NAME() + "/" + dbUserId + "/";
    }

    public CollectionReference getUserCollectionRef() {


        CollectionReference collectionRef = db.collection(COLLECTION_MY_USERS);

        return collectionRef;
    }


    public CollectionReference getUserDevicesCollectionRef() {


        CollectionReference collectionRef = db.collection(COLLECTION_MY_USERS_DEVICES);

        return collectionRef;
    }

    protected String addData(String collectionName, Object data, String id) {

        String basicRef = getBasicRef();
        CollectionReference collectionRef = db.collection(basicRef + collectionName);

        if (id == null || id.isEmpty()) id = getID(collectionName);

        collectionRef.document(id).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        return id;
    }

    protected String addDataInBatch(WriteBatch batch, String collectionName, Object data, String id) {

        try {


            DocumentReference ref = getCollectionRef(collectionName).document();

            if (id == null || id.isEmpty()) id = ref.getId();


            // Add data
            batch.set(ref, data);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return id;
    }

    @SuppressLint("MissingPermission")
    private String getDeviceID() {

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }

    private String getUserID() {

        //Save credentials to pref
        SharedPreferences pref=context.getSharedPreferences(AllStaticKt.getPREF_FOR_DATA_PATH(), Context.MODE_PRIVATE);

        deviceID=pref.getString(AllStaticKt.getDEVICE_ID(),"anonymous");

        return pref.getString(AllStaticKt.getROOT_USER_PATH(),"anonymous");
    }


    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }



    public Task<Void> addRecord(final DocumentReference docRef, final Object object, OnCompleteListener listener) {


        try {

            WriteBatch batch = getDb().batch();


            // Add Record
            batch.set(docRef, object);


            return batch.commit().addOnCompleteListener(listener);


            ////Log.i("MyAlarmService", "MyAlarmService transactionInsertStatementNotif  saveState pos 4_nbre Notif simpe->"+nbreSansJoin+"_Notif avecJoin->"+listBuf.size());
        } catch (Exception e) {
            Log.e("MyFirestoreEditor", "addRecord Error on add record");
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }



    public Task<Void> saveInitParam(String id,Object data, OnCompleteListener listener,boolean newInit,Map<String ,Object> dataToUpdate) {


        try {


                DocumentReference ref=getUserDevicesCollectionRef().document(id);



            WriteBatch batch = getDb().batch();



            // Add Record
            if(newInit){

                batch.set(ref, data);
            }
            else{

                batch.update(ref, dataToUpdate);
            }


            /*DocumentReference refDD=ref.collection(COLLECTION_MY_DEVICES).document(idD);

            batch.set(refDD,dataD);*/

            Log.e("MyAlarmService", "MyAlarmService saveInitParam  saveState pos 4_nbre Notif simpe->_Notif avecJoin->");

            if(listener!=null){
                return batch.commit().addOnCompleteListener(listener);
            }
            else{
                return batch.commit();
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }

    public Task<Void> updateData(final DocumentReference docRef, Map<String,Object> updates, OnCompleteListener listener) {


        try {

            WriteBatch batch = getDb().batch();

            // Add Record
            batch.update(docRef, updates);


            return batch.commit().addOnCompleteListener(listener);


            ////Log.i("MyAlarmService", "MyAlarmService transactionInsertStatementNotif  saveState pos 4_nbre Notif simpe->"+nbreSansJoin+"_Notif avecJoin->"+listBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }



    public void payNotif(DeviceBean notif){

        if(notif==null) return;

        Map<String,Object> values=new HashMap<>();
        values.put("isOnline",true);
        values.put("name",notif.name);

        DocumentReference docRef=getCollectionRef(COLLECTION_ONTIMENOTIF).document(notif.devId);

        OnCompleteListener listener=new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isComplete()){

                    if(task.isSuccessful()){

                        Toast.makeText(context, context.getString(R.string.op_completed), Toast.LENGTH_LONG).show();
                    }
                    else{

                        Toast.makeText(context, context.getString(R.string.op_failed), Toast.LENGTH_LONG).show();

                    }
                }
            }
        };


        updateData(docRef,values,listener);
    }





    public Task<Void> updateObject(final DocumentReference docRef, Object object, OnCompleteListener listener) {


        try {

            WriteBatch batch = getDb().batch();

            // Add Record
            batch.set(docRef, object);


            return batch.commit().addOnCompleteListener(listener);


            ////Log.i("MyAlarmService", "MyAlarmService transactionInsertStatementNotif  saveState pos 4_nbre Notif simpe->"+nbreSansJoin+"_Notif avecJoin->"+listBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }

    public Task<Void> deleteRecord(final DocumentReference docRef, OnCompleteListener listener) {


        try {

            WriteBatch batch = getDb().batch();


            // delete Record
            batch.delete(docRef);


            return batch.commit().addOnCompleteListener(listener);


            ////Log.i("MyAlarmService", "MyAlarmService transactionInsertStatementNotif  saveState pos 4_nbre Notif simpe->"+nbreSansJoin+"_Notif avecJoin->"+listBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }

    public Task<Void> deleteAllRecord(String collectionRef,ArrayList<DocumentSnapshot> list, OnCompleteListener listener) {


        try {

            //Log.e("MyFirestoreEditor", "deleteAllRecord this is pos 1->");


            if(list!=null){

                //Log.e("MyFirestoreEditor", "deleteAllRecord this is pos 2 and list->"+list);

                WriteBatch batch = getDb().batch();

                for(DocumentSnapshot snap:list){

                    Log.e("MyFirestoreEditor", "deleteAllRecord this is pos 2 and eltID->"+snap.getId());
                    //DocumentReference ref=snap.getReference();
                    DocumentReference ref=getCollectionRef(collectionRef).document(snap.getId());

                    // delete Record
                    batch.delete(ref);
                }

                if(listener!=null){

                    return batch.commit().addOnCompleteListener(listener);
                }
                else{
                    return batch.commit();
                }
            }




            ////Log.i("MyAlarmService", "MyAlarmService transactionInsertStatementNotif  saveState pos 4_nbre Notif simpe->"+nbreSansJoin+"_Notif avecJoin->"+listBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //daoSession.getDatabase().endTransaction();
        }

        return null;
    }






    public void deleteDevice(DeviceBean notif){

        if(notif==null) return;



        DocumentReference docRef=getCollectionRef(COLLECTION_ONTIMENOTIF).document(notif.devId);

        OnCompleteListener listener=new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isComplete()){

                    if(task.isSuccessful()){

                        Toast.makeText(context, context.getString(R.string.op_completed), Toast.LENGTH_LONG).show();
                    }
                    else{

                        Toast.makeText(context, context.getString(R.string.op_failed), Toast.LENGTH_LONG).show();

                    }
                }
            }
        };



        deleteRecord(docRef,listener);
    }

    public Task<QuerySnapshot> getListItems(String colRef, final onActionDone listner) {
        //Log.e("getListItems","getListItems this is cat list from firestore->"+colRef);
        mArrayList=new ArrayList<>();
        return db.collection(getBasicRef()+colRef).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "getListItems onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            // List<Type> types = documentSnapshots.toObjects(Type.class);

                            // Add all to your list
                           // mArrayList.addAll(mArrayList);
                            Log.d(TAG, "getListItems onSuccess: " + documentSnapshots.size());
                            if(listner!=null) listner.onListDone(documentSnapshots);
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "getListItems Error getting data!!!", Toast.LENGTH_LONG).show();
                        if(listner!=null) listner.onListDone(null);
                    }
                });

    }




    public Task<QuerySnapshot> getListItems(Query q, final onActionDone listner) {
        //Log.e("getListItems","getListItems this is cat list from firestore->"+colRef);
        mArrayList=new ArrayList<>();
        return q.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "getListItems onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            // List<Type> types = documentSnapshots.toObjects(Type.class);

                            // Add all to your list
                            // mArrayList.addAll(mArrayList);
                            Log.d(TAG, "getListItems onSuccess: " + documentSnapshots.size());
                            if(listner!=null) listner.onListDone(documentSnapshots);
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "getListItems Error getting data!!!", Toast.LENGTH_LONG).show();
                        if(listner!=null) listner.onListDone(null);
                    }
                });

    }



    public void deleteOldNotif(String collectionName,String idCat,String idBbm,Long dat1,
                               long dat2,String status,String sens,boolean paidMatters,
                               boolean paid){

        onActionDone listner=new onActionDone() {
            @Override
            public void onListDone(@Nullable QuerySnapshot list) {

                if(list!=null){

                    deleteAllRecord(COLLECTION_ONTIMENOTIF,(ArrayList<DocumentSnapshot>) list.getDocuments(),null);
                }
            }
        };

        getListItems(getQueryForNotifParm(collectionName,idCat,idBbm,dat1,dat2,status,sens,paidMatters,paid,"","",500),listner);
    }






    public void cancelNotification(){

        try{

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Will display the notification in the notification bar
            notificationManager.cancelAll();
        }
        catch (Exception e){

            e.printStackTrace();
        }
    }


    public  int getIdAlarmForADay(String idStat){


        int res=9999999;

        /*MySettings myS=null;//=getAlarmForADay(idStat);

        if(myS!=null){

            res= Integer.parseInt(myS.getVal());
        }*/

        //Log.e("MyBDinteraction","getAlarmForAday voici nbre retour->"+res);


        return res;
    }



}
