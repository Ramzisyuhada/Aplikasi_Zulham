package com.example.aplikasi_zulham

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikasi_zulham.databinding.ActivityRegisterBinding

class register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_register)
        binding.Registerbutton.setOnClickListener {
            val nama = binding.registerusername.toString()
            val email = binding.registeremail.toString()
            val password = binding.passwordEditText.toString()
            val password2 = binding.passwordEditText2.toString()

            if(nama.isNotEmpty()){


            }

        }
    }
}