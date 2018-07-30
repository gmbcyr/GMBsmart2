package com.nenbeg.smart.allstatic

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.Nullable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.nenbeg.smart.app.NenbegInstallation
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.lang.StringBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

public val DEFAULT_ID_ALARM = 20558
public val DB_NAME = "nbgsmartdb"

public val PREF_FOR_DATA_PATH = "DB_PATH_PREF"
val ALL_DEVICES_PREF_FILE_NAME="all-devices-pref"
val ALL_DEVICES_PREF_DATA_JSON="all-devices-pref-data-json"
val ALL_DEVICES_PREF_LIST_JSON="all-devices-pref-list-json"
public val ROOT_USER_PATH = "rootUserDoc"
public val USER_EMAIL = "userEmail"
public val DEVICE_ID = "deviceID"
public val DEVICE_ID_CREATED = "dateDIcreated"


public val DEVICE_ID_SHARE = "deviceIDShare"
public val DEVICE_ID_SHARE_CREATED = "dateDIshareCreated"

public val DB_NAME_EXTENSION = ".db"
public val DB_NAME_EXPORTED = "bbmDBexported"

public val MY_CONFIG_FILE = "bbmconffile.txt"


public val _MODE = 0
public val PREF_NAME = "ReminderPro"
public val IS_FIRSTTIME = "IsFirstTime"
public val PREF_SHOW_PUB = "showPub"
public val PREF_PUB_PERCENT = "pubPercent"
public val PREF_PUB_PERCENT_CORRECTION = "pubPercentCorrection"
public val PREF_DB_VERSION = "dbVersion"
public val PREF_DB_NEEDS_UPDATE = "dbNeedsUpdate"
public val userName = "user_name"
public val NBRE_NOTIF_HANDLE = "NBRE_NOTIF_HANDLE"
public val DATE_LAST_NOTIF_HANDLE = "DATE_LAST_NOTIF_HANDLE"
public val NBRE_NOTIF_SEND = "NBRE_NOTIF_SEND"
public val ID_NOTIF_SEND = "ID_NOTIF_SEND"
public val MIN_NOTIF_HANDLE = 4
public var nbreCurrentNotifHeld = 0
public var dateLastNotif = System.currentTimeMillis()

public var nbreCurrentNotifSend = 0
public var idNotifSend: Long = 0

public val notifSendLock = Any()



/*************************************************Some DEVICE PREF FIELDS***********************************************************/

val DEVICE_PREF_NOTIF_ON_ALARM="notif-on-alarm"
val DEVICE_PREF_NOTIF_ON_BATTERY="notif-on-battery"
val DEVICE_PREF_NOTIF_IS_CRITICAL="is-critical"

val DEVICE_PREF_NAME="name"
val DEVICE_PREF_CATEGORY="category"
val DEVICE_PREF_GROUP="group"
val DEVICE_PREF_LOCATION="location"


fun ClosedRange<Int>.myRandomInt() =
        Random().nextInt((endInclusive + 1) - start) +  start

/*******************************************************************Static field for FCM exchance********************************************************/
val NENB_TYPE_MSG="nenbTypeMessage"
val NENB_MSG_INFOS="nenb-msg-infos"
val NENB_MSG_WARNING="nenb-msg-warning"
val NENB_MSG_CRITICAL="nenb-msg-critical"
val NENB_MSG_CONTENT="nenb-msg-content"
val NENB_MSG_CONTENT_LIST="nenb-msg-content-list"
val NENB_MSG_PREF_NAME="nenb-msg-pref-name"
val NENB_UPDATE_ALARM_LIST_NEW="nenb-update-alarm-list-new"
val NENB_UPDATE_ALARM_LIST_OLD="nenb-update-alarm-list-old"


private var myFbEditor: MyFirestoreEditor?=null

/********************************************IMPORTANT METHODS***********/

public   fun getDateAtCurrentTime(date: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = System.currentTimeMillis()
    val h = cal.get(Calendar.HOUR_OF_DAY)
    val m = cal.get(Calendar.MINUTE)
    cal.timeInMillis = date
    cal.set(Calendar.HOUR_OF_DAY, h)
    cal.set(Calendar.MINUTE, m)

    return cal.timeInMillis
}



public fun getFireStoreEditor(context: Context): MyFirestoreEditor {

    if(myFbEditor==null){

        myFbEditor= MyFirestoreEditor(context)
    }

    return myFbEditor as MyFirestoreEditor
}



public fun getEmailAsKey(email:String):String{

    var emailKey = email!!.replace("@", "#$#")
    emailKey = emailKey.replace(".", "$#$")

    return emailKey
}


public fun getEmailFromKey(emailAsKey:String):String{

    var email = emailAsKey!!.replace( "#$#","@")
    email = email.replace( "$#$",".")

    return email
}








@TargetApi(Build.VERSION_CODES.KITKAT)
fun getLocalCurrency(context: Context): Currency {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    val numFor = NumberFormat.getCurrencyInstance(Locale.US)
    var cur = numFor.currency

    try {

        val defCurCode = pref.getString("lst_default_currency", numFor.currency.currencyCode)

        //Log.e("MyBBmApp","getLocalCurrency this is defautCode->"+defCurCode);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            val curs = Currency.getAvailableCurrencies()

            val it = curs.iterator()

            while (it.hasNext()) {
                val c = it.next()
                if (c.currencyCode.equals(defCurCode!!, ignoreCase = true)) {
                    cur = c
                    break
                }
            }
        } else {

            cur = Currency.getInstance(defCurCode)
        }


    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return cur

}






fun isClickMenuContextuel(context: Context): Boolean {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return pref.getBoolean("swt_mouse_bouton", true)
}


fun compareDate(date1: Long, date2: Long, withoutTime: Boolean): Int {


    var res: Int = 0
    if (withoutTime) {

        val cal = GregorianCalendar()
        cal.timeInMillis = date1
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val d1 = cal.time

        cal.timeInMillis = date2
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val d2 = cal.time

        res = d1.compareTo(d2)

    } else {

        val d1 = Date(date1)
        val d2 = Date(date2)
        res = d1.compareTo(d2)
    }



    return res
}


fun isOverdueAllowed(context: Context): Boolean {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)

    return pref.getBoolean("swt_overdue_allowed", false)

}


fun isBlockOverBudgetActivated(context: Context): Boolean {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)

    return pref.getBoolean("swt_over_budget_blocked", true)

}


/***************************************************************UI Usual functions**************************************************************/

fun hideSoftInput(context: Context, view: View?, showKeyboard: Boolean) {

    if (showKeyboard) {

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    } else {

        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}


/***************************************************************All Date Functions********************************************************/
fun getLastDayMonth(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getLastDayMonth voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curMont = cal.get(Calendar.MONTH)
    var lastDay = cal.get(Calendar.DAY_OF_MONTH)

    while (cal.get(Calendar.MONTH) == curMont) {

        lastDay = cal.get(Calendar.DAY_OF_MONTH)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }

    //Remise au mois dont on faisait la boucle car a la fin, on a change de mois et pour certains cas l annee
    cal.add(Calendar.DAY_OF_MONTH, -1)

    //cal.set(Calendar.MONTH,curMont);
    cal.set(Calendar.DAY_OF_MONTH, lastDay)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)

    //Log.e("MyBbmAppli", "getLastDayMonth voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis

}


fun getLastHourDay(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getLastDayMonth voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour

    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)


    return cal.timeInMillis

}

fun getFirstHourDay(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getLastDayMonth voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)

    return cal.timeInMillis

}


fun getNumberOfDaysInMonth(dateJour: Long): Int {

    getFirstDayMonth(dateJour)

    val cal = GregorianCalendar()

    cal.timeInMillis = dateJour

    cal.set(Calendar.DAY_OF_MONTH, 1)
    ////Log.e("CustomCalendar", "getNumberOfDaysOfMonth first day du mois->" + cal.get(Calendar.DAY_OF_MONTH));
    cal.add(Calendar.MONTH, 1)
    cal.add(Calendar.DAY_OF_MONTH, -1)

    ////Log.e("CustomCalendar", "getNumberOfDaysOfMonth last day du mois->" + cal.get(Calendar.DAY_OF_MONTH));
    return cal.get(Calendar.DAY_OF_MONTH)
}


fun getFirstDayMonth(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getFirstDayMonth voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curMont = cal.get(Calendar.MONTH)
    var firstDay = cal.get(Calendar.DAY_OF_MONTH)

    while (cal.get(Calendar.MONTH) == curMont) {

        firstDay = cal.get(Calendar.DAY_OF_MONTH)
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }

    //Remise au mois dont on faisait la boucle car a la fin, on a change de mois et pour certains cas l annee
    cal.add(Calendar.DAY_OF_MONTH, 1)

    //cal.set(Calendar.MONTH,curMont);
    cal.set(Calendar.DAY_OF_MONTH, firstDay)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)

    //Log.e("MyBbmAppli", "getFirstDayMonth voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis
}


fun getFirstDayWeek(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getFirstDayWeek voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curWeek = cal.get(Calendar.WEEK_OF_YEAR)

    while (cal.get(Calendar.WEEK_OF_YEAR) == curWeek) {

        cal.add(Calendar.DAY_OF_MONTH, -1)
    }

    //Remise au week dont on faisait la boucle car a la fin, on a change de week,mois out annee
    cal.add(Calendar.DAY_OF_MONTH, 1)


    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)

    //Log.e("MyBbmAppli", "getFirstDayWeek voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis
}

fun getLastDayWeek(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getLastDayWeek voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curWeek = cal.get(Calendar.WEEK_OF_YEAR)

    while (cal.get(Calendar.WEEK_OF_YEAR) == curWeek) {

        cal.add(Calendar.DAY_OF_MONTH, 1)
    }

    //Remise au week dont on faisait la boucle car a la fin, on a change de week,mois out annee
    cal.add(Calendar.DAY_OF_MONTH, -1)


    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)

    //Log.e("MyBbmAppli", "getLastDayWeek voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis
}

fun getFirstDayWeekOfMonth(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getFirstDayWeek voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curWeek = cal.get(Calendar.WEEK_OF_MONTH)

    while (cal.get(Calendar.WEEK_OF_MONTH) == curWeek) {

        cal.add(Calendar.DAY_OF_MONTH, -1)
    }

    //Remise au week dont on faisait la boucle car a la fin, on a change de week,mois out annee
    cal.add(Calendar.DAY_OF_MONTH, 1)


    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)

    //Log.e("MyBbmAppli", "getFirstDayWeek voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis
}

fun getLastDayWeekOfMonth(datCour: Long): Long {

    //Log.e("MyBbmAppli", "getLastDayWeek voici ce que je recois->" + new Date(datCour));

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    val curWeek = cal.get(Calendar.WEEK_OF_MONTH)

    while (cal.get(Calendar.WEEK_OF_MONTH) == curWeek) {

        cal.add(Calendar.DAY_OF_MONTH, 1)
    }

    //Remise au week dont on faisait la boucle car a la fin, on a change de week,mois out annee
    cal.add(Calendar.DAY_OF_MONTH, -1)


    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)

    //Log.e("MyBbmAppli", "getLastDayWeek voici la date  de retour->" + new Date(cal.getTimeInMillis()));

    return cal.timeInMillis
}

fun getFirstDayPreviousMonth(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.MONTH, -1)

    // Log.e("MyBbmAppli","getFirstDayLastMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));

    return getFirstDayMonth(cal.timeInMillis)

}

fun getLastDayPreviousMonth(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.MONTH, -1)

    //Log.e("MyBbmAppli","getLastDayLastMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getLastDayMonth(cal.timeInMillis)

}


fun getFirstDayNextMonth(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.MONTH, 1)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getFirstDayMonth(cal.timeInMillis)

}

fun getFirstDayNextYear(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.YEAR, 1)
    cal.set(Calendar.MONTH, Calendar.JANUARY)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getFirstDayMonth(cal.timeInMillis)

}

fun getLastDayNextMonth(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.MONTH, 1)

    //Log.e("MyBbmAppli","getLastDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getLastDayMonth(cal.timeInMillis)

}


fun getFirstDayPreviousYear(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.YEAR, -1)
    cal.set(Calendar.MONTH, Calendar.JANUARY)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getFirstDayMonth(cal.timeInMillis)

}


fun getLastDayPreviousYear(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.add(Calendar.YEAR, -1)
    cal.set(Calendar.MONTH, Calendar.DECEMBER)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getLastDayMonth(cal.timeInMillis)

}


fun getFirstDayYear(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.set(Calendar.MONTH, Calendar.JANUARY)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getFirstDayMonth(cal.timeInMillis)

}


fun getLastDayYear(datCour: Long): Long {

    val cal = GregorianCalendar()
    cal.timeInMillis = datCour
    cal.set(Calendar.MONTH, Calendar.DECEMBER)

    //Log.e("MyBbmAppli","getFirstDayNextMonth voici la date a traiter->"+new Date(cal.getTimeInMillis()));
    return getLastDayMonth(cal.timeInMillis)

}


fun getMonthShortAsString(dateJour: Long): String {

    val daFor = SimpleDateFormat("MMM.")

    return daFor.format(Date(dateJour))
}


fun getMonthAsString(dateJour: Long): String {

    val daFor = SimpleDateFormat("MMMM")

    return daFor.format(Date(dateJour))
}

fun getMonthAndYearAsString(dateJour: Long): String {

    val daFor = SimpleDateFormat("MMMM yyyy")

    return daFor.format(Date(dateJour))
}

fun getWeekDayAsString(dateJour: Long): String {
    val daFor = SimpleDateFormat("EEEEE")

    return daFor.format(Date(dateJour))
}

fun getWeekDayShortAsString(dateJour: Long): String {
    val daFor = SimpleDateFormat("EEE")

    return daFor.format(Date(dateJour))
}

fun getWeekDayOneLetterAsString(dateJour: Long): String {
    val daFor = SimpleDateFormat("E")

    return daFor.format(Date(dateJour))
}





fun isSundayFirstDayWeek(context: Context): Boolean {

    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    var res = true

    try {

        res = pref.getBoolean("swt_sunday_first_day", true)
    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return res

}




fun getAmountMinCat(_context: Context): Double {

    val pref = PreferenceManager.getDefaultSharedPreferences(_context)

    var min = 10.00

    try {
        min = java.lang.Double.parseDouble(pref.getString("txt_default_cat_min_amount", "10.00"))
    } catch (ex: Exception) {

    }

    return min
}

fun isNotEmpty(test:String):Boolean{

    var res:Boolean=true

    if(test==null) return false

    if("".equals(test,true)) return false

    return  res

}


fun setPreferenceVal(context: Context, @Nullable prefName:String, prefKey:String, prefVal:String):Boolean {

    var pref: SharedPreferences?=null

    if(prefName==null){

        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }
    else{


        pref = context.getSharedPreferences(prefName, PRIVATE_MODE)
    }

    val editor = pref.edit()



    try {

        editor.putString(prefKey,prefVal)
        editor.commit()

        return true


    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return false

}


fun getPreferenceVal(context: Context, @Nullable prefName:String, prefKey:String):String {

    var pref: SharedPreferences?=null

    if(prefName==null){

        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }
    else{


        pref = context.getSharedPreferences(prefName, PRIVATE_MODE)
    }



    try {



        return pref!!.getString(prefKey,"")


    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return ""

}

val PRIVATE_MODE: Int=0

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






fun fetchUserString(userId: String) = async {
    // request user from network
    // return user String
    "val retour"
}

fun deserializeUser(userString: String) = async {
    // deserialize
    // return User
    "User "
}

val starting= launch(UI) {

    //progressBar.visibility = View.VISIBLE
    try {
        val userString = fetchUserString("1").await()
        val user = deserializeUser(userString).await()
        //showUserData(user)
    } catch (ex: Exception) {
        //log(ex)
    } finally {
        // progressBar.visibility = View.GONE
    }

}



/********************************SAVE NEW CREDENTIALS AFTER FIREBASE AUTHENTIFICATION*************************************************/


private fun saveNewCredentials(context: Context,acct: GoogleSignInAccount) {

    val json = HashMap<String, Any>()
    try {

        val bb = NenbegInstallation()
        val idDD = bb.id(context)
        val emailKey = getEmailAsKey(acct.email!!)



        json[ROOT_USER_PATH] = emailKey
        //json[USER_EMAIL] = acct.email
        json.put(USER_EMAIL,acct.email!!)

        //json["name"] = acct.displayName
        json.put("name",acct.displayName!!)
        json["photoUrl"] = acct.photoUrl!!.toString()



        json["deviceID"] = bb.id(context)
        json["deviceName"] = bb.getDeviceName()
        json["deviceModel"] = bb.getDeviceModel()
        //jsonD.put("token",acct.getIdToken()); DO NOT TOUCH THIS, ONLY EDIT ON REGISTRATION NEW TOKEN
        json["dateLogin"] = System.currentTimeMillis().toString() + ""
        json["dateLoginReadable"] = Date(System.currentTimeMillis()).toString() + ""


        //Save credentials to pref
        val pref = context.getSharedPreferences(PREF_FOR_DATA_PATH, Context.MODE_PRIVATE)
        val editPref = pref.edit()
        editPref.putString(ROOT_USER_PATH, emailKey)
        editPref.putString(USER_EMAIL, json[USER_EMAIL].toString())
        editPref.putString(DEVICE_ID, json["deviceID"].toString())
        editPref.putLong(DEVICE_ID_CREATED, java.lang.Long.valueOf(json["dateLogin"].toString()))


        editPref.commit()


        sendRegistrationToServer(context,json["deviceID"].toString(), json)

    } catch (ex: Exception) {

        Log.d("AllStatic", "AllStatic saveNewCredentials *************Refreshed token Error: ")
        ex.printStackTrace()
    }

}


private fun sendRegistrationToServer(context: Context,id: String, data: Map<String, Any>) {
    //Implement this method if you want to store the token on your server

    val editor = MyFirestoreEditor(context)

    editor.saveInitParam(id, data, null, false, data)
}






/********************************SAVE ALL DEVICES PREFERENCES IN SHAREPREFERENCES********************************************************/
fun getDevicePreference(context: Context, deviceId:String):String? {

    try {

   var pref = context.getSharedPreferences(ALL_DEVICES_PREF_FILE_NAME, PRIVATE_MODE)



    pref?.let {

        it.getString(ALL_DEVICES_PREF_LIST_JSON,null)
    }?.let {

        var tok=StringTokenizer(it,"#$#*#")

        var hashMap=HashMap<String,String>()

        while (tok.hasMoreTokens()){

            var vals=tok.nextToken().split("->")

            hashMap.put(vals.get(0),vals.get(1))
        }

        hashMap

    }?.let {

        it.get(deviceId)
    }?.let {

        return it
    }







    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return ""

}





fun saveDevicePreference(context: Context, deviceId:String,data:JSONObject):Boolean? {

    try {

        val editor=MyFirestoreEditor(context);



        var pref = context.getSharedPreferences(ALL_DEVICES_PREF_FILE_NAME, PRIVATE_MODE)


        var hashMap=HashMap<String,String>()

        pref?.let {

            it.getString(ALL_DEVICES_PREF_LIST_JSON,null)
        }?.let {

            var tok=StringTokenizer(it,"#$#*#")



            while (tok.hasMoreTokens()){

                var vals=tok.nextToken().split("->")

                hashMap.put(vals.get(0),vals.get(1))
            }

            hashMap

        }

        hashMap.put(deviceId,data.toString())

        var build=StringBuilder()

        for(k in hashMap.keys){

            build.append(k+"->"+hashMap.get(k)+"#$#*#")
        }



        var edit=pref.edit()
        edit.putString(ALL_DEVICES_PREF_LIST_JSON,build.toString())
        edit.commit()

            return true








    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return false

}





fun getDevicePreferenceAsJson(context: Context, deviceId:String):JSONObject {

    var jsonDevice=JSONObject()
    jsonDevice.put("id",deviceId)

    try {


        val strPref=getDevicePreference(context,deviceId)

        if(strPref?.isNotBlank() as Boolean){

            jsonDevice=JSONObject(strPref)
        }






    } catch (ex: Exception) {

        ex.printStackTrace()
    }


    return jsonDevice

}


fun getRandomDateBetween(yearMin:Int,yearMax:Int):Timestamp{

    val dfDateTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val year = randBetween(yearMin, yearMax)// Here you can set Range of years you need
    val month = randBetween(0, 11)
    val hour = randBetween(9, 22) //Hours will be displayed in between 9 to 22
    val min = randBetween(0, 59)
    val sec = randBetween(0, 59)


    val gc = GregorianCalendar(year, month, 1)
    val day = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH))

    gc.set(year, month, day, hour, min, sec)

    return Timestamp(gc.time)
}


fun randBetween(start: Int, end: Int): Int {
    return start + Math.round(Math.random() * (end - start)).toInt()
}
