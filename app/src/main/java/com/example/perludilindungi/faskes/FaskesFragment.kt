package com.example.perludilindungi.faskes

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.perludilindungi.R

class FaskesFragment : Fragment() {

    companion object {
        fun newInstance() = FaskesFragment()
    }

    private lateinit var viewModel: FaskesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val provinsi = resources.getStringArray(R.array.provinsi)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsi)
        val view = inflater.inflate(R.layout.faskes_fragment, container, false)
        view.autoCompleteTextView.setAdapter(arrayAdapter)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FaskesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}