package com.example.aplikasi_zulham.Model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

data class Berita(val Judul : String,val Image: Bitmap,val Aduan : String, val Time : String) : ViewModel() {
}