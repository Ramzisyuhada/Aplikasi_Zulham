package com.example.aplikasi_zulham.Model

import android.graphics.Bitmap

class Laporan {
    private var _judul: String = ""
    private var _image: Bitmap? = null
    private var _aduan: String = ""
    private var _time: String = ""
    private var _lokasi: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(judul: String, image: Bitmap, aduan: String, time: String, lokasi: String) {
        this._judul = judul
        this._image = image
        this._aduan = aduan
        this._time = time
        this._lokasi = lokasi
    }
    constructor() {
        this._judul = judul
        this._image = null
        this._aduan = aduan
        this._time = time
        this._lokasi = lokasi
    }
    var judul: String
        get() = _judul
        set(value) {
            _judul = value
        }

    var image: Bitmap?
        get() = _image
        set(value) {
            _image = value
        }

    var aduan: String
        get() = _aduan
        set(value) {
            _aduan = value
        }

    var time: String
        get() = _time
        set(value) {
            _time = value
        }

    var lokasi: String
        get() = _lokasi
        set(value) {
            _lokasi = value
        }
}
