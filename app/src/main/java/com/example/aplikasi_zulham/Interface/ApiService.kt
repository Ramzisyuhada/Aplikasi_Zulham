package com.example.aplikasi_zulham.Interface

import CuacaResponse
import com.example.aplikasi_zulham.Model.Cuaca

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {



    @GET("publik/prakiraan-cuaca")
    suspend fun getWeatherData(@Query("adm4") adm4: String): Response<CuacaResponse>

}