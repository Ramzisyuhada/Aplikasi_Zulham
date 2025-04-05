package com.example.aplikasi_zulham.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert
import com.example.aplikasi_zulham.databinding.ActivityRegisterBinding

class register : AppCompatActivity() , NetworkHelper.NetworkListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var networkHelper: NetworkHelper
    private var alert = ViewModelAlert(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(this)
        networkHelper.setNetworkListener(this)
        networkHelper.startNetworkCallback()

        setContentView(R.layout.activity_register)
        binding.Registerbutton.setOnClickListener {
            val nama = binding.registerusername.toString()
            val email = binding.registeremail.toString()
            val password = binding.passwordEditText.toString()
            val password2 = binding.passwordEditText2.toString()

            if(nama.isNotEmpty()){


            }
            if (!NetworkHelper.isConnected(this)) {
                alert.startLoadingDialogJaringan()
                return@setOnClickListener
            }

        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            alert.dismissDialog()
        }    }

    override fun onNetworkLost() {
        runOnUiThread {
            alert.startLoadingDialogJaringan()
        }
    }
}