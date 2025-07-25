package com.example.aplikasi_zulham.View

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_zulham.Controller.UserController
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.UsersController
import com.example.aplikasi_zulham.ForgetPassword
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.Model.User
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert
import com.example.aplikasi_zulham.databinding.ActivityLoginBinding
import com.example.aplikasi_zulham.repository.Users
import com.example.aplikasi_zulham.repository.UsersLogin
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), NetworkHelper.NetworkListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var networkHelper: NetworkHelper
    private lateinit var alertDialog: ViewModelAlert
    private val userController = UserController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alertDialog = ViewModelAlert(this)

        networkHelper = NetworkHelper(this)
        networkHelper.setNetworkListener(this)
        networkHelper.startNetworkCallback()


        val kategori = arrayOf("Kuta", "Bukit Merese", "Pantai", "Sirkuit Mandalika")
        val adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, kategori
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.PilihanDestinasi.adapter = adapter
        binding.click.setOnClickListener {
            if (NetworkHelper.isConnected(this)) {
                val intent = Intent(this, register::class.java)
                startActivity(intent)
            } else {
                alertDialog.startLoadingDialogJaringan()
            }
        }

        binding.ForgotPasswordID.setOnClickListener {
            if (NetworkHelper.isConnected(this)) {
                val intent = Intent(this, ForgetPassword::class.java)
                startActivity(intent)
            } else {
                alertDialog.startLoadingDialogJaringan()
            }
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val hasil = userController.Login(User(username,password),window.decorView.rootView)



            if (username.isEmpty()) {
                binding.loginusername.error = "Nama Tidak Boleh Kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.loginusername.error = "Nama Tidak Boleh Kosong"
                return@setOnClickListener
            }
            if (!NetworkHelper.isConnected(this)) {
                alertDialog.startLoadingDialogJaringan()
                return@setOnClickListener
            }

            binding.loginusername.error = null

            lifecycleScope.launch {
                val users = UsersLogin(username, password,binding.PilihanDestinasi.selectedItemPosition+1   )
                val controller = UsersController()
                val result = controller.Login(users)

                val (token, role) = result.first
                val (id, username) = result.second
                if (token != null && role != null) {

                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("token", token)
                        .putString("role", role)
                        .putInt("id", id ?: -1)
                        .putString("username",username)
                        .putInt("DestinasiID",binding.PilihanDestinasi.selectedItemPosition+1)
                        .putString("NamaDestinasi",binding.PilihanDestinasi.selectedItem.toString())
                        .apply()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            alertDialog.dismissDialog()
        }

    }

    override fun onNetworkLost() {
        runOnUiThread {
            alertDialog.startLoadingDialogJaringan()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkHelper.stopNetworkCallback()
    }
}
