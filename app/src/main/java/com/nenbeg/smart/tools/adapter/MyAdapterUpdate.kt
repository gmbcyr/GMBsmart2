package com.gmb.bbm2.tools.adapter

/**
 * Created by GMB on 2/26/2018.
 */
interface MyAdapterUpdate {

    //public Class<? extends Object> getItemAtPostion(int position);

    //public Class<? extends Object> removeFromList(int position);

    abstract fun <T> getItemAtPostion(position: Int): Any

    abstract fun <T> removeFromList(position: Int): Any

    abstract fun getMyAdapterPosition(): Int

    abstract fun afficheEltCorrespondantAuChoix(pos: Int)

    abstract fun afficheMenuContextuel(pos: Int)

    abstract fun onSwipeRight(pos: Int)

    abstract fun onSwipeLeft(pos: Int)
}