package com.example.aplikasi_zulham.View

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert
import com.example.aplikasi_zulham.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), NetworkHelper.NetworkListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkHelper: NetworkHelper
    private lateinit var alertDialog: ViewModelAlert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token  = prefs.getString("token", null)
        val role = prefs.getString("role", null)


        val username = intent.getStringExtra("username") ?: ""
        Log.i("POST" ,role.toString())

        if (role == "admin"){
            replaceFragment(Admin())
            Log.d("Role",username)

            binding.NavButton.visibility = View.GONE
        }else if (role == "user"){
            binding.NavButton.visibility = View.VISIBLE
            if (savedInstanceState == null) {
                replaceFragment(HomeFragment())
            }
        }
        // Inisialisasi helper
        networkHelper = NetworkHelper(this)
        alertDialog = ViewModelAlert(this)

        // Start listener jaringan
        networkHelper.setNetworkListener(this)
        networkHelper.startNetworkCallback()

        alertDialog


        // Navigasi bawah
        binding.NavButton.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.add -> replaceFragment(TutorialFragment())
                R.id.user -> replaceFragment(UserFragment())
            }
            true
        }

        // Menambahkan padding bawah jika ada sistem UI seperti gesture bar
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.main2).updatePadding(bottom = systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
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
