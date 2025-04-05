package com.example.aplikasi_zulham.Model

import Alamat
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class Aduan   {
    var  Gambar  = ArrayList<Bitmap>()
      var Lokasi : Alamat = Alamat()
      var  KategoriAduan : String = ""
      var DeskripsiMasalah : String = ""

    constructor()
    constructor(
        Gambar: ArrayList<Bitmap>,
        Lokasi: Alamat,
        KategoriAduan: String,
        DeskripsiMasalah: String
    )  {
        this.Gambar = Gambar
        this.Lokasi = Lokasi
        this.KategoriAduan = KategoriAduan
        this.DeskripsiMasalah = DeskripsiMasalah
    }


}