package com.example.aplikasi_zulham

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasi_zulham.View.register
import com.example.aplikasi_zulham.databinding.ActivityForgetPasswordBinding
import com.example.aplikasi_zulham.databinding.ActivityLoginBinding
import com.example.aplikasi_zulham.databinding.ActivityRegisterBinding

class ForgetPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_forget_password)
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
        }
    }
}