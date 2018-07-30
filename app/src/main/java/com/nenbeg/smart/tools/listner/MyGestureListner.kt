package com.nenbeg.smart.tools.listner

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import com.gmb.bbm2.tools.adapter.MyAdapterUpdate
import com.nenbeg.smart.allstatic.isClickMenuContextuel

class MyGestureListner(private val context:Context,private val myAdapter:MyAdapterUpdate,
                       private val viewHolder: RecyclerView.ViewHolder): GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {

        if (isClickMenuContextuel(context)) {


            myAdapter.afficheMenuContextuel(viewHolder.adapterPosition)

        } else {

            myAdapter.afficheEltCorrespondantAuChoix(viewHolder.adapterPosition)
        }
        return true
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {

        if (p0!!.getX() < p1!!.getX()) {
            //Log.d("Gesture ", "Left to Right swipe: "+ e1.getX() + " - " + e2.getX());
            myAdapter.onSwipeRight(viewHolder.adapterPosition)
            //Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
        }
        if (p0!!.getX() > p1!!.getX()) {
            //Log.d("Gesture ", "Right to Left swipe: "+ e1.getX() + " - " + e2.getX());
            myAdapter.onSwipeLeft(viewHolder.adapterPosition)
            //Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
        }
        if (p0!!.getY() < p1!!.getY()) {
            //Log.d("Gesture ", "Up to Down swipe: " + e1.getX() + " - " + e2.getX());
            //Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
        }
        if (p0!!.getY() > p1!!.getY()) {
            //Log.d("Gesture ", "Down to Up swipe: " + e1.getX() + " - " + e2.getX());
            //Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
        }
        return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {

        /* Log.d("Gesture ", " onScroll");
        if (e1.getY() < e2.getY()){
            Log.d("Gesture ", " Scroll Down");
        }
        if(e1.getY() > e2.getY()){
            Log.d("Gesture ", " Scroll Up");
        }*/
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {

        if (isClickMenuContextuel(context)) {


            myAdapter.afficheEltCorrespondantAuChoix(viewHolder.adapterPosition)

        } else {

            myAdapter.afficheMenuContextuel(viewHolder.adapterPosition)

        }
    }

    override fun onDoubleTap(p0: MotionEvent?): Boolean {

        //Log.d("Gesture ", " onDoubleTap");
        return true
    }

    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {

        //Log.d("Gesture ", " onDoubleTapEvent");
        return true
    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
        //Log.d("Gesture ", " onSingleTapConfirmed");
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {


    }
}