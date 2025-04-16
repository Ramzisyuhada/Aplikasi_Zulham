package com.example.aplikasi_zulham.Interface

import com.example.aplikasi_zulham.Model.Cuaca
import retrofit2.http.GET

interface ApiService {

    fun <T>  AmbilData() : List<T>


}