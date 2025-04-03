package com.example.aplikasi_zulham

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_zulham.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur klik tombol login

        binding.click.setOnClickListener {
            val intent = Intent(this,register::class.java)
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            // Mendapatkan teks dari EditText
            val username = binding.usernameEditText.text.toString()

            if (username.isEmpty()) {
                binding.loginusername.error = "Nama Tidak Boleh Kosong"
            } else {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                binding.loginusername.error = null
            }

    //           .
        }
    }
}
