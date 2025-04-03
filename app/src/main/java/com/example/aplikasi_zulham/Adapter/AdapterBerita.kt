package com.example.aplikasi_zulham.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.Berita
import com.example.aplikasi_zulham.R

class AdapterBerita(
    private val beritaList: ArrayList<Berita>,
    private val onItemClick: (Berita) -> Unit
) : RecyclerView.Adapter<AdapterBerita.AdapterViewHolder>() {

    class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.beritagambar)
        val textBerita: TextView = itemView.findViewById(R.id.textberita)
        val time: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.berita_cardview, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val berita = beritaList[position]

        holder.image.setImageBitmap(berita.Image)
        holder.textBerita.text = berita.Aduan
        holder.time.text = berita.Time

        holder.itemView.setOnClickListener {
            onItemClick(berita)
        }
    }

    override fun getItemCount(): Int {
        return beritaList.size
    }
}
