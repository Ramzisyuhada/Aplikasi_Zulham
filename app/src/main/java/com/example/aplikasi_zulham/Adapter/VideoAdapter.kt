package com.example.aplikasi_zulham.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_zulham.Model.VideoModel
import com.example.aplikasi_zulham.databinding.ItemVideoBinding

class VideoAdapter(
    private val videos: List<VideoModel>,
    private val onVideoClick: (VideoModel) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.binding.titleTextView.text = video.title
        holder.binding.channelTextView.text = video.channel
        // Kalau mau load thumbnail pakai Glide/Picasso nanti

        holder.itemView.setOnClickListener {
            onVideoClick(video)
        }
    }

    override fun getItemCount() = videos.size
}