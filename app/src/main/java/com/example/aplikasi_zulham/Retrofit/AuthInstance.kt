package com.example.aplikasi_zulham.Retrofit

import com.example.aplikasi_zulham.Interface.ApiService
import com.example.aplikasi_zulham.Retrofit.UserInstance.gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthInstance {
    fun getInstance(token: String): ApiService {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))

            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.206.192.90:8080/api/")
            .client(client)

            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}