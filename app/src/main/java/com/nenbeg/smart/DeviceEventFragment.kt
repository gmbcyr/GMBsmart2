package com.nenbeg.smart

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*

import com.nenbeg.smart.dummy.DummyContent
import com.nenbeg.smart.dummy.DummyContent.DummyItem

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DeviceEventFragment.OnListFragmentInteractionListener] interface.
 */
class DeviceEventFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

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
        val view = inflater.inflate(R.layout.fragment_deviceevent_list2, container, false)

        val recyView = view.findViewById<RecyclerView>(R.id.list)

        // Set the adapter
        if (recyView is RecyclerView) {
            with(recyView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyDeviceEventRecyclerViewAdapter(DummyContent.ITEMS, listener)
            }
        }
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // First clear current all the menu items
        menu.clear();

        // Add the new menu items
        inflater.inflate(R.menu.menu_device_event, menu);

        super.onCreateOptionsMenu(menu, inflater);


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
        fun newInstance(columnCount: Int) =
                DeviceEventFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }



    }
}
