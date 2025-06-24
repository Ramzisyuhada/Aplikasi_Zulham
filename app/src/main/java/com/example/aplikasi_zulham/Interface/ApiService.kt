package com.example.aplikasi_zulham.Interface

import CuacaResponse
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Model.Cuaca
import com.example.aplikasi_zulham.repository.LoginResponse
import com.example.aplikasi_zulham.repository.Users
import okhttp3.ResponseBody

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


    @POST("register")
    suspend fun AddUser(@Body user : Users): Response<Any>

    @JvmSuppressWildcards
    @POST("login")
    suspend fun Login(@Body user: Map<String, Any>): Response<ResponseBody>

    @POST("forget-password")
    suspend fun ResetPassword(@Body User: Map<String, String>) : Response<Any>



    //Users
    @POST("complaints")
    suspend fun AddComplaint(@Body Aduan: Map<String, Any>) :Response<Any>


}
