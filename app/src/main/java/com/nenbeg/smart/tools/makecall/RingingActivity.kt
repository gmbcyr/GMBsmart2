package com.nenbeg.smart.tools.makecall

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.PowerManager
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.nenbeg.smart.R

class RingingActivity : AppCompatActivity() {

    private var networkCarrier: String? = null
    private var mp: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.ringing_view)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val fakeName = findViewById(R.id.chosenfakename) as TextView
        val fakeNumber = findViewById(R.id.chosenfakenumber) as TextView

        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        networkCarrier = tm.networkOperatorName


        val waker=WakeMyScreenUp(applicationContext)

        waker.wakeup(10000)

        val titleBar = findViewById(R.id.textView1) as TextView
        if (networkCarrier != null) {
            titleBar.text = "Incoming call - $networkCarrier"
        } else {
            titleBar.text = "Incoming call"
        }

        val callNumber = getContactNumber()
        val callName = getContactName()

        fakeName.setText(callName)
        fakeNumber.setText(callNumber)

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mp = MediaPlayer.create(applicationContext, notification)
        mp?.start()

        /*KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
        lock.disableKeyguard();*/

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        val answerCall = findViewById(R.id.answercall) as Button
        val rejectCall = findViewById(R.id.rejectcall) as Button

        answerCall.setOnClickListener { mp?.stop()
            mp?.isLooping=false
            mp?.release()

            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            finish()

        }
        rejectCall.setOnClickListener {
            mp?.stop()
            mp?.isLooping=false
            mp?.release()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            finish()
        }
    }


    private fun getContactNumber(): String? {
        var contact: String? = null
        val myIntent = intent
        val mIntent = myIntent.extras
        if (mIntent != null) {
            contact = mIntent.getString("number")
        }
        return contact
    }

    private fun getContactName(): String? {
        var contactName: String? = null
        val myIntent = intent
        val mIntent = myIntent.extras
        if (mIntent != null) {
            contactName = mIntent.getString("name")
        }
        return contactName
    }
}