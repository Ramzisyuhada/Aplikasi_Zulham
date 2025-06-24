package com.example.aplikasi_zulham.Model

import Alamat
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class Aduan   {
    var  Gambar  = ArrayList<Bitmap>()
<<<<<<< HEAD
      var Lokasi : Alamat = Alamat()
      var DeskripsiMasalah : String = ""

=======
    var Lokasi : Alamat = Alamat()
    var DeskripsiMasalah : String = ""
    var isVideo: Boolean = false
    var videoFile: File? = null
    var File = ArrayList<File>()
>>>>>>> 6cebebf010d1ceb4654375a6160916e498a35a82
    constructor()


    constructor(DeskripsiMasalah: String, Lokasi: Alamat,File: ArrayList<File>){
        this.DeskripsiMasalah = DeskripsiMasalah
        this.Lokasi = Lokasi
        this.File = File
    }

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