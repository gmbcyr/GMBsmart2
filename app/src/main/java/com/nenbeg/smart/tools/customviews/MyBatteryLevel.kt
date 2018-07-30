package com.nenbeg.smart.tools.customviews

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nenbeg.smart.R

class MyBatteryLevel(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {


    var battLevel= DEFAULT_LEVEL
    var battFillColor= DEFAULT_COLOR
    var battShowText= DEFAULT_SHOW_TEXT
    var battText= DEFAULT_TEXT_SHOWN


    lateinit var band1:TextView
    lateinit var band2:TextView
    lateinit var band3:TextView
    lateinit var band4:TextView
    lateinit var band5:TextView
    lateinit var band6:TextView
    lateinit var band7:TextView
    lateinit var band8:TextView
    lateinit var band9:TextView
    lateinit var band10:TextView

    lateinit var txtBatteryLevel: TextView

    lateinit var tab:ArrayList<TextView>

    init {
        val rootView= View.inflate(context,R.layout.my_battery_level,this)

        band1=rootView.findViewById(R.id.band1)
        band2=rootView.findViewById(R.id.band2)
        band3=rootView.findViewById(R.id.band3)
        band4=rootView.findViewById(R.id.band4)
        band5=rootView.findViewById(R.id.band5)
        band6=rootView.findViewById(R.id.band6)
        band7=rootView.findViewById(R.id.band7)
        band8=rootView.findViewById(R.id.band8)
        band9=rootView.findViewById(R.id.band9)
        band10=rootView.findViewById(R.id.band10)



        txtBatteryLevel=rootView.findViewById(R.id.txtLevelText)


        tab=ArrayList<TextView>()

        tab.add(band1)
        tab.add(band1)
        tab.add(band2)
        tab.add(band3)
        tab.add(band4)
        tab.add(band5)
        tab.add(band6)
        tab.add(band7)
        tab.add(band8)
        tab.add(band9)
        tab.add(band10)


        setupAttributes(attrs)

    }

    public fun setupAttributes(textVal:String=battText,fillColor:Int=battFillColor,showText:Boolean=battShowText,level:Int=battLevel) {



        battText = textVal

        battFillColor = fillColor

        battShowText = showText

        battLevel = level

        updateView()


    }

    private fun setupAttributes(attrs: AttributeSet?) {

        if (attrs == null) return

        val ta = context.obtainStyledAttributes(attrs, R.styleable.MyBatteryLevel)



        battText = ta.getString(R.styleable.MyBatteryLevel_battLvlTextShown)

        battFillColor = ta.getColor(R.styleable.MyBatteryLevel_battLvlColor, DEFAULT_COLOR)

        battShowText = ta.getBoolean(R.styleable.MyBatteryLevel_battLvlShowText, DEFAULT_SHOW_TEXT)

        battLevel = ta.getInt(R.styleable.MyBatteryLevel_battLvlLevel, DEFAULT_LEVEL)




        updateView()


        // 8
        // TypedArray objects are shared and must be recycled.
        ta.recycle()
    }


    fun updateView(){

        txtBatteryLevel.setText(battText+" "+battLevel+"%")


        val ind=battLevel/10

        for (i in 1..10){


            tab.get(i).setBackgroundColor(Color.TRANSPARENT)

            if(ind>=i){

                tab.get(i).setBackgroundResource(R.drawable.back_battery_cell)
            }
        }

        if(battShowText){

            txtBatteryLevel.visibility= View.VISIBLE
        }else{

            txtBatteryLevel.visibility= View.GONE
        }
    }

    companion object {


        private val DEFAULT_LEVEL=0
        private val DEFAULT_COLOR= Color.CYAN
        private val DEFAULT_COLOR_BORDER= Color.BLACK
        private val DEFAULT_SHOW_TEXT=true
        private val DEFAULT_TEXT_SHOWN="LEVEL "



    }
}

