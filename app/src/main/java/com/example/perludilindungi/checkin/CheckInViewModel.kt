package com.example.perludilindungi.checkin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perludilindungi.data.model.CheckInRequest
import com.example.perludilindungi.data.model.CheckInResponse
import com.example.perludilindungi.data.model.News
import com.example.perludilindungi.data.remote.CheckInApi
import com.example.perludilindungi.data.remote.CheckInService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class CheckInViewModel : ViewModel() {
    private var _scannedValue = ""
    var scannedValue: String
        get() = _scannedValue
        set(value){
            changeScannedValue(value)
        }

    private val _checkInStatus = MutableLiveData<String>("")
    val checkInStatus: LiveData<String>
        get() = _checkInStatus

    private val _checkInReason = MutableLiveData<String>("")
    val checkInReason: LiveData<String>
        get() = _checkInReason

    private val _responseContents = MutableLiveData<String>("")
    val responseContents: LiveData<String>
        get() = _responseContents

    private var _latitude : Double = 0.0
    var latitude : Double
        get() = _latitude
        set(value){
            _latitude = value
        }

    private var _longitude : Double = 0.0
    var longitude : Double
        get() = _longitude
        set(value){
            _longitude = value
        }

    init {

    }

    private fun changeScannedValue(newScannedValue: String){
        _scannedValue = newScannedValue
        submitScannedValue(newScannedValue)
    }

    private fun submitScannedValue(scannedValue: String){
//        Log.d("CheckInViewModel", CheckInRequest("checkin:cea26d78bbf33299419134de", -6.1351855, 11.0323457).json.toString())

        viewModelScope.launch {
            try {

                val requestBody = CheckInRequest(scannedValue, _latitude, _longitude )
                    .json
                    .toString() //Json in string
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val response = CheckInApi.retrofitService.postCheckIn(requestBody)


                _checkInStatus.value = response?.data.userStatus
                _checkInReason.value = response?.data?.reason
                //TODO Handle No Internet Connection
            }
            catch (e: Exception){
//                _responseContents.value = "Failure: " + e.message
                _checkInStatus.value = ""
                _checkInReason.value = ""
            }
        }



//        CheckInApi.retrofitService
//            .getNews()
//            .enqueue(object: Callback<News>{
//                override fun onResponse(call: Call<News>, response: Response<News>) {
//                    _responseContents.value = response.body()?.success.toString()
//                }
//
//                override fun onFailure(call: Call<News>, t: Throwable) {
//                    _responseContents.value = "Failure: " + t.message
//                }
//
//            })
    }
}