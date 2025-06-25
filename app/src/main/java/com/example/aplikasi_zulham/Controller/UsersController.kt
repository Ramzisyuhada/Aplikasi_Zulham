package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Retrofit.UserInstance
import com.example.aplikasi_zulham.repository.LoginResponse
import com.example.aplikasi_zulham.repository.Users
import com.example.aplikasi_zulham.repository.UsersLogin
import com.google.gson.Gson
import retrofit2.Response

class UsersController {
    suspend fun AddUsers(user: Users): Boolean {
        return try {
            val response = UserInstance.api.AddUser(user)

            if (response.isSuccessful) {
                Log.d("POST", "Sukses: ${response.body()}")
                true
            } else {
                Log.e("POST", "Gagal - Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                false
            }

        } catch (e: Exception) {
            Log.e("POST", "Error: ${e.message}", e)
            false
        }
    }



    suspend fun ResetPassword(user : Users) : String
    {
        return try {

            val body  = mapOf(
                "email" to user.email
            )

            val response = UserInstance.api.ResetPassword(body)
           if (response.isSuccessful){
                return response.body().toString()
           }else{
               return response.errorBody().toString()

           }

        }catch (e : Exception){
            Pair("exception", e.localizedMessage)
            return ""
        }
    }

    suspend fun Login(user: UsersLogin): Pair<String?, String?> {
        return try {
            val body = mapOf(
                "username" to user.username,
                "password" to user.password,
                "id_tour" to  user.Destinasi
            )

            val response = UserInstance.api.Login(body)


            if (response.isSuccessful ) {
                val responseText = response.body()?.string()
                if (responseText?.startsWith("{") == true) {
                    val gson = Gson()
                    val loginResponse = gson.fromJson(responseText, LoginResponse::class.java)

                    val token = loginResponse.data?.token
                    val role = loginResponse.data?.user?.role
                    Log.d("LOGIN", "Token: $token, Role: $role")

                    Pair(token, role)
                }else {
                    Log.e("LOGIN", "Gagal login / response bukan JSON: $responseText")
                    Pair(null, null)
                }
            }else{
                Pair(null, null)

            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Exception: ${e.message}", e)
            Pair(null, null)
        }
    }





}