package com.example.aplikasi_zulham.repository

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: UserData,
    val token: String,
    val id_tour: Int?
)

data class UserData(
    val id_users: Int,
    val username: String,
    val email: String,
    val role: String
)

