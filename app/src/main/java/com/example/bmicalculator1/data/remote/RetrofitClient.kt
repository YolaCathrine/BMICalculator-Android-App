package com.example.bmicalculator1.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // For Android Emulator - use 10.0.2.2 to connect to localhost on host machine
    // If using physical device, change to your computer's IP: http://192.168.1.XXX:3000/
    
    // OPTION 1: For Emulator (default)
    private const val BASE_URL = "http://10.0.2.2:3000/"
    
    // OPTION 2: For Physical Device or if Option 1 doesn't work
    // Uncomment the line below and use your actual IP address
    // private const val BASE_URL = "http://192.168.2.101:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
