package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Retrofit.AuthInstance
import com.example.aplikasi_zulham.Retrofit.UserInstance
import com.example.aplikasi_zulham.repository.LoginResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class AduanController {


    suspend fun AddAduan(aduan: Aduan, token: String,Alamat :String): Boolean {
        return try {
            val complaint = aduan.DeskripsiMasalah.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude = aduan.Lokasi.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude = aduan.Lokasi.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val completeAddress = Alamat
                .toRequestBody("text/plain".toMediaTypeOrNull()) // <- ini tambahkan
            val mediaParts = aduan.File.mapIndexed { index, file ->
                val mediaType = when {
                    file.extension.equals("mp4", true) -> "video/mp4"
                    else -> "image/jpeg"
                }.toMediaTypeOrNull()

                val requestFile = file.asRequestBody(mediaType)
                MultipartBody.Part.createFormData("media[$index]", file.name, requestFile)
            }


            val response = AuthInstance.getInstance(token).AddComplaint(
                complaint,
                latitude,
                longitude,
                completeAddress,
                mediaParts

            )

            return if (response.isSuccessful) {
                val jsonString = response.body()?.string()
                Log.d("POST", "Isi JSON: $jsonString")
                true
            } else {
                Log.e("POST", "Gagal kirim aduan: ${response.errorBody()?.string()}")
                false
            }

        } catch (e: Exception) {
            Log.e("POST", "Gagal mengirim aduan", e)
            false
        }
    }

//
    suspend fun GetAduanById (Id : Int,Token: String):JSONObject{
       return try {
            val Response = AuthInstance.getInstance(Token).getComplaintById(Id)
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}") // handle kalau null
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }

        }catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())
            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }        }
    }
    suspend fun getComplaintAllUserById(token: String, userId: Int, tourId: Int): JSONObject {
        return try {
            val response = AuthInstance.getInstance(token).getAllComplaintsByUserAndTour(userId, tourId)

            if (response.isSuccessful) {
                val bodyString = response.body()?.string()
                Log.d("PROFILE", "Response: $bodyString")
                JSONObject(bodyString ?: "{}")
            } else {
                val errorString = response.errorBody()?.string()
                Log.e("PROFILE", "Error body: $errorString")
                JSONObject(errorString ?: """{"error":"Unknown error"}""")
            }

        } catch (e: Exception) {
            Log.e("PROFILE", "Exception: ${e.message}", e)
            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }
        }
    }

    suspend fun GetAllAduan(id:Int,Token:String):JSONObject{
        return try {
            val Response = AuthInstance.getInstance(Token).GetAllAduan(id)
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}")
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }

        }catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())
            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }
        }
    }

    suspend fun GetAllAduanAdmin(id:Int,Token:String):JSONObject{
        return try {
            val Response = AuthInstance.getInstance(Token).GetAllAduanAdmin(id)
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}")
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }

        }catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())
            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }
        }
    }
    suspend fun GetAduanByIdAdmin (Id : Int,Token: String):JSONObject{
        return try {
            val Response = AuthInstance.getInstance(Token).GetAduanbyAdmin(Id)
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}") // handle kalau null
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }

        }catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())
            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }        }
    }


}