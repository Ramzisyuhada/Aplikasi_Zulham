package com.example.aplikasi_zulham.Adapter

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.R
import com.github.islamkhsh.CardSliderAdapter
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView

class MediaAdapter(private val beritaList: ArrayList<Aduan>) :
    CardSliderAdapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.img)
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        var exoPlayer: ExoPlayer? = null
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun bindVH(holder: MediaViewHolder, position: Int) {
        val item = beritaList[position]

        holder.image.visibility = View.GONE
        holder.playerView.visibility = View.GONE

        if (!item.isVideo && item.videoFile != null) {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.videoFile!!.path)
                .placeholder(R.drawable.tahura1)
                .into(holder.image)
        }

        if (item.isVideo && item.videoFile != null) {
            val rawPath = item.videoFile!!.path
            val fixedPath = if (rawPath.startsWith("http:/") && !rawPath.startsWith("http://")) {
                rawPath.replaceFirst("http:/", "http://")
            } else rawPath

            val uri = Uri.parse(fixedPath)
            holder.playerView.visibility = View.VISIBLE

            // Hentikan player lama jika ada
            holder.exoPlayer?.release()

            // Inisialisasi player baru
            val context = holder.itemView.context
            val exoPlayer = ExoPlayer.Builder(context).build().also {
                it.setMediaItem(MediaItem.fromUri(uri))
                it.prepare()
                it.playWhenReady = true
            }

            holder.playerView.player = exoPlayer
            holder.exoPlayer = exoPlayer

            Log.d("EXO_VIDEO_URL", "Playing: $uri")
        }
    }

    override fun getItemCount(): Int = beritaList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_media, parent, false)
        return MediaViewHolder(view)
    }
}
