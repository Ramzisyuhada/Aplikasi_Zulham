package com.example.aplikasi_zulham

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_zulham.Adapter.VideoAdapter
import com.example.aplikasi_zulham.Model.VideoModel
import com.example.aplikasi_zulham.databinding.FragmentVideoBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class Video : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoAdapter: VideoAdapter
    private var youTubePlayer: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

//      lifecycle.addObserver(binding.videoView) // penting: daftarin lifecycle player
//
//        // Setup YouTube Player Listener
//        binding.videoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(player: YouTubePlayer) {
//                youTubePlayer = player
//            }
//        })

        // Setup RecyclerView
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val videos = listOf(
            VideoModel(
                "Part 1 - Reviu Simulator Tank - asharilabs",
                "Galih Ashari R",
                "https://img.youtube.com/vi/xOxwURrI7KA/hqdefault.jpg",
                "https://youtu.be/9gvdO6r4Vjg?si=DymDU5gvljHQnRhc"
            ),
            VideoModel(
                "Part 2 - Persiapan Unity 3D",
                "Galih Ashari R",
                "https://img.youtube.com/vi/xOxwURrI7KA/hqdefault.jpg",
                "https://youtu.be/xOxwURrI7KA?si=ypSAknhbh5pWLayB"
            )
        )

        videoAdapter = VideoAdapter(videos) { video ->
            val videoId = extractVideoId(video.videoUrl)
            if (videoId != null) {
                showPopup(videoId)
            }
        }

        binding.playlistRecyclerView.adapter = videoAdapter

        return binding.root
    }

    private fun showPopup(videoId: String) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.fragment_popup_video, null)

        val popupYouTubePlayerView = popupView.findViewById<com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView>(R.id.popupVideoView)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, // full screen
            true // focusable
        )

        // Agar background hitam terlihat
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Tampilkan di tengah
        popupWindow.showAtLocation(binding.root, android.view.Gravity.CENTER, 0, 0)

        lifecycle.addObserver(popupYouTubePlayerView)

        popupYouTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.loadVideo(videoId, 0f)
            }
        })
    }

//    private fun showPopup(videoId: String) {
//        // Inflate the popup layout
//        val inflater = LayoutInflater.from(requireContext())
//        val popupView = inflater.inflate(R.layout.fragment_popup_video, null)
//
//        // Initialize the YouTubePlayerView inside the popup layout
//        val popupYouTubePlayerView: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView =
//            popupView.findViewById(R.id.popupVideoView)
//
//        // Create the PopupWindow with the specified layout parameters
//        val popupWindow = PopupWindow(popupView,
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT)
//
//        // Set the background of the PopupWindow to be transparent (or another color)
//        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        // Allow the popup to be dismissed if clicked outside
//        popupWindow.isOutsideTouchable = true
//        popupWindow.isFocusable = true
//
//        // Show the PopupWindow at the center of the screen
//        popupWindow.showAtLocation(binding.root, android.view.Gravity.CENTER, 0, 0)
//
//        lifecycle.addObserver(popupYouTubePlayerView)
//
//        // Set the YouTubePlayerListener for the popup player
//        popupYouTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(player: YouTubePlayer) {
//                // Load the selected video when the player is ready
//                player.loadVideo(videoId, 0f)
//            }
//        })
//    }

    private fun extractVideoId(url: String): String? {
        return when {
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&")
            }
            else -> null
        }
    }

    private fun playVideo(videoId: String) {
        youTubePlayer?.loadVideo(videoId, 0f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
