package com.nenbeg.smart.ui.accueil

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nenbeg.smart.R

class AccueilFragment : Fragment() {

    companion object {
        fun newInstance() = AccueilFragment()
    }

    private lateinit var viewModel: AccueilViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.accueil_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AccueilViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
