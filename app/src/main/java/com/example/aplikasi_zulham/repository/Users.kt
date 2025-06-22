package com.example.aplikasi_zulham.repository

data class Users(    var username : String,
                     var password : String,
                     var email : String)


data class UsersLogin(var username : String,var password:String)
