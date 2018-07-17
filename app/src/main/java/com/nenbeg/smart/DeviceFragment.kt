package com.nenbeg.smart

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.nenbeg.smart.dummy.DummyContent.DummyItem
import com.tuya.smart.sdk.TuyaUser
import com.tuya.smart.sdk.bean.DeviceBean


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
     val TAG:String="DeviceFragment"

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


        // Here notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_device_list2, container, false)


        val recyView = view.findViewById<RecyclerView>(R.id.list)

        // Set the adapter
        if (recyView is RecyclerView) {

            //val listDevices= TuyaUser.getDeviceInstance().getDevList();

            val listDevices= generateDevicesList()

            with(recyView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyDeviceRecyclerViewAdapter(listDevices, listener)
            }
        }
        return view
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
            /*R.id.post_stuff -> {
                Log.d(TAG, "Will post the photo to server")
                return true
            }
            R.id.cancel_post -> {
                Log.d(TAG, "Will cancel post the photo")
                return true
            }
            else -> {
            }*/
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

            val val1= DeviceBean()
            val1.name="Device1"
            val1.category="category"
            val1.devId="dev1"

            val val2= DeviceBean()
            val2.name="Device2"
            val2.category="category"
            val2.devId="dev2"


            val val3= DeviceBean()
            val3.name="Device3"
            val3.category="category"
            val3.devId="dev3"


            val retour= listOf<DeviceBean>(val1,val2,val3)

            devList.addAll(retour)


            return devList



            //TuyaUser.getDeviceInstance().devList

        }
    }
}
