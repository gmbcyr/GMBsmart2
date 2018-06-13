package com.nenbeg.smart

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nenbeg.smart.ui.accueil.AccueilFragment

class AccueilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accueil_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AccueilFragment.newInstance())
                    .commitNow()
        }
    }

}
