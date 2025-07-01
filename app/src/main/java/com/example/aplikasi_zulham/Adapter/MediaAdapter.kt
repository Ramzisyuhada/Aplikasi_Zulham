package com.example.aplikasi_zulham.Adapter

import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.R
import com.github.islamkhsh.CardSliderAdapter

class MediaAdapter(private val beritaList: ArrayList<Aduan>,
    ): CardSliderAdapter<MediaAdapter.MediaViewHolder>()
{

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view)

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun bindVH(holder: MediaViewHolder, position: Int) {
        var AduanGambar = holder.itemView.findViewById<ImageView>(R.id.img)
        var AduanVideo = holder.itemView.findViewById<VideoView>(R.id.video)



        if(!beritaList[position].isVideo && beritaList[position].videoFile != null){

            val FilePath = beritaList[position].videoFile!!.path

            AduanGambar.setImageBitmap(BitmapFactory.decodeFile(FilePath))

        }

        if (beritaList[position].isVideo && beritaList[position].videoFile != null){
            val FilePath = beritaList[position].videoFile!!.path

            AduanVideo.setVideoPath(FilePath)
        }




    }

    override fun getItemCount(): Int {
        return beritaList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_media, parent, false)
        return MediaViewHolder(view)
    }

}