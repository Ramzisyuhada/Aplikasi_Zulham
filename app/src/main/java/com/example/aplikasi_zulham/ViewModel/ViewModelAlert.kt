package com.example.aplikasi_zulham.ViewModel

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.aplikasi_zulham.R

class ViewModelAlert(private val activity: Activity) {
    private lateinit var alertDialog: AlertDialog

    fun startLoadingDialogJaringan() {
        if (activity.isFinishing || activity.isDestroyed) return

        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.alertdialog_jaringan, null)
        builder.setView(dialogView)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()
        val window = alertDialog.window
        window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun dismissDialog() {
        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }
}


