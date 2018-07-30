package com.gmb.bbm2.tools.adapter



import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.tuya.smart.common.e
import java.util.*

/**
 * Created by GMB on 3/12/2018.
 */
abstract class MyFBadapter<VH : RecyclerView.ViewHolder>(private var mQuery: Query?) : RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {

    private var mRegistration: ListenerRegistration? = null



    var snapshot: ArrayList<DocumentSnapshot>? = ArrayList()
        protected set

    val position: Int
        get() = position


    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {

        if (p1 != null) {
            Log.w(TAG, "onEvent:error", p1)
            onError(p1)
            return
        }

        // Dispatch the event
        Log.e(TAG, "MyFBadapter onEvent:numChanges:" + p0?.documentChanges?.size)

        for (change in p0!!.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
            }
        }

        onDataChanged()
    }



    fun startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }

        snapshot!!.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query) {
        // Stop listening
        stopListening()

        // Clear existing data
        snapshot!!.clear()
        notifyDataSetChanged()

        // Listen to new query
        mQuery = query
        startListening()
    }

    override fun getItemCount(): Int {
        return snapshot!!.size
    }

    fun getSnapshot(index: Int): DocumentSnapshot? {
        return if (snapshot != null && snapshot!!.size > 0) snapshot!![index] else null
    }

    protected fun onDocumentAdded(change: DocumentChange) {
        snapshot!!.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    protected fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            snapshot!![change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            snapshot!!.removeAt(change.oldIndex)
            snapshot!!.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    protected fun onDocumentRemoved(change: DocumentChange) {
        snapshot!!.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    abstract fun onError(e: FirebaseFirestoreException)

    abstract fun onDataChanged()

    companion object {

        private val TAG = "FirestoreAdapter"
    }
}

