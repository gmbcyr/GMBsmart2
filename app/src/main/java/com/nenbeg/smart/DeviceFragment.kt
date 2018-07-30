package com.nenbeg.smart

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gmb.bbm2.tools.adapter.MyDeviceBeanAdapter
import com.google.firebase.firestore.Query
import com.nenbeg.smart.allstatic.DB_NAME
import com.nenbeg.smart.allstatic.getFireStoreEditor
import com.nenbeg.smart.app.NenbegApp
import com.nenbeg.smart.app.NenbegApp.Companion.userOwnerEmail
import com.nenbeg.smart.app.NenbegApp.Companion.userOwnerUid
import com.nenbeg.smart.dummy.DummyContent.DummyItem
import com.nenbeg.smart.model.MyDeviceBean
import com.nenbeg.smart.tools.firestore.MyFirestoreEditor
import com.tuya.smart.sdk.TuyaUser
import com.tuya.smart.sdk.bean.DeviceBean
import org.json.JSONObject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceFragment : Fragment(), MyDeviceBeanAdapter.OnItemSelectListener {


    override fun onItemSelected(document: MyDeviceBean, twoPane: Boolean) {

        listener?.onListFragmentInteraction(document)
    }



    // TODO: Customize parameters
    private var columnCount = 1
     val TAG:String="DeviceFragment"

    private var listener: OnListFragmentInteractionListener? = null
    lateinit var fsEdit: MyFirestoreEditor
    lateinit var mQuery: Query
    lateinit var mAdapter :  MyDeviceBeanAdapter
    lateinit var navControl: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }



        // Here notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true);

        fsEdit = getFireStoreEditor(this.requireContext())


    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_device_list2, container, false)


        val recyView = view.findViewById<RecyclerView>(R.id.list)


        //generateDevicesListTest();


        mQuery = fsEdit.getQueryFromCollection(MyFirestoreEditor.COLLECTION_DEVICES, 100)//.whereEqualTo(MyDeviceBean.FIELD_NAME_USER_OWNER_EMAIL,NenbegApp().userOwnerEmail)



        // Set the adapter
        if (recyView is RecyclerView) {

            //val listDevices= TuyaUser.getDeviceInstance().getDevList();

            Log.e("DeviceFragment","DeviceFragment onCreateView is recyView pos 0->")

            val listDevices= generateDevicesList()

            with(recyView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }




                Log.e("DeviceFragment","DeviceFragment onCreateView is recyView pos 1->")
                mAdapter =  MyDeviceBeanAdapter(mQuery, this@DeviceFragment,false,this,this@DeviceFragment.activity as AppCompatActivity)


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


        navControl= Navigation.findNavController(activity!!, R.id.nav_host_fragment)

        return view
    }


    fun generateDevicesListTest(){


        val devList=TuyaUser.getDeviceInstance().getDevList();


        Log.e("DeviceFragment","DeviceFragment generateDevicesList list->"+devList)

        /*val devi= TuyaUser.getDeviceInstance().getDev("AZ0on5zxaMYQRu");

        Log.e("DeviceFragment","DeviceFragment generateDevicesList static device->"+devi)*/

        val val1= MyDeviceBean()
        val1.name="Device1"
        val1.category="category"
        val1.devId="dev1"


        val val2= MyDeviceBean()
        val2.name="Device2"
        val2.category="category"
        val2.devId="dev2"


        val val3= MyDeviceBean()
        val3.name="Device3"
        val3.category="category"
        val3.devId="dev3"

        NenbegApp.run {

            val1.userOwnerEmail=userOwnerEmail
            val1.userOwnerUid=userOwnerUid

            val2.userOwnerEmail=userOwnerEmail
            val2.userOwnerUid=userOwnerUid

            val3.userOwnerEmail=userOwnerEmail
            val3.userOwnerUid=userOwnerUid

            Log.e("DeviceFragment","DeviceFragment generateDevicesList userOwnerEmail->"+userOwnerEmail+"_userOwnerUid ->"+userOwnerUid)
        }

        //fsEdit.getCollectionRef(MyFirestoreEditor.COLLECTION_DEVICES).document(val1.devId).set(val1)

        //fsEdit.getCollectionRef(MyFirestoreEditor.COLLECTION_DEVICES).document(val2.devId).set(val2)

        //fsEdit.getCollectionRef(MyFirestoreEditor.COLLECTION_DEVICES).document(val3.devId).set(val3)


        val retour= listOf<MyDeviceBean>(val1,val2,val3)



        for(it in retour){

            fsEdit.getCollectionRef(MyFirestoreEditor.COLLECTION_DEVICES).document(it.devId).set(it)

            //fsEdit.addRecord(fsEdit.getCollectionRef(MyFirestoreEditor.COLLECTION_DEVICES).document(it.devId),it,null)

        }








        //TuyaUser.getDeviceInstance().devList

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // First clear current all the menu items
        menu.clear();

        // Add the new menu items
        inflater.inflate(R.menu.top_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_add_device -> {

                var json= JSONObject()

                json.put("id",id)

                val args=Bundle()


                args.putString("jsonDevice",json.toString())

                navControl.navigate(R.id.addDeviceFragment,args)


                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }


    override fun onStart() {
        super.onStart()

        listener!!.onBackToAccueilFragment("data")

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
        fun onListFragmentInteraction(item: DeviceBean?)

        fun onBackToAccueilFragment( data:String)
    }




    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                DeviceFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }

        fun generateDevicesList():List<DeviceBean>{


            val devList=TuyaUser.getDeviceInstance().getDevList();


            Log.e("DeviceFragment","DeviceFragment generateDevicesList list->"+devList)

            /*val devi= TuyaUser.getDeviceInstance().getDev("AZ0on5zxaMYQRu");

            Log.e("DeviceFragment","DeviceFragment generateDevicesList static device->"+devi)*/

            val val1= MyDeviceBean()
            val1.name="Device1"
            val1.category="category"
            val1.devId="dev1"

            val val2= MyDeviceBean()
            val2.name="Device2"
            val2.category="category"
            val2.devId="dev2"


            val val3= MyDeviceBean()
            val3.name="Device3"
            val3.category="category"
            val3.devId="dev3"


            val retour= listOf<MyDeviceBean>(val1,val2,val3)



            for(it in retour){


            }

            devList.addAll(retour)


            return devList



            //TuyaUser.getDeviceInstance().devList

        }
    }
}
