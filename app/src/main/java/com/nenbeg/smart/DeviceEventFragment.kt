package com.nenbeg.smart

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.gmb.bbm2.tools.adapter.MyDeviceBeanAdapter
import com.gmb.bbm2.tools.adapter.MyDeviceHistoAdapter
import com.google.firebase.firestore.Query
import com.nenbeg.smart.allstatic.getFireStoreEditor
import com.nenbeg.smart.allstatic.getRandomDateBetween
import com.nenbeg.smart.allstatic.myRandomInt
import com.nenbeg.smart.allstatic.randBetween
import com.nenbeg.smart.app.NenbegApp

import com.nenbeg.smart.dummy.DummyContent
import com.nenbeg.smart.dummy.DummyContent.DummyItem
import com.nenbeg.smart.model.DeviceEventDetails
import com.nenbeg.smart.model.MyDeviceBean
import com.nenbeg.smart.tools.customviews.CustomSnackBar
import com.nenbeg.smart.tools.customviews.MyBatteryLevel
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import com.tuya.smart.common.bv
import com.tuya.smart.sdk.TuyaUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_deviceevent_list2.*
import kotlinx.android.synthetic.main.snack_bar_option.view.*
import org.json.JSONObject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceEventFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceEventFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var id=""
    private var name=""
    private var cat=""
    private var batLevel=50

    private var listener: OnListFragmentInteractionListener? = null
    lateinit var navControl: NavController

    lateinit var  fsEdit:MyFirestoreEditor
    lateinit var mQuery: Query
    lateinit var mAdapter : MyDeviceHistoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            //columnCount = it.getInt(ARG_COLUMN_COUNT)
            var json=JSONObject(it.getString("jsonDevice"))

            Log.e("DeviceEvent","DeviceEvent OnCreate this is the json->"+json)

            id=json.getString("id")
            cat=json.getString("category")

            name=json.getString("name")
            batLevel=json.getInt("batLevel")
        }

        // Here notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true);

        fsEdit = getFireStoreEditor(this.requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_deviceevent_list2, container, false)

        val recyView = view.findViewById<RecyclerView>(R.id.list)

        generateRandomHisto()


        mQuery = fsEdit.db.document(fsEdit.rootInfoscRef+MyFirestoreEditor.COLLECTION_DEVICES+"/"+id)
                .collection(MyFirestoreEditor.COLLECTION_DEVICES_HISTO).limit(100).orderBy("timeEvent",Query.Direction.DESCENDING)



        // Set the adapter
        if (recyView is RecyclerView) {

            //val listDevices= TuyaUser.getDeviceInstance().getDevList();

            Log.e("DeviceEventFragment","DeviceEventFragment onCreateView is recyView pos 0->")

            val listDevices= DeviceFragment.generateDevicesList()

            with(recyView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }




                Log.e("DeviceFragment","DeviceFragment onCreateView is recyView pos 1->")
                mAdapter =  MyDeviceHistoAdapter(mQuery,this,this@DeviceEventFragment.activity as AppCompatActivity)


//recList.setHasFixedSize(true);
                val llm = LinearLayoutManager(this.context)
                llm.orientation = LinearLayoutManager.VERTICAL
                setLayoutManager(llm)

                Log.e("DeviceFragment","DeviceFragment onCreateView is recyView pos 2->")
//setListAdapter(mAdapter);
                setAdapter(mAdapter)

                Log.e("DeviceFragment","DeviceFragment onCreateView is recyView pos 3->"+mAdapter.snapshot)
            }
        }

        view.apply {

            var txt=findViewById<TextView>(R.id.txtHistoryEvent)

            txt?.apply {
                text=getString(R.string.deviceHisto, name)
            }
        }

        val myBatLevel=view.findViewById<MyBatteryLevel>(R.id.myBatLevel)

        myBatLevel.setupAttributes("Lvl", Color.GREEN,true,batLevel)



        val imgNotif=view.findViewById<ImageView>(R.id.imgNotif)

        imgNotif.setOnClickListener { view ->

            val snack=CustomSnackBar.getSnackBar(this.requireContext(),view,id)
        }


        navControl= Navigation.findNavController(activity!!, R.id.nav_host_fragment)




        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // First clear current all the menu items
       menu.clear();

        // Add the new menu items

        inflater.inflate(R.menu.device_event_menu, menu);


        super.onCreateOptionsMenu(menu, inflater);



    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        when(item?.itemId){

            R.id.nav_add_device_to_setting_fragment -> {

                var json=JSONObject()

                json.put("id",id)
                json.put("name",name)

                json.put("category",cat)

                val args=Bundle()


                args.putString("jsonDevice",json.toString())

                navControl!!.navigate(R.id.deviceSettingFragment,args)


                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        if(listener!=null) listener!!.hideNavigationForHistoFragment("data")
        if (mAdapter != null) {
            mAdapter.startListening()
        }
    }


    override fun onStop() {
        super.onStop()

        if (mAdapter != null) {
            mAdapter.stopListening()
        }
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









    fun generateRandomHisto(){


        val max= randBetween(5,20)

        for(i in 1..max){


            val buf=DeviceEventDetails(id, getRandomDateBetween(2017,2018),"type de test")

            fsEdit.db.document( fsEdit.rootInfoscRef+MyFirestoreEditor.COLLECTION_DEVICES+"/"+id).collection(MyFirestoreEditor.COLLECTION_DEVICES_HISTO).document(id+"_"+i).set(buf)


        }




        //TuyaUser.getDeviceInstance().devList

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
                DeviceEventFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }



    }
}
