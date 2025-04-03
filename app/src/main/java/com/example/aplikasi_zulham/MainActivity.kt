package com.example.aplikasi_zulham

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.aplikasi_zulham.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Perbaikan urutan binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            ReplaceFragment(HomeFragment())
        }

        val bottomNav: BottomNavigationView = binding.NavButton
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Home -> ReplaceFragment(HomeFragment())
                R.id.Add -> ReplaceFragment(AddFragment())  // Ganti dengan fragment yang sesuai
                R.id.User -> ReplaceFragment(UserFragment()) // Ganti dengan fragment yang sesuai
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val windowInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.main2).updatePadding(bottom = windowInsets.bottom)
            return@setOnApplyWindowInsetsListener CONSUMED
        }
    }

    private fun ReplaceFragment(fragment: Fragment) {
        Log.i("Debug", "Fragment ${fragment.javaClass.simpleName} ditampilkan")
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
}
