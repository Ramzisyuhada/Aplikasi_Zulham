package com.example.aplikasi_zulham.Retrofit

import com.example.aplikasi_zulham.Interface.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


    object UserInstance {
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()
//        val client = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(token))
//            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.114.40:8000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(ApiService::class.java)
    }