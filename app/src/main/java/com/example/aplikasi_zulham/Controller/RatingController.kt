package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Model.Rating
import com.example.aplikasi_zulham.Retrofit.AuthInstance
import okhttp3.Response
import org.json.JSONObject

class RatingController {

    /*
    * Api MenambahKan Rating Dengan Parameter Model Rating dan token .
    * akan mengambalikan nilai boolean
    * */
    suspend fun AddRating(rating: Rating , token : String) : Boolean {
        return try {
            val body = mapOf(
                "value" to rating.Value,
                "comment" to "user.password"
            )

            val Response = AuthInstance.getInstance(token).AddRating(body)
            if(Response.isSuccessful){
                Log.d("POST","Berhasil Menambahkan Rating")
                true
            }else {
                Log.d("POST","Berhasil Gagal Rating")

                false
            }



        } catch (e: Exception) {
            Log.e("POST", "Server Gangguan", e)
            false
        }
    }

    suspend fun GetRatingById(IdUser : Int ,IdDestinasi : Int, Token : String):JSONObject {
        return  try {
            val Response = AuthInstance.getInstance(Token).GetRating(IdUser,IdDestinasi)
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}")
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }
        } catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())

            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }
        }
    }
    suspend fun GetAllRating(Token : String):JSONObject {
        return  try {
            val Response = AuthInstance.getInstance(Token).GetAllRating()
            return if(Response.isSuccessful){
                val bodyString = Response.body()?.string()

                Log.d("POST", "Pesan : $bodyString")
                JSONObject(bodyString ?: "{}")
            }else{
                val errorString = Response.errorBody()?.string()
                Log.e("POST", "Error body : $errorString")

                JSONObject(errorString ?: "{\"error\":\"Unknown error\"}")
            }
        } catch (e : Exception){
            Log.e("POST","Error Nya Adalah : " + e.toString())

            JSONObject().apply {
                put("error", e.message ?: "Unknown exception")
            }
        }
    }
}