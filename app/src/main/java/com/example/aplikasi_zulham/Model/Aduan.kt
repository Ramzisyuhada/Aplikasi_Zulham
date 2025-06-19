package com.example.aplikasi_zulham.Model

import Alamat
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import java.io.File

class Aduan   {
    var  Gambar  = ArrayList<Bitmap>()
      var Lokasi : Alamat = Alamat()

    var DeskripsiMasalah : String = ""
    var isVideo: Boolean = false
    var videoFile: File? = null
    constructor()
    constructor(
        Gambar: ArrayList<Bitmap>,
        Lokasi: Alamat,
        DeskripsiMasalah: String
    )  {
        this.Gambar = Gambar
        this.Lokasi = Lokasi
        this.DeskripsiMasalah = DeskripsiMasalah
    }


}