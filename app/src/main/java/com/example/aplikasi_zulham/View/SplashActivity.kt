package com.example.aplikasi_zulham.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasi_zulham.Helper.NetworkHelper
import com.example.aplikasi_zulham.Helper.NetworkUtil
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAlert


class SplashActivity : AppCompatActivity(), NetworkHelper.NetworkListener {
    private lateinit var AlertJaringanDialog: ViewModelAlert
    private lateinit var networkHelper: NetworkHelper
    private var alreadyNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        AlertJaringanDialog = ViewModelAlert(this)
        networkHelper = NetworkHelper(this)
        networkHelper.setNetworkListener(this)
        networkHelper.startNetworkCallback()

        if (!NetworkHelper.isConnected(this)) {
            AlertJaringanDialog.startLoadingDialogJaringan()
        } else {
            navigateToLoginWithDelay()
        }
    }

    private fun navigateToLoginWithDelay() {
        if (!alreadyNavigated) {
            alreadyNavigated = true
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            AlertJaringanDialog.dismissDialog()
            navigateToLoginWithDelay()
        }
    }

    override fun onNetworkLost() {
        runOnUiThread {
            AlertJaringanDialog.startLoadingDialogJaringan()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkHelper.stopNetworkCallback()
    }
}
