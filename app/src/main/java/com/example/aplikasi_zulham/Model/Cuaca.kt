package com.example.aplikasi_zulham.Model

class Cuaca {
    var Suhu  = 0
    var Tanggal = ""
    var Jam = ""
    var Tempat = ""

    constructor(Suhu: Int, Tanggal: String, Jam: String, Tempat: String) {
        this.Suhu = Suhu
        this.Tanggal = Tanggal
        this.Jam = Jam
        this.Tempat = Tempat
    }
}