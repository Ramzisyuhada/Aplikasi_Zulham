package com.example.aplikasi_zulham.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_zulham.Controller.UserController
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.UsersController
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.Model.User
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert
import com.example.aplikasi_zulham.databinding.ActivityRegisterBinding
import com.example.aplikasi_zulham.repository.Users
import kotlinx.coroutines.launch

class register : AppCompatActivity() , NetworkHelper.NetworkListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var networkHelper: NetworkHelper

    private val userController = UserController()
    private var alert = ViewModelAlert(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(this)
        networkHelper.setNetworkListener(this)
        networkHelper.startNetworkCallback()

        setContentView(binding.root)
        binding.Registerbutton.setOnClickListener {
            val nama = binding.registerusername.text.toString().trim()
            val email = binding.registeremail.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val password2 = binding.passwordEditText2.text.toString()

            var isValid = true

            if (nama.isEmpty()) {
                binding.registerusername.error = "Nama tidak boleh kosong"
                isValid = false
            }

            if (email.isEmpty()) {
                binding.registeremail.error = "Email tidak boleh kosong"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.registeremail.error = "Format email tidak valid"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password tidak boleh kosong"
                isValid = false
            } else if (password.length < 8) {
                binding.passwordEditText.error = "Password minimal 8 karakter"
                isValid = false
            }

            if (password2.isEmpty()) {
                binding.passwordEditText2.error = "Konfirmasi password tidak boleh kosong"
                isValid = false
            } else if (password != password2) {
                binding.passwordEditText2.error = "Password tidak sama"
                isValid = false
            }

            if (!NetworkHelper.isConnected(this)) {
                alert.startLoadingDialogJaringan()
                return@setOnClickListener
            }

            if (isValid) {
                val user = Users(nama, password, email)
                val controller = UsersController()

                lifecycleScope.launch {
                    val response = controller.AddUsers(user)
                    if (response) {
                        Log.d("POST", "Hello world")
                        val intent = Intent(this@register, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }



    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            alert.dismissDialog()
        }
    }

    override fun onNetworkLost() {
        runOnUiThread {
            alert.startLoadingDialogJaringan()
        }
    }
}