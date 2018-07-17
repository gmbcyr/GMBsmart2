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
import kotlinx.android.synthetic.main.snack_bar_option.view.*
import java.nio.file.Files.getAttribute


class CustomSnackBar {


    companion object {

        public fun getSnackBar(context:Context,src: View?,idDevice:String){

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

            val  snackDismiss=snackbar

            snackView.txtDone.setOnClickListener { view ->

                snackbar.dismiss()
            }

            //Log.e("Tag", "getSnackBar pos 2*****************************")

            snackView.txt_id_alarm.text=idDevice

            snackView.swc_bat_level.setOnClickListener { view ->

                updateUserPref(context)
            }

            //Log.e("Tag", "getSnackBar pos 3*****************************")

            snackView.swc_critical.setOnClickListener { view ->

                updateUserPref(context)
            }


            snackView.swc_detection.setOnClickListener { view ->

                updateUserPref(context)
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


        fun updateUserPref(context: Context){

            Toast.makeText(context,"User Pref Updated",Toast.LENGTH_LONG).show()
        }
    }
}