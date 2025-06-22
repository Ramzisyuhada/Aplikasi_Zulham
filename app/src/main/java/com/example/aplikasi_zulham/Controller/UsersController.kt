package com.example.aplikasi_zulham.Controller

import android.util.Log
import com.example.aplikasi_zulham.Retrofit.UserInstance
import com.example.aplikasi_zulham.repository.Users
import com.example.aplikasi_zulham.repository.UsersLogin
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

    suspend fun Login(user: UsersLogin): Pair<String?, String?> {
        return try {
            val body = mapOf(
                "username" to user.username,
                "password" to user.password
            )
            val response = UserInstance.api.Login(body)

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                val token = data?.token
                val role = data?.user?.role
                Log.d("POST", "Token: $token, Role: $role")
                Pair(token, role)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Error: ${e.message}", e)
            Pair(null, null)
        }
    }




}