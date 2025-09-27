package com.example.aplikasi_zulham

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.aplikasi_zulham.databinding.FragmentVideoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Video : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private var mediaController: MediaController? = null
    private var lastPosition: Int = 0
    private var wasPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        val Linear = requireActivity().findViewById<LinearLayout>(R.id.header)

        bottomNav.visibility = View.GONE
        Linear.visibility = View.GONE
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Siapkan MediaController
        mediaController = MediaController(requireContext()).apply {
            setAnchorView(binding.videoView) // tombol kontrol akan nempel di VideoView
        }
        binding.videoView.setMediaController(mediaController)

        // 2) Sumber video
        // Contoh: dari raw (ganti `samplevideo` dengan nama file di res/raw)
        val uri: Uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.contentvideo}")


        binding.videoView.setVideoURI(uri)

        binding.videoView.setOnPreparedListener { mp ->
            mp.isLooping = false

            if (lastPosition > 0) {
                binding.videoView.seekTo(lastPosition)
                if (wasPlaying) binding.videoView.start()
            } else {
                // Auto-start (opsional)
                binding.videoView.start()
            }
        }

        // (Opsional) Listener error
        binding.videoView.setOnErrorListener { _, what, extra ->
            // Log atau tampilkan pesan ke user
            // return true kalau error sudah ditangani
            false
        }

        // (Opsional) Listener selesai
        binding.videoView.setOnCompletionListener {
            // contoh: kembali ke awal
            binding.videoView.seekTo(0)
        }
    }

    override fun onPause() {
        super.onPause()
        // Simpan state playback
        wasPlaying = binding.videoView.isPlaying
        lastPosition = binding.videoView.currentPosition
        binding.videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        // Lanjutkan jika sebelumnya lagi play
        if (lastPosition > 0) {
            binding.videoView.seekTo(lastPosition)
            if (wasPlaying) binding.videoView.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.videoView.stopPlayback()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        mediaController = null
        _binding = null
    }
}
