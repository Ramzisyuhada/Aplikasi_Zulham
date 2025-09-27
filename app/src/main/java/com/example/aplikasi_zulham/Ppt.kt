package com.example.aplikasi_zulham

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.aplikasi_zulham.databinding.FragmentPptBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Ppt : Fragment() {

    private var _binding: FragmentPptBinding? = null
    private val binding get() = _binding!!

    private var currentSlideIndex = 0
    private lateinit var slideImageView: ImageView

    // Ganti resource ini sesuai kebutuhanmu
    private val slideImages = arrayOf(
        R.drawable.slide1,
        R.drawable.slide2,
        R.drawable.slide3,
        R.drawable.slide4,
        R.drawable.slide5,
        R.drawable.slide6,
        R.drawable.slide7,
        R.drawable.slide8,
        R.drawable.slide9,
        R.drawable.slide10,
        R.drawable.slide11,
        R.drawable.slide12,
        R.drawable.slide13,
        R.drawable.slide14,
        R.drawable.slide15,
        R.drawable.slide16,
        R.drawable.slide17,
    )

    // --- Auto-hide controls ---
    private val uiHandler = Handler(Looper.getMainLooper())
    private var isControlVisible = true
    private val hideDelayMs: Long = 3000 // 3 detik

    // Runnable untuk menyembunyikan controls
    private val hideRunnable = Runnable {
        hideControls()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPptBinding.inflate(inflater, container, false)

        // Sembunyikan bottom nav & header dari Activity
        requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)?.visibility = View.GONE
        requireActivity().findViewById<LinearLayout>(R.id.header)?.visibility = View.GONE

        // Lock ke landscape selama fragment aktif
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Init views
        slideImageView = binding.slideImageView
        val btnPrev = binding.btnPrev
        val btnNext = binding.btnNext

        // Navigasi slide
        btnNext.setOnClickListener {
            goToNextSlide()
            resetHideTimer()
        }
        btnPrev.setOnClickListener {
            goToPreviousSlide()
            resetHideTimer()
        }

        // Tap layar untuk menampilkan kembali kontrol
        slideImageView.setOnClickListener {
            if (!isControlVisible) {
                showControls()
            } else {
                // Jika sudah terlihat, reset timer supaya auto-hide lagi setelah jeda
                resetHideTimer()
            }
        }

        // (Opsional) Jika punya tombol back di layout, aktifkan ini:
        binding.btnBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Tampilkan slide awal & mulai timer auto-hide
        displaySlide(currentSlideIndex)
        resetHideTimer()

        return binding.root
    }

    private fun displaySlide(index: Int) {
        if (index in slideImages.indices) {
            slideImageView.setImageResource(slideImages[index])
        }
    }

    private fun goToNextSlide() {
        currentSlideIndex = (currentSlideIndex + 1) % slideImages.size
        displaySlide(currentSlideIndex)
    }

    private fun goToPreviousSlide() {
        currentSlideIndex = if (currentSlideIndex == 0) {
            slideImages.size - 1
        } else {
            currentSlideIndex - 1
        }
        displaySlide(currentSlideIndex)
    }

    // --- Show/Hide Controls ---

    private fun showControls() {
        binding.buttonLayout.visibility = View.VISIBLE
        isControlVisible = true
        resetHideTimer()
    }

    private fun hideControls() {
        binding.buttonLayout.visibility = View.GONE
        isControlVisible = false
    }

    private fun resetHideTimer() {
        uiHandler.removeCallbacks(hideRunnable)
        uiHandler.postDelayed(hideRunnable, hideDelayMs)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)?.visibility = View.VISIBLE
        requireActivity().findViewById<LinearLayout>(R.id.header)?.visibility = View.VISIBLE

        uiHandler.removeCallbacks(hideRunnable)

        _binding = null
    }
}
