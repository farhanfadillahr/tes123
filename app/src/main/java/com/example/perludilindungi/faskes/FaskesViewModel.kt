package com.example.perludilindungi.faskes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.perludilindungi.data.remote.FaskesApi
import com.example.perludilindungi.data.remote.FaskesService

class FaskesViewModel : ViewModel() {
    private val _provinces = MutableLiveData<List<String>>()
    val provinces: LiveData<List<String>> get() = _provinces

    fun getProvinces(){
        _provinces.value = listOf("Jawa Barat")
    }
    // TODO: Implement the ViewModel
}