package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Retrofit.AuthInstance
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

    suspend fun Login(user: UsersLogin): Pair<Pair<String?, String?>, Pair<Int?, String?>> {
        return try {
            val body = mapOf(
                "username" to user.username,
                "password" to user.password,
                "id_tour" to user.Destinasi
            )

            val response = UserInstance.api.Login(body)

            if (response.isSuccessful) {
                val responseText = response.body()?.string()
                if (responseText?.startsWith("{") == true) {
                    val gson = Gson()
                    val loginResponse = gson.fromJson(responseText, LoginResponse::class.java)

                    val id = loginResponse.data?.user?.id_users
                    val token = loginResponse.data?.token
                    val role = loginResponse.data?.user?.role
                    val username = loginResponse.data?.user?.username

                    Log.d("LOGIN", "Token: $token, Role: $role, ID: $id, Msg: $username")

                    Pair(Pair(token, role), Pair(id, username))
                } else {
                    Log.e("LOGIN", "Gagal login / response bukan JSON: $responseText")
                    Pair(Pair(null, null), Pair(null, "Invalid response format"))
                }
            } else {
                Log.e("LOGIN", "Login gagal - response code: ${response.code()}")
                Pair(Pair(null, null), Pair(null, "Login failed"))
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Exception: ${e.message}", e)
            Pair(Pair(null, null), Pair(null, e.message))
        }
    }



    suspend fun Logout(token :String): Boolean {
        return try {

            val response = AuthInstance.getInstance(token).Logout()

            if (response.isSuccessful) {
                true
            } else {
               false
            }
        } catch (e: Exception) {
            false
        }
    }



}