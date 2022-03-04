package com.example.perludilindungi.data.remote

import com.example.perludilindungi.data.model.CheckInRequest
import com.example.perludilindungi.data.model.CheckInResponse
import com.example.perludilindungi.data.model.News
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "https://perludilindungi.herokuapp.com"

private val logging = HttpLoggingInterceptor()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface CheckInService {
    @POST("/check-in")
    suspend fun postCheckIn(@Body requestBody: RequestBody): CheckInResponse

    @GET("/api/get-news")
    fun getNews():
            Call<News>


}

object CheckInApi {
    val retrofitService : CheckInService by lazy {
        retrofit.create(CheckInService::class.java) }
}