package com.example.perludilindungi.faskes

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.example.perludilindungi.R
import com.example.perludilindungi.data.model.ProvinceResponse
import com.example.perludilindungi.data.remote.FaskesApi
import com.example.perludilindungi.databinding.FaskesFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FaskesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = FaskesFragment()
    }

    private lateinit var viewModel: FaskesViewModel
    private lateinit var provinceAdapter: ArrayAdapter<String>
    private var keyProvince = ArrayList<String>()
    private var valProvince = ArrayList<String>()
    private var keyCity = ArrayList<String>()
    private var valCity = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val provinsi = resources.getStringArray(R.array.provinsi)
//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsi)
        showProvinces()
//        provinceAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrayListOf())
        val view = FaskesFragmentBinding.inflate(inflater, container, false)
//        view.autoCompleteTextView.setAdapter(provinceAdapter)
        return view.root
    }

    private fun showProvinces() {
        FaskesApi.retrofitService.getProvinces().enqueue(object: Callback<ProvinceResponse>{
            override fun onResponse(call: Call<ProvinceResponse>, response: Response<ProvinceResponse>) {
                val listProvinces = response.body()?.results
                listProvinces?.forEach{
                    if (it != null) {
                        it.key?.let { it1 -> keyProvince.add(it1) }
                    }
                    if (it != null) {
                        it.value?.let { it1 -> valProvince.add(it1) }
                    }
                }
                val dropdownProvince = view?.findViewById<Spinner>(R.id.spinner)
                val adapter = ArrayAdapter(this@FaskesFragment.requireContext(), android.R.layout.simple_spinner_dropdown_item, valProvince)
                if (dropdownProvince != null) {
                    dropdownProvince.setAdapter(adapter)
                }

                if (dropdownProvince != null) {
                    dropdownProvince.onItemSelectedListener = this@FaskesFragment
                }
            }

            override fun onFailure(call: Call<ProvinceResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun renderList(newProvince: List<String>){
        provinceAdapter.clear()
        provinceAdapter.addAll(newProvince)
        provinceAdapter.notifyDataSetChanged()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FaskesViewModel::class.java)
        // TODO: Use the ViewModel
//        viewModel.provinces.observe(viewLifecycleOwner){
//            newProvinces -> renderList(newProvinces)
//        }
//
//        viewModel.getProvinces()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        p0?.getItemAtPosition(p2)
        val dropdownProvince = view?.findViewById<Spinner>(R.id.spinner)
        if(p0?.selectedItem == dropdownProvince?.selectedItem){
            showCity(keyProvince[p2])
        }
    }

    private fun showCity(s: String) {
        FaskesApi.retrofitService.getCity(s).enqueue(object: Callback<ProvinceResponse>{
            override fun onResponse(call: Call<ProvinceResponse>, response: Response<ProvinceResponse>) {
                keyCity.clear()
                valCity.clear()
                val listCity = response.body()?.results
                listCity?.forEach{
                    if (it != null) {
                        it.key?.let { it1 -> keyCity.add(it1) }
                    }
                    if (it != null) {
                        it.value?.let { it1 -> valCity.add(it1) }
                    }
                }
                val dropdownCity = view?.findViewById<Spinner>(R.id.spinner2)
                val adapter = ArrayAdapter(this@FaskesFragment.requireContext(), android.R.layout.simple_spinner_dropdown_item, valCity)
                if (dropdownCity != null) {
                    dropdownCity.setAdapter(adapter)
                }

                if (dropdownCity != null) {
                    dropdownCity.onItemSelectedListener = this@FaskesFragment
                }
            }

            override fun onFailure(call: Call<ProvinceResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}