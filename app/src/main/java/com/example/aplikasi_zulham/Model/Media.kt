package com.example.aplikasi_zulham.Model

import android.graphics.Bitmap
import android.net.Uri

class Media {

    var Image  = ArrayList<Bitmap>()
    var Video : Uri? = null

    constructor(Image : ArrayList<Bitmap>){
        this.Image = Image
    }

    constructor(Video : Uri){
        this.Video = Video
    }


}
