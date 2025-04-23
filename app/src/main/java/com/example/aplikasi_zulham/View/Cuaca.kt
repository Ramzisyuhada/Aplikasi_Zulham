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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.TextStyle
import java.util.ArrayList
import java.util.Locale

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.ZoneId

class CuacaView : Fragment() {

    private val controller = CuacaController()
    private var _binding: FragmentCuacaBinding? = null
    private val binding get() = _binding!!

    val datasuhu = ArrayList<Entry>()
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
                    val dateTime = OffsetDateTime.parse(cuaca.Tanggal).toLocalDateTime()

                    val jam = String.format("%02d:00", dateTime.hour)

                    val hari = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))

                    val epoch = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond().toFloat()
                    datasuhu.add(Entry(epoch, cuaca.Suhu.toFloat()))

                    view.findViewById<TextView>(R.id.HariID).text = hari
                    view.findViewById<TextView>(R.id.JamID).text = jam
                    view.findViewById<TextView>(R.id.HariID).text = hari

                    val bitmap = withContext(Dispatchers.IO) {
                        controller.svgUrlToBitmap(cuaca.Icon, 250, 250)
                    }

                    view.findViewById<ImageView>(R.id.IconCuacaID).setImageBitmap(bitmap)

                    binding.cardContainer.addView(view)
                    setupChart()

                }
            } catch (e: Exception) {
                Log.e("CuacaFragment", "Gagal memuat data", e)
            } finally {
                dialog.dismiss()
            }
        }


        return binding.root
    }
    private fun setupChart() {
        val mChart = binding.chart



        val dataSet = LineDataSet(datasuhu, "Suhu (°C)").apply {
            color = resources.getColor(R.color.black, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setCircleColor(resources.getColor(R.color.black, null))
            circleRadius = 5f
            valueTextSize = 12f
        }

        mChart.data = LineData(dataSet)
        mChart.setTouchEnabled(true)
        mChart.setPinchZoom(true)
        mChart.setScaleEnabled(true)
        mChart.animateX(1000)

        val description = Description().apply {
            text = "Perkiraan Suhu"
            textSize = 14f
        }
        mChart.description = description

        // Atur posisi sumbu Y
        val yAxis = mChart.axisLeft
        yAxis.setPosition(com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.OUTSIDE_CHART)

        // Atur sumbu X untuk menampilkan tanggal dan hari
        val xAxis = mChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Mengonversi epoch second menjadi waktu dan format menjadi tanggal dan hari
                val dateTime = OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(value.toLong()), java.time.ZoneOffset.UTC).toLocalDateTime()
                val hari = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
                val jam = String.format("%02d:00", dateTime.hour)
                return "$hari\n$jam"
            }
        }

        mChart.axisRight.isEnabled = false
        mChart.invalidate()
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
