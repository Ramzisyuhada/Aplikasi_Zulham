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
    var File = ArrayList<File>()
    constructor()

    constructor(File : File ){
        this.videoFile = File
    }

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