package com.nenbeg.smart

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nenbeg.smart.allstatic.*
import com.nenbeg.smart.app.NenbegInstallation

import com.nenbeg.smart.dummy.DummyContent
import com.nenbeg.smart.dummy.DummyContent.DummyItem
import com.nenbeg.smart.tools.customviews.CustomSnackBar
import com.nenbeg.smart.tools.customviews.CustomSnackBar.Companion.updateUserPref
import com.nenbeg.smart.tools.customviews.MyBatteryLevel
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import com.nenbeg.smart.tools.utils.MyCustomInputText
import com.tuya.smart.common.bb
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_deviceevent_list2.*
import kotlinx.android.synthetic.main.fragment_setting_device.view.*
import kotlinx.android.synthetic.main.snack_bar_option.view.*
import org.json.JSONObject
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceSettingFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceSettingFragment : Fragment(),View.OnClickListener,MyCustomInputText.OnInputCompleted {
    override fun inputCompleted(idCaller:String?,input: String?) {

        when(idCaller){

            R.id.txtDevName.toString()->{

                name=input!!
                rootView!!.txtDevName.text=name

                jsonDevice?.put(DEVICE_PREF_NAME,name)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)



            }


            R.id.txtDevLocation.toString()->{


                rootView!!.txtDevLocation.text=input

                jsonDevice?.put(DEVICE_PREF_LOCATION,input)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)



            }
        }
    }

    override fun inputCancelled() {

        //nothing to do
    }


    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.txtDevName->{

                /*val inputAssist=MyCustomInputText(activity!!.applicationContext,name,this,MyCustomInputText.INPUT_TYPE_TEXT,R.id.txtDevName.toString())
                inputAssist?.show()*/

                showNewNameDialog(R.id.txtDevName.toString(),name)
            }


            R.id.txtDevLocation->{

               /* val inputAssist=MyCustomInputText(activity!!.applicationContext,name,this,MyCustomInputText.INPUT_TYPE_TEXT,R.id.txtDevLocation.toString())
                inputAssist.show()*/

                showNewNameDialog(R.id.txtDevLocation.toString(),location)


            }
        }
    }

    // TODO: Customize parameters
    private var columnCount = 1
    private var id=""
    private var name=""
    private var location=""
    private var cat=""
    private var batLevel=50
    private var rootView:View?=null
    private var jsonDevice:JSONObject?=null

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            //columnCount = it.getInt(ARG_COLUMN_COUNT)
            var json=JSONObject(it.getString("jsonDevice"))

            Log.e("DeviceEvent","DeviceEvent OnCreate this is the json->"+json)

            id=json.getString("id")
            cat=json.getString("category")

            name=json.getString("name")
            //batLevel=json.getInt("batLevel")
        }

        // Here notify the fragment that it should participate in options menu handling.
        //setHasOptionsMenu(true);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_device, container, false)

        rootView=view

        jsonDevice= getDevicePreferenceAsJson(activity!!.applicationContext,id)

        jsonDevice?.apply {

            if(!has(DEVICE_PREF_NAME)){

                jsonDevice?.put(DEVICE_PREF_NAME,name)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)
            }

            if(!has(DEVICE_PREF_CATEGORY)){

                jsonDevice?.put(DEVICE_PREF_CATEGORY,cat)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)
            }

            if(!has(DEVICE_PREF_LOCATION)){

                jsonDevice?.put(DEVICE_PREF_LOCATION,"Unset")
                updateUserPref(activity!!.applicationContext,jsonDevice!!)
            }
        }




        view.apply {

            var txtDevName=findViewById<TextView>(R.id.txtDevName)

            txtDevName?.apply {
                text=jsonDevice?.getString(DEVICE_PREF_NAME)
                setOnClickListener(this@DeviceSettingFragment)
            }


            var txtDevLocation=findViewById<TextView>(R.id.txtDevLocation)

            txtDevLocation?.apply {
                text=jsonDevice?.getString(DEVICE_PREF_LOCATION)
                setOnClickListener(this@DeviceSettingFragment)
            }
        }





        return view
    }


    fun updateUserPref(context: Context,json:JSONObject){

        val map = HashMap<String, Any>()
        try {

            val keys=json.keys()


            for (k in keys){

                map.put(k,json.get(k))
            }


            val editor = MyFirestoreEditor(context)


            editor.updateData(editor.deviceBeanCollectionRef.document(id),map,null)




        }
     catch (ex: Exception) {

        ex.printStackTrace()
    }

        saveDevicePreference(context,json.getString("id"),json)

        Toast.makeText(context,"User Pref Updated", Toast.LENGTH_LONG).show()
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear();

        // Add the new menu items

        inflater.inflate(R.menu.device_event_menu, menu);


        super.onCreateOptionsMenu(menu, inflater);


    }

    override fun onStart() {
        super.onStart()

        if(listener!=null) listener!!.hideNavigationForHistoFragment("data")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)

        fun hideNavigationForHistoFragment( data:String)
    }


    fun showNewNameDialog(callerID:String,previousText:String) {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dynamic_custom_input_text, null)
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById<View>(R.id.txt_saisie) as EditText

        editText?.setText(previousText)

        dialogBuilder.setTitle("Custom dialog")
        dialogBuilder.setMessage("Enter Name Below")
        dialogBuilder.setPositiveButton("Save", { dialog, whichButton ->
            //do something with edt.getText().toString();

            // Add Name in list
            updateOnInputCompleted(callerID,editText.text.toString())
            // Handler code here.


        })
        dialogBuilder.setNegativeButton("Cancel", { dialog, whichButton ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }



    fun updateOnInputCompleted(idCaller:String?,input: String?) {

        when(idCaller){

            R.id.txtDevName.toString()->{

                name=input!!
                rootView!!.txtDevName.text=name

                jsonDevice?.put(DEVICE_PREF_NAME,name)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)



            }


            R.id.txtDevLocation.toString()->{


                rootView!!.txtDevLocation.text=input

                jsonDevice?.put(DEVICE_PREF_LOCATION,input)
                updateUserPref(activity!!.applicationContext,jsonDevice!!)



            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        private const val ARG_EVENT = "event"

        /*fun bundleArgs(event: Lifecycle.Event): Bundle {
            return Bundle().apply {
                this.putParcelable(ARG_EVENT, event)
            }
        }*/


        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int,jsonDevice:JSONObject) =
                DeviceSettingFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }



    }
}
