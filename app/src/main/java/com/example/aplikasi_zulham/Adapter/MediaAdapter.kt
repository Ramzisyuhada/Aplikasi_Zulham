package com.example.aplikasi_zulham.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.R
import com.github.islamkhsh.CardSliderAdapter

class MediaAdapter(private val beritaList: ArrayList<Aduan>,
    ): CardSliderAdapter<MediaAdapter.MediaViewHolder>()
{

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun bindVH(holder: MediaViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return beritaList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_cardview, parent, false)
        return MediaViewHolder(view)

    }

}