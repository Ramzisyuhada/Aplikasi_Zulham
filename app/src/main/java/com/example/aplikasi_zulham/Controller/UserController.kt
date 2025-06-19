package com.example.aplikasi_zulham.Controller

import android.view.View
import android.widget.EditText
import com.example.aplikasi_zulham.Model.User
import com.example.aplikasi_zulham.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class UserController {
    fun Login(user: User, view: View): Int {
        val passwordLayout: TextInputLayout = view.findViewById(R.id.loginusername)
        val usernameLayout: TextInputLayout = view.findViewById(R.id.loginpassword)

        var hasil = 1

        if (user.Username.isEmpty()) {
            usernameLayout.error = "Username tidak boleh kosong"
            hasil = -1
        } else {
            usernameLayout.error = null
        }

        if (user.Password.isEmpty()) {
            passwordLayout.error = "Password tidak boleh kosong"
            hasil = -1
        } else {
            passwordLayout.error = null
        }

        return hasil
    }

    fun Register(user: User, view: View): Int {
        val usernameEdit: EditText = view.findViewById(R.id.registerusername)
        val emailEdit: EditText = view.findViewById(R.id.registeremail)
        val passwordLayout: TextInputLayout = view.findViewById(R.id.passwordEditText)
        val passwordLayout2: TextInputLayout = view.findViewById(R.id.passwordEditText2)
        var hasil = 1
        if (user.Username.isEmpty()) {
            usernameEdit.error = "Username tidak boleh kosong"
            hasil = -1
        } else {
            usernameEdit.error = null
        }

        if (user.Email.isEmpty()) {
            emailEdit.error = "Email tidak boleh kosong"
            hasil = -1
        } else {
            emailEdit.error = null
        }

        if (user.Password.isEmpty()) {
            passwordLayout.error = "Password tidak boleh kosong"
            hasil = -1
        } else {
            passwordLayout.error = null
        }

        if (user.Password2.isEmpty()) {
            passwordLayout2.error = "Konfirmasi password tidak boleh kosong"
            hasil = -1
        } else {
            passwordLayout2.error = null
        }

        if (user.Password != user.Password2) {
            passwordLayout2.error = "Konfirmasi password tidak cocok"
            hasil = -1
        }

        return hasil
    }
}