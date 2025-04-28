package com.example.aplikasi_zulham

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.aplikasi_zulham.databinding.FragmentPptBinding  // Import ViewBinding

class Ppt : Fragment() {

    private var _binding: FragmentPptBinding? = null  // Private reference to binding
    private val binding get() = _binding!!  // Public access to binding

    private var currentSlideIndex = 0  // Untuk melacak slide yang sedang ditampilkan
    private lateinit var slideImageView: ImageView

    // Array untuk gambar slide (contoh)
    private val slideImages = arrayOf(
        R.drawable.tahura2,  // Ganti dengan nama gambar slide pertama
        R.drawable.tahura1,  // Ganti dengan nama gambar slide kedua
        R.drawable.tahura2   // Ganti dengan nama gambar slide ketiga
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate layout dengan ViewBinding
        _binding = FragmentPptBinding.inflate(inflater, container, false)

        // Mengambil referensi ImageView dan tombol dari binding
        slideImageView = binding.slideImageView
        val btnPrev = binding.btnPrev
        val btnNext = binding.btnNext



        // Menangani klik tombol Next
        btnNext.setOnClickListener {
            goToNextSlide()
        }

        // Menangani klik tombol Previous
        btnPrev.setOnClickListener {
            goToPreviousSlide()
        }

        displaySlide(currentSlideIndex)

        return binding.root
    }

    private var isFullScreen = false

    private fun toggleFullScreenMode() {
//        val activity = activity ?: return
//
//        val params = binding.slideImageView.layoutParams as ConstraintLayout.LayoutParams
//
//        if (isFullScreen) {
//            // Kembali normal
//            (activity as AppCompatActivity).supportActionBar?.show()
//            //requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
//            activity.window.insetsController?.show(WindowInsets.Type.systemBars())
//
//            binding.buttonLayout.visibility = View.VISIBLE
//            binding.fabNext.visibility = View.VISIBLE
//
//            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//            params.bottomToTop = binding.buttonLayout.id
//
//        } else {
//            // Full screen
//            (activity as AppCompatActivity).supportActionBar?.hide()
//        //    requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
//            activity.window.insetsController?.hide(WindowInsets.Type.systemBars())
//
//            binding.buttonLayout.visibility = View.GONE
//            binding.fabNext.visibility = View.GONE
//
//            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
//        }
//
//        binding.slideImageView.layoutParams = params
//
//        isFullScreen = !isFullScreen
    }





    private fun displaySlide(index: Int) {
        // Memeriksa apakah index berada dalam rentang gambar yang ada
        if (index in slideImages.indices) {
            slideImageView.setImageResource(slideImages[index])  // Ganti gambar berdasarkan index
        }
    }

    private fun goToNextSlide() {
        // Increment index dan periksa apakah masih dalam batas array
        currentSlideIndex = (currentSlideIndex + 1) % slideImages.size
        displaySlide(currentSlideIndex)
    }

    private fun goToPreviousSlide() {
        // Decrement index dan periksa apakah masih dalam batas array
        currentSlideIndex = if (currentSlideIndex == 0) {
            slideImages.size - 1
        } else {
            currentSlideIndex - 1
        }
        displaySlide(currentSlideIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear binding reference to avoid memory leaks
    }
}
