package com.example.aplikasi_zulham.ViewModel

import Alamat
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplikasi_zulham.Model.Aduan

class ViewModelAduan : ViewModel() {
    var Judul: String = ""
    var Image: Bitmap? = null
    var Aduan: Aduan = Aduan()
    var Time: String = ""
    var Kategori: String = ""
    var alamat: Alamat = Alamat() //
    var media = MutableLiveData<ArrayList<Aduan>>(ArrayList())
}
