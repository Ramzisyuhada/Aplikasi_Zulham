package com.example.aplikasi_zulham.ViewModel

import Alamat
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplikasi_zulham.Model.Aduan
import java.io.File

class ViewModelAduan : ViewModel() {
    var Judul: String = ""
    var Image: Bitmap? = null
    var Video : File? = null
    var Aduan: Aduan = Aduan()
    var Time: String = ""
    var Kategori: String = ""
    var alamat: Alamat = Alamat() //
    var media = MutableLiveData<ArrayList<Aduan>>(ArrayList())

    fun clear() {
        Video = null
        Judul = ""
        Image = null
        Aduan = Aduan()           // buat ulang object-nya
        Time = ""
        Kategori = ""
        alamat = Alamat()         // buat ulang object-nya
        media.value = ArrayList() // kosongkan list
    }
}
