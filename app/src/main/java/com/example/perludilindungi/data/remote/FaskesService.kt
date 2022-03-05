package com.example.perludilindungi.data.remote

import com.example.perludilindungi.data.model.CheckInResponse
import com.example.perludilindungi.data.model.News
import com.example.perludilindungi.data.model.ProvinceResponse
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://perludilindungi.herokuapp.com"
private val logging = HttpLoggingInterceptor()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface FaskesService {

    @GET("/api/get-province")
    fun getProvinces():
            Call<ProvinceResponse>

    @GET("/api/get-city")
    fun getCity(@Query("start_id") start_id: String):
            Call<ProvinceResponse>

    @GET("/api/get-faskes-vaksinasi")
    fun getFaskesVaksinasi(@Query("province") province: String, @Query("city") city: String)


}

object FaskesApi {
    val retrofitService : FaskesService by lazy {
        retrofit.create(FaskesService::class.java) }
}