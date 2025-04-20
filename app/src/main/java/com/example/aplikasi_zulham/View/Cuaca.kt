package com.example.aplikasi_zulham.View

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Controller.CuacaController
import com.example.aplikasi_zulham.Model.Cuaca
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.databinding.FragmentCuacaBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuacaView : Fragment() {

    private val controller = CuacaController()
    private var _binding: FragmentCuacaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCuacaBinding.inflate(inflater, container, false)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        lifecycleScope.launch {
            try {
                val cuacaList = controller.ambilData("52.02.04.2009")

                for (cuaca in cuacaList) {
                    val view = layoutInflater.inflate(R.layout.card_cuaca, binding.cardContainer, false)

                    view.findViewById<TextView>(R.id.DerajatID).text = "${cuaca.Suhu}°"
                    view.findViewById<TextView>(R.id.NamaTempatID).text = cuaca.Desc
                    view.findViewById<TextView>(R.id.TanggalDanJamID).text = cuaca.Tanggal

                    val bitmap = withContext(Dispatchers.IO) {
                        controller.svgUrlToBitmap(cuaca.Icon, 250, 250)
                    }

                    view.findViewById<ImageView>(R.id.IconCuacaID).setImageBitmap(bitmap)

                    binding.cardContainer.addView(view)
                }
            } catch (e: Exception) {
                Log.e("CuacaFragment", "Gagal memuat data", e)
            } finally {
                dialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "Cuaca"

        bottomNav.visibility = View.GONE
    }

    private fun updateUI(cuacaData: List<Cuaca>) {

        if (cuacaData.isNotEmpty()) {
            cuacaData.forEach {
                Log.d("CuacaView", "Suhu: ${it.Suhu}, Tanggal: ${it.Tanggal}, Tempat: ${it.Tanggal}")
            }
        } else {
            Log.d("CuacaView", "No weather data available.")
        }


    }
}
