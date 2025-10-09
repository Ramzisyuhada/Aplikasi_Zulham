package com.example.aplikasi_zulham.View

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.aplikasi_zulham.*
import com.example.aplikasi_zulham.Adapter.AdapterBerita
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.Model.Laporan
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterBerita: AdapterBerita
    private val beritaList = ArrayList<Laporan>()
    private data class AduanSearch(val username: String, val idComplaint: Int)
    private val searchIndex = mutableListOf<AduanSearch>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        binding.pembersihanumum.setOnClickListener { replaceFragment(PembersihanUmum()) }
        binding.aduanterterbaru.setOnClickListener { replaceFragment(ListAduan()) }
        binding.Weather.setOnClickListener { replaceFragment(CuacaView()) }
        binding.eco.setOnClickListener { replaceFragment(EcoTourismeGuide()) }
        binding.aduan.setOnClickListener { replaceFragment(AduanKu()) }
        binding.zone.setOnClickListener { replaceFragment(MapInteractive()) }

        setupRecyclerView()
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        adapterBerita = AdapterBerita(ArrayList()) { laporan ->
            // Handle item click if needed
        }

        binding.recyclerView.adapter = adapterBerita
    }

    private fun loadData(dialog: Dialog) {
        val controller = AduanController()
        val prefs = requireContext().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val destinasiId = prefs.getInt("DestinasiID", -1)

        beritaList.clear()
        searchIndex.clear()    // ⬅️ penting: reset index

        lifecycleScope.launch {
            try {
                val json = token?.let { controller.GetAllAduan(destinasiId, it) }
                val dataArray = json?.optJSONArray("data")

                if (dataArray == null || dataArray.length() == 0) {
                    dialog.dismiss()
                    return@launch
                }

                var selesai = 0
                val total = dataArray.length()

                for (i in 0 until total) {
                    val item = dataArray.getJSONObject(i)
                    val complaintId = item.getInt("id_complaint")

                    val username = item.getJSONObject("user").getString("username")
                    val complaint = item.getString("complaint")
                    val tanggal = item.getString("complaint_date")
                    val jam = tanggal.split(" ")
                    val tourName = item.getJSONObject("tour").getString("tour_name")
                    val mediaArray = item.getJSONArray("media")
                    searchIndex.add(AduanSearch(username = username, idComplaint = complaintId))

                    ambilGambarDariMedia(mediaArray) { bitmap ->
                        beritaList.add(
                            Laporan(
                                username,
                                bitmap,
                                complaint,
                                convertToAmPm(jam[1]),
                                tourName
                            )
                        )

                        selesai++

                        requireActivity().runOnUiThread {
                            adapterBerita.updateData(beritaList.take(3))
                        }

                        if (selesai == total) {
                            dialog.dismiss()
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.e("HomeFragment", "JSON error: ${e.message}")
                dialog.dismiss()
            }
        }
    }

    private fun ambilGambarDariMedia(mediaArray: JSONArray, onBitmapReady: (Bitmap) -> Unit) {
        var imagePath: String? = null

        for (j in 0 until mediaArray.length()) {
            val mediaItem = mediaArray.getJSONObject(j)
            val mediaType = mediaItem.getString("media_type")
            val path = mediaItem.getString("path")

            if (mediaType == "image" && path.endsWith(".jpg")) {
                imagePath = path
                break
            }
        }

        val fullUrl = "http://13.216.4.3$imagePath"

        if (imagePath != null) {
            Glide.with(requireContext())
                .asBitmap()
                .load(fullUrl)
                .error(R.drawable.tahura1)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        onBitmapReady(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Log.e("HomeFragment", "Gagal load gambar dari $fullUrl")
                        onBitmapReady(BitmapFactory.decodeResource(resources, R.drawable.tahura1))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            onBitmapReady(BitmapFactory.decodeResource(resources, R.drawable.tahura1))
        }
    }

    private fun convertToAmPm(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time24)
        return outputFormat.format(date)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = requireActivity().findViewById<TextView>(R.id.head)
        text.text = "SIWASPADA"

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        bottomNav.visibility = View.VISIBLE
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val namadestinasi = prefs.getString("NamaDestinasi", null)
        val sliderList = when (namadestinasi) {
            "Kuta" -> listOf(
                SlideModel(R.drawable.kuta1, ScaleTypes.FIT),
                SlideModel(R.drawable.kuta2, ScaleTypes.FIT),
                SlideModel(R.drawable.kuta3, ScaleTypes.FIT)

            )
            "Pantai" -> listOf(
                SlideModel(R.drawable.pantai1, ScaleTypes.FIT),
                SlideModel(R.drawable.pantai2, ScaleTypes.FIT),
                SlideModel(R.drawable.pantai3, ScaleTypes.FIT)

            )
            "Bukit Merese" -> listOf(
                SlideModel(R.drawable.bukit1, ScaleTypes.FIT),
                SlideModel(R.drawable.bukit2, ScaleTypes.FIT),
                SlideModel(R.drawable.bukit3, ScaleTypes.FIT)
            )
            "Sirkuit Mandalika" -> listOf(
                SlideModel(R.drawable.mandalika1, ScaleTypes.FIT),
                SlideModel(R.drawable.mandalika2, ScaleTypes.FIT),
                SlideModel(R.drawable.mandalika3, ScaleTypes.FIT)
            )
            else -> listOf(
                SlideModel(R.drawable.tahura1, ScaleTypes.FIT),
                SlideModel(R.drawable.tahura2, ScaleTypes.FIT)
            )
        }

        binding.imageSlider.setImageList(sliderList, ScaleTypes.FIT)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query?.trim().orEmpty()
                if (q.isEmpty()) return false

                // cari di index (username contains query)
                val matches = searchIndex.filter { it.username.contains(q, ignoreCase = true) }

                if (matches.isEmpty()) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Tidak ditemukan: $q",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Ambil data pertama yang cocok dan buat Bundle
                    val m = matches.first()
                    val bundle = Bundle().apply {
                        putInt("id_complaint", m.idComplaint)
                        putString("username", m.username)
                    }

                    // Langsung pindah ke fragment Aduan dengan bundle
                    val aduanFragment = Aduan().apply { arguments = bundle }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.Frame, aduanFragment)
                        .addToBackStack(null)
                        .commit()
                }

                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })


    }

    override fun onResume() {
        super.onResume()

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        loadData(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
