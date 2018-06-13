package com.gmb.bbm2.tools.firestore

import com.google.firebase.firestore.QuerySnapshot

/**
 * Created by GMB on 3/13/2018.
 */
interface onActionDone {


    abstract fun onListDone(list: QuerySnapshot?)

}