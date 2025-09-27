package com.example.aplikasi_zulham.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.UserController
import com.example.aplikasi_zulham.Controller.UsersController
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.Model.User
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert
import com.example.aplikasi_zulham.databinding.ActivityLoginBinding
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
        networkHelper = NetworkHelper(this).also {
            it.setNetworkListener(this)
            it.startNetworkCallback()
        }

        // ===== Spinner destinasi =====
        val kategori = arrayOf("Kuta", "Bukit Merese", "Pantai", "Sirkuit Mandalika")
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, kategori
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.PilihanDestinasi.adapter = adapter

        // ===== Prefs untuk remember me =====
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val remembered = prefs.getBoolean("remember", false)
        binding.rememberCheck.isChecked = remembered
        if (remembered) {
            // Prefill username & destinasi
            binding.usernameEditText.setText(prefs.getString("savedUsername", ""))
            val savedDestIndex = prefs.getInt("savedDestinationIndex", 0)
            if (savedDestIndex in kategori.indices) {
                binding.PilihanDestinasi.setSelection(savedDestIndex)
            }
        }

        // ===== Tombol daftar =====
        binding.click.setOnClickListener {
            if (NetworkHelper.isConnected(this)) {
                val intent = Intent(this, register::class.java)
                startActivity(intent)
            } else {
                alertDialog.startLoadingDialogJaringan()
            }
        }

        // ===== Tombol masuk =====
        binding.loginButton.setOnClickListener {
            val usernameInput = binding.usernameEditText.text?.toString().orEmpty()
            val passwordInput = binding.passwordEditText.text?.toString().orEmpty()

            // Validasi sederhana
            var hasError = false
            if (usernameInput.isEmpty()) {
                binding.loginusername.error = "Nama tidak boleh kosong"
                hasError = true
            } else {
                binding.loginusername.error = null
            }

            if (passwordInput.isEmpty()) {
                binding.loginpassword.error = "Password tidak boleh kosong"
                hasError = true
            } else {
                binding.loginpassword.error = null
            }

            if (hasError) return@setOnClickListener

            if (!NetworkHelper.isConnected(this)) {
                alertDialog.startLoadingDialogJaringan()
                return@setOnClickListener
            }

            // (Kalau kamu butuh logic UI lain, ini nampaknya memanggil validasi custom kamu)
            userController.Login(User(usernameInput, passwordInput), window.decorView.rootView)

            lifecycleScope.launch {
                try {
                    val destinasiId = binding.PilihanDestinasi.selectedItemPosition + 1
                    val controller = UsersController()
                    val result = controller.Login(UsersLogin(usernameInput, passwordInput, destinasiId))

                    val (token, role) = result.first
                    val (id, usernameResult) = result.second  // hindari bentrok nama variabel

                    if (token != null && role != null) {
                        // Simpan session utama (token, role, dll)
                        prefs.edit()
                            .putString("token", token)
                            .putString("role", role)
                            .putInt("id", id ?: -1)
                            .putString("username", usernameResult) // dari server
                            .putInt("DestinasiID", destinasiId)
                            .putString("NamaDestinasi", binding.PilihanDestinasi.selectedItem.toString())
                            .apply()

                        // Simpan/bersihkan data remember me
                        if (binding.rememberCheck.isChecked) {
                            prefs.edit()
                                .putBoolean("remember", true)
                                .putString("savedUsername", usernameInput)
                                .putInt("savedDestinationIndex", binding.PilihanDestinasi.selectedItemPosition)
                                .apply()
                        } else {
                            prefs.edit()
                                .remove("remember")
                                .remove("savedUsername")
                                .remove("savedDestinationIndex")
                                .apply()
                        }

                        // Lanjut ke Main
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("username", usernameResult)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Login error", e)
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan. Coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ===== Network callbacks =====
    override fun onNetworkAvailable() {
        runOnUiThread { alertDialog.dismissDialog() }
    }

    override fun onNetworkLost() {
        runOnUiThread { alertDialog.startLoadingDialogJaringan() }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkHelper.stopNetworkCallback()
    }
}
