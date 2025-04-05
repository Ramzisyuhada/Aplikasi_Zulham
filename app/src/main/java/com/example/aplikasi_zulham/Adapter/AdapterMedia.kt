package com.example.aplikasi_zulham.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.R

class AdapterMedia(
    private var beritaList: ArrayList<Aduan>,
    private val onItemClick: (Aduan) -> Unit,
    private val onDetailsClick: (Aduan,Int) -> Unit

) : RecyclerView.Adapter<AdapterMedia.AdapterViewHolder>() {

    fun updateData(newList: List<Aduan>) {
        beritaList.clear()
        beritaList.addAll(newList)
        notifyDataSetChanged()
    }
    class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.mediagambar)
        val detailsButton: AppCompatButton = itemView.findViewById(R.id.deletebutton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_cardview, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val berita = beritaList[position]
        if(berita.Gambar.isNotEmpty())  holder.image.setImageBitmap( berita.Gambar.last)

        holder.itemView.setOnClickListener {
            onItemClick(berita)
        }
        holder.detailsButton.setOnClickListener {
            onDetailsClick(berita,position)
        }
    }



    override fun getItemCount(): Int {
        return beritaList.size
    }
}
