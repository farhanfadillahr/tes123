package com.example.perludilindungi.data.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class CheckInRequest(
    val qrCode: String,
    val latitude: Double,
    val longitude: Double,
){
    val json = JSONObject()

    init{
        json.put("qrCode", qrCode)
        json.put("latitude", latitude)
        json.put("longitude", longitude)
    }
}
