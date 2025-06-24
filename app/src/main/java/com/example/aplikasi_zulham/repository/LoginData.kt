package com.example.aplikasi_zulham.repository

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: User?,
    val token: String?,
    val id_tour: String?,
    val role: String?
)

data class User(
    val id_users: Int,
    val username: String,
    val email: String,
    val role: String
)
