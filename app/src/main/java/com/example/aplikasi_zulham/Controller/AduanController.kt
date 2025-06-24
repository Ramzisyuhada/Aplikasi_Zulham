package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Retrofit.UserInstance
import retrofit2.Response

class AduanController {

    suspend fun AddAduan(Aduan : Aduan): Boolean{
        return try {
            val body = mapOf(
                "complaint" to Aduan.DeskripsiMasalah,
                "latitude" to Aduan.Lokasi.latitude,
                "longitude" to Aduan.Lokasi.longitude,
                "latitude" to Aduan.File
            )
            val Respone = UserInstance.api.AddComplaint(body)
            if (Respone.isSuccessful ){
                Log.i("POST" , Respone.body().toString())

                true
            }else{
                Log.i("POST" , Respone.errorBody().toString())
                false
            }
        }catch (e : Exception){
            Log.i("POST" , e.toString())

            false
        }
    }


}