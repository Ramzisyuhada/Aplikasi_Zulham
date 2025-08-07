package com.example.aplikasi_zulham.Model



class User {
    var Username : String = ""
    var Password : String = ""
    var Password2 : String = ""
    var Email : String = ""
    var Role : String = ""
    var Token : String = ""

    constructor(Username : String ,Password : String,Password2 : String  , Email : String ,  Role : String ,Token : String){
        this.Username = Username
        this.Password2 = Password2
        this.Password = Password
        this.Email = Email
        this.Role = Role
        this.Token = Token

    }
    constructor(Username : String ,Password : String,Password2 : String  , Email : String ){
        this.Username = Username
        this.Password2 = Password2
        this.Password = Password
        this.Email = Email


    }
    constructor(Username : String , Password: String){
        this.Password = Password
        this.Username = Username
    }

    constructor(Username : String , Password: String,Email : String){
        this.Password = Password
        this.Username = Username
        this.Email = Email

    }

}