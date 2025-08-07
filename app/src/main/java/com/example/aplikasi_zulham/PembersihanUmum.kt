package com.example.aplikasi_zulham

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.RatingController
import com.example.aplikasi_zulham.Model.Rating
import com.example.aplikasi_zulham.View.HomeFragment
import com.example.aplikasi_zulham.databinding.FragmentPembersihanUmumBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PembersihanUmum : Fragment() {

    private var _binding: FragmentPembersihanUmumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembersihanUmumBinding.inflate(inflater, container, false)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        bottomNav.visibility = View.GONE

        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getInt("id", -1)
        val destinasiId = prefs.getInt("DestinasiID", -1)

        val controller = RatingController()

        val loadingDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        lifecycleScope.launch {
            val allRatings = token?.let { controller.GetAllRating(it) }
            val dataArray = allRatings?.getJSONArray("data")

            if (dataArray != null && dataArray.length() > 0) {
                var count = IntArray(6)
                val total = dataArray.length()
                for (i in 0 until total) {
                    val value = dataArray.getJSONObject(i).getInt("value")
                    if (value in 1..5) count[value]++
                }

                with(binding) {
                    bar1.max = 100
                    bar2.max = 100
                    bar3.max = 100
                    bar4.max = 100
                    bar5.max = 100

                    val sum = count.sum()
                    if (sum > 0) {
                        bar1.progress = count[1] * 100 / sum
                        bar2.progress = count[2] * 100 / sum
                        bar3.progress = count[3] * 100 / sum
                        bar4.progress = count[4] * 100 / sum
                        bar5.progress = count[5] * 100 / sum

                        JumlahUlasan.text = sum.toString()
                        val totalValue = count.indices.sumBy { it * count[it] }
                        val rataRata = totalValue.toFloat() / sum

                        JumlahRatingID.text = String.format("%.1f", rataRata)
                        ratingBar.rating = rataRata
                    }
                }
            }

            loadingDialog.dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        bottomNav.visibility = View.GONE

        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getInt("id", -1)
        val username = prefs.getString("username", null)
        val destinasiId = prefs.getInt("DestinasiID", -1)
        val namaDestinasi = prefs.getString("NamaDestinasi", null)

        val controller = RatingController()

        val header = requireActivity().findViewById<TextView>(R.id.head)
        header.text = "SKOR"

        binding.ButtonUlasanID.setOnClickListener {
            val reviewView = LayoutInflater.from(requireContext()).inflate(R.layout.card_review, null)
            val dialog = AlertDialog.Builder(requireContext()).setView(reviewView).create()

            val namaUserText = reviewView.findViewById<TextView>(R.id.NamaUserID)
            val namaTempatText = reviewView.findViewById<TextView>(R.id.NamaTempatID)
            val ratingBar = reviewView.findViewById<RatingBar>(R.id.ratingBar)
            val batalButton = reviewView.findViewById<Button>(R.id.DialogBtnClose1)
            val lanjutButton = reviewView.findViewById<Button>(R.id.LanjutBtnID)

            namaUserText.text = username
            namaTempatText.text = namaDestinasi

            lifecycleScope.launch {
                val existingRating = token?.let { controller.GetRatingById(userId, destinasiId, it) }
                val dataArray = existingRating?.getJSONArray("data")

                var ratingId = -1
                if (dataArray != null && dataArray.length() > 0) {
                    val ratingObj = dataArray.getJSONObject(0)
                    ratingBar.rating = ratingObj.getInt("value").toFloat()
                    ratingId = ratingObj.getInt("id_rating")
                }

                lanjutButton.setOnClickListener {
                    val ratingValue = ratingBar.rating.toInt()
                    val rating = Rating(ratingValue, ratingId)
                    lifecycleScope.launch {
                        val success = if (ratingId == -1) {
                            controller.AddRating(rating, token!!)
                        } else {
                            controller.UpdateRating(rating, token!!)
                        }

                        if (success) {
                            val successView = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_succes_ulasan, null)
                            val successDialog = AlertDialog.Builder(requireContext()).setView(successView).create()
                            successDialog.show()

                            val closeBtn = successView.findViewById<Button>(R.id.ok)
                            closeBtn.setOnClickListener {
                                successDialog.dismiss()
                                dialog.dismiss()
                                replaceFragment(HomeFragment())
                            }
                        } else {
                            Toast.makeText(requireContext(), "Gagal menyimpan rating", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                batalButton.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
