package com.example.aplikasi_zulham.Interface

import CuacaResponse
import com.example.aplikasi_zulham.Model.Cuaca
import com.example.aplikasi_zulham.repository.LoginResponse
import com.example.aplikasi_zulham.repository.Users

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {



    @GET("publik/prakiraan-cuaca")
    suspend fun getWeatherData(@Query("adm4") adm4: String): Response<CuacaResponse>


    @POST("api/register")
    suspend fun AddUser(@Body user : Users): Response<Any>

    @POST("api/login")
    suspend fun Login(@Body user: Map<String, String>): Response<LoginResponse>


}
