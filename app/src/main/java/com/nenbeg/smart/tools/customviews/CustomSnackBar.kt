package com.nenbeg.smart.tools.customviews

import android.app.PendingIntent.getActivity
import android.content.Context
import android.widget.TextView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.nenbeg.smart.R
import com.nenbeg.smart.allstatic.*
import kotlinx.android.synthetic.main.snack_bar_option.view.*
import nl.komponents.kovenant.Kovenant.context
import org.json.JSONObject
import java.nio.file.Files.getAttribute


class CustomSnackBar {


    companion object {

        public fun getSnackBar(context:Context,src: View?,idDevice:String){


            //Get the device saved pref

            var jsonDevice= getDevicePreferenceAsJson(context,idDevice)

            if(!jsonDevice.has(DEVICE_PREF_NOTIF_ON_ALARM)){

                jsonDevice.put(DEVICE_PREF_NOTIF_ON_ALARM,true)
                updateUserPref(context,jsonDevice)
            }

            if(!jsonDevice.has(DEVICE_PREF_NOTIF_IS_CRITICAL)){

                jsonDevice.put(DEVICE_PREF_NOTIF_IS_CRITICAL,false)
                updateUserPref(context,jsonDevice)
            }

            if(!jsonDevice.has(DEVICE_PREF_NOTIF_ON_BATTERY)){

                jsonDevice.put(DEVICE_PREF_NOTIF_ON_BATTERY,true)
                updateUserPref(context,jsonDevice)
            }

            //Log.e("Tag", "getSnackBar pos *****************************")

            // Create the Snackbar
            val snackbar = Snackbar.make(src!!, "Show my Snack Bar", Snackbar.LENGTH_LONG)
// Get the Snackbar's layout view
            val layout = snackbar.view as Snackbar.SnackbarLayout
// Hide the text
            val textView = layout.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.visibility =View.INVISIBLE

            //Log.e("Tag", "getSnackBar pos 1*****************************")

// Inflate our custom view
            val snackView = LayoutInflater.from(context).inflate(R.layout.snack_bar_option, null)

            snackView.apply {

                swc_detection.isChecked=jsonDevice.getBoolean(DEVICE_PREF_NOTIF_ON_ALARM)
                swc_bat_level.isChecked=jsonDevice.getBoolean(DEVICE_PREF_NOTIF_ON_BATTERY)
                swc_critical.isChecked=jsonDevice.getBoolean(DEVICE_PREF_NOTIF_IS_CRITICAL)
            }

            snackView.txtDone.setOnClickListener { view ->

                snackbar.dismiss()
            }

            //Log.e("Tag", "getSnackBar pos 2*****************************")

            snackView.txt_id_alarm.text=idDevice

            snackView.swc_bat_level.setOnClickListener { view ->

                jsonDevice.put(DEVICE_PREF_NOTIF_ON_BATTERY,snackView.swc_bat_level.isChecked)
                updateUserPref(context,jsonDevice)
            }

            //Log.e("Tag", "getSnackBar pos 3*****************************")

            snackView.swc_critical.setOnClickListener { view ->

                jsonDevice.put(DEVICE_PREF_NOTIF_IS_CRITICAL,snackView.swc_critical.isChecked)
                updateUserPref(context,jsonDevice)
            }


            snackView.swc_detection.setOnClickListener { view ->

                jsonDevice.put(DEVICE_PREF_NOTIF_ON_ALARM,snackView.swc_detection.isChecked)
                updateUserPref(context,jsonDevice)
            }



            //Log.e("Tag", "getSnackBar pos 4*****************************")

//If the view is not covering the whole snackbar layout, add this line
            layout.setPadding(0, 0, 0, 0)

// Add the view to the Snackbar's layout
            layout.addView(snackView, 0)
// Show the Snackbar
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.accent_material_dark));
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE)
            snackbar.show()

        }


        fun updateUserPref(context: Context,json:JSONObject){

            saveDevicePreference(context,json.getString("id"),json)

            Toast.makeText(context,"User Pref Updated",Toast.LENGTH_LONG).show()
        }
    }
}