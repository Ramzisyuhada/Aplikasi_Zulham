package com.example.aplikasi_zulham.Model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

class Cuaca {

     var Suhu: Int = 0
    var Desc : String = ""
    var Tanggal: String = ""
    var Icon: String  = ""
    var Jam: String = ""

    constructor(Suhu: Int, Tanggal: String, Jam: String, Icon: String,Desc : String) {
        this.Suhu = Suhu
        this.Tanggal = Tanggal
        this.Jam = Jam
        this.Icon = Icon
        this.Desc = Desc
    }

    constructor()
}
