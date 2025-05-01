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
                "Judul 1",
                "Channel 1",
                "https://linkgambar.com/thumb1.jpg",
                "https://youtu.be/xOxwURrI7KA?si=pDXPWW0VMZMkmu_7"
            ),
            VideoModel(
                "Judul 2",
                "Channel 2",
                "https://linkgambar.com/thumb2.jpg",
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
        // Inflate the popup layout
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.fragment_popup_video, null)

        // Initialize the YouTubePlayerView inside the popup layout
        val popupYouTubePlayerView: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView =
            popupView.findViewById(R.id.popupVideoView)

        // Create the PopupWindow with the specified layout parameters
        val popupWindow = PopupWindow(popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        // Set the background of the PopupWindow to be transparent (or another color)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Allow the popup to be dismissed if clicked outside
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Show the PopupWindow at the center of the screen
        popupWindow.showAtLocation(binding.root, android.view.Gravity.CENTER, 0, 0)

        // Register lifecycle observer for the YouTubePlayer inside the popup
        lifecycle.addObserver(popupYouTubePlayerView)

        // Set the YouTubePlayerListener for the popup player
        popupYouTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                // Load the selected video when the player is ready
                player.loadVideo(videoId, 0f)
            }
        })
    }

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
