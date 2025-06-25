package com.example.aplikasi_zulham.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.Laporan
import com.example.aplikasi_zulham.R

class AdapterBerita(
    private val beritaList: ArrayList<Laporan>,
    private val onItemClick: (Laporan) -> Unit
) : RecyclerView.Adapter<AdapterBerita.AdapterViewHolder>() {


    class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val  Nama:TextView =  itemView.findViewById(R.id.textjudul)
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
        holder.Nama.text = berita.judul
        holder.image.setImageBitmap(berita.image)
        holder.textBerita.text = berita.aduan
        holder.time.text = berita.time

        holder.itemView.setOnClickListener {
            onItemClick(berita)
        }
    }
    fun updateData(newItems: List<Laporan>) {
        Log.d("ADAPTER", "updateData called with ${newItems.size} items")
        beritaList.clear()
        beritaList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return beritaList.size
    }

}
