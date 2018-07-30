package com.gmb.bbm2.tools.adapter

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.nenbeg.smart.R
import com.nenbeg.smart.model.DeviceEventDetails
import com.nenbeg.smart.model.MyDeviceBean
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import com.nenbeg.smart.tools.listner.MyGestureListner
import kotlinx.android.synthetic.main.fragment_device.view.*

import java.text.NumberFormat
import java.text.SimpleDateFormat

/**
 * Created by GMB on 3/12/2018.
 */
class MyDeviceHistoAdapter(private val query: Query,
                           private val viewRoot:View,
                           private val activity: AppCompatActivity) :
        MyFBadapter<MyDeviceHistoAdapter.ViewHolder>(query),MyAdapterUpdate {





    var fbEdit: MyFirestoreEditor?=null


    init {
        fbEdit= MyFirestoreEditor(activity)



        /*mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as MyDeviceBean
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onItemSelected(item,false)
        }*/
    }

    override fun <T> getItemAtPostion(position: Int): Any {

        return super.getSnapshot(position)!!
    }

    override fun <T> removeFromList(position: Int): Any {

        val buf=getItemAtPostion<Any>(position)!!
        super.snapshot!!.remove(buf)
        super.notifyItemRemoved(position)

        return buf
    }

    override fun getMyAdapterPosition(): Int {

        return super.position
    }










    override fun afficheEltCorrespondantAuChoix(pos: Int) {

        val buf = getItemAtPostion<Any>(pos) as DocumentSnapshot




        showItemSelected(buf,false)
    }

    override fun afficheMenuContextuel(pos: Int) {

        /*val snap = getItemAtPostion<Any>(pos) as DocumentSnapshot

        val buf = snap.toObject(OnTimeNotificationFB::class.java)

        if (buf != null && buf!!.getPaidop()) {

            Toast.makeText(activity, activity.getString(R.string.cant_edit_this_notif), Toast.LENGTH_LONG).show()

            return

        }






        val listener = object : CustomDialogListSelector.onItemSelectListener {
            override fun onItemSelected(IDVal: Object, valSelected: Object) {


                if(activity.getString(R.string.ctx_mn_edit_val).equals(IDVal.toString())){


                    showItemSelected(snap,false)
                }
                else if(activity.getString(R.string.ctx_mn_pay_val).equals(IDVal.toString())){

                    paid(buf)

                }
                else if(activity.getString(R.string.ctx_mn_delete_val).equals(IDVal.toString())){

                    delete(buf)
                }
                else if(activity.getString(R.string.ctx_mn_delete_all_val).equals(IDVal.toString())){

                    deleteAll(buf)
                }

            }


        }


        val labels=activity.resources.getStringArray(R.array.context_menu)

        val vals=activity.resources.getStringArray(R.array.context_menu_val)


        //val vals=activity.resources.getStringArray(R.array.context_menu_val)
        val myVals=ArrayList<Object>()
        for(str in vals){

            (myVals as ArrayList<String>).add(str)
        }


        val myLabels=ArrayList<Object>()
        for(str in labels){

            (myLabels as ArrayList<String>).add(str)
        }


        val dialog = CustomDialogListSelector(activity, activity.getString(R.string.context_menu_title), myVals, myLabels, listener)
        dialog.show()*/





    }

    override fun onSwipeRight(pos: Int) {


        /*val snap = getItemAtPostion<Any>(pos) as DocumentSnapshot
        val buf = snap.toObject(OnTimeNotificationFB::class.java)

        if (buf != null) {

            if (!buf!!.getPaidop()) {

                paid(buf)

                /*buf = removeFromList<Any>(pos) as OnTimeNotification
                // OnTimeNotificationFB buf=re
                //send la notification a alarmService pour qu il mette a jour notif dans la bd
                MyReccurentDBrequest.updateNotification(context, buf!!.getBbmStatement().getId(), buf!!.getId(),
                        MyAlarmService.CREATE, context.getString(R.string.current_state_done),
                        MyAlarmService.CALLER_EVENT_DONE, buf!!.getBbmStatement().getMontant(),
                        buf!!.getTimestart(), true)*/

                return

            }

            Toast.makeText(activity, activity.getString(R.string.cant_edit_this_notif), Toast.LENGTH_LONG).show()

        }*/
    }

    override fun onSwipeLeft(pos: Int) {


        /*val snap = getItemAtPostion<Any>(pos) as DocumentSnapshot
        val buf = snap.toObject(OnTimeNotificationFB::class.java)

        if (buf != null) {

            if (!buf!!.getPaidop()) {

                cancel(buf)

                /*buf = removeFromList<Any>(pos) as OnTimeNotification
                // OnTimeNotificationFB buf=re
                //send la notification a alarmService pour qu il mette a jour notif dans la bd
                MyReccurentDBrequest.updateNotification(context, buf!!.getBbmStatement().getId(), buf!!.getId(),
                        MyAlarmService.CREATE, context.getString(R.string.current_state_done),
                        MyAlarmService.CALLER_EVENT_DONE, buf!!.getBbmStatement().getMontant(),
                        buf!!.getTimestart(), true)*/

                return

            }

            Toast.makeText(activity, activity.getString(R.string.cant_edit_this_notif), Toast.LENGTH_LONG).show()

        }*/
    }


    override   fun onDataChanged() {
        // Show/hide content if the query returns empty.
        /*if (getItemCount() === 0) {
            recyclerView.setVisibility(View.GONE)
            emptyView.setVisibility(View.VISIBLE)
        } else {
            recyclerView.setVisibility(View.VISIBLE)
            emptyView.setVisibility(View.GONE)
        }*/

        //Log.e("TAG", "MyDeviceBeanAdapter onDataChanged " )
    }

    override   fun onError(e: FirebaseFirestoreException) {
        // Show a snackbar on errors
        Snackbar.make(viewRoot,"Error: check logs for info.", Snackbar.LENGTH_LONG).show()


    }

    interface OnItemSelectListener {

        fun onItemSelected(document: MyDeviceBean, twoPane:Boolean)

    }


    //private lateinit var  mListener: OnItemSelectListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view= ViewHolder(inflater.inflate(R.layout.device_histo_line, parent, false),parent.context,this)



        return view
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position))

        var buf: MyDeviceBean = getSnapshot(position)!!.toObject(MyDeviceBean::class.java)!!


    }


    public fun resetQuery(query: Query){

        super.setQuery(query)
    }


     fun showItemSelected(documentSnap: DocumentSnapshot, mTwoPane: Boolean) {


        val buf = documentSnap.toObject(MyDeviceBean::class.java)
        //Resources resources = itemView.getResources();


         if(buf!=null){

            // mListener.onItemSelected(buf,mTwoPane)
         }
    }
























    class ViewHolder(itemView: View,private val context: Context,private val myAdapter: MyAdapterUpdate)//ButterKnife.bind(this, itemView);
        : RecyclerView.ViewHolder(itemView)/*,View.OnTouchListener*/ {


        private var mGestureDetector: GestureDetector?=null



        var dateFor=SimpleDateFormat("MMM. dd yyyy")
        var dayFor=SimpleDateFormat("dd")
        var hourFor=SimpleDateFormat("HH:mm")
        var numFor=NumberFormat.getCurrencyInstance()

         init{

             //itemView.setOnTouchListener(this)

            val gestureListner = MyGestureListner(context, myAdapter,  this)

            mGestureDetector = GestureDetector(context, gestureListner)

             //itemView!!.setOnTouchListener(this)
        }


        fun bind(snapshot: DocumentSnapshot?) {


            var item: DeviceEventDetails = snapshot!!.toObject(DeviceEventDetails::class.java)!!
            //Resources resources = itemView.getResources();

            itemView.content.setText(dateFor.format(item.timeEvent.toDate()))
            itemView.txtDetails1.setText(hourFor.format(item.timeEvent.toDate()))
            itemView.txtDetails2.setText(item.idDevice+"_"+item.typeEvt)



            /*with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }*/




            // Click listener


            /*itemView.setOnTouchListener() {
                if (listener !== null) {
                    listener!!.onItemSelected(snapshot!!,twoPane)
                }
            }*/
        }




       /* override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {


            //Toast.makeText(context,"CustomCOntextMenuNotif2 position->",Toast.LENGTH_SHORT).show();
            return mGestureDetector!!.onTouchEvent(p1)
        }*/

    }
}