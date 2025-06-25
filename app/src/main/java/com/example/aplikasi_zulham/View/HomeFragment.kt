package com.example.aplikasi_zulham.View

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.aplikasi_zulham.Adapter.AdapterBerita
import com.example.aplikasi_zulham.ListAduan
import com.example.aplikasi_zulham.MapInteractive
import com.example.aplikasi_zulham.Model.Laporan
import com.example.aplikasi_zulham.PembersihanUmum
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.example.aplikasi_zulham.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aplikasi_zulham.AduanKu
import com.example.aplikasi_zulham.Controller.AduanController
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterBerita: AdapterBerita
    private val beritaList = ArrayList<Laporan>()


    private lateinit var datalaporan: ViewModelAduan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        //Init()

        binding.pembersihanumum.setOnClickListener {
            replaceFragment(PembersihanUmum())
        }
        binding.aduanterterbaru.setOnClickListener {
            replaceFragment(ListAduan())
        }
        binding.Weather.setOnClickListener {
            replaceFragment(CuacaView())
        }

        binding.eco.setOnClickListener {
            replaceFragment(EcoTourismeGuide())
        }

        binding.aduan.setOnClickListener {
            replaceFragment(AduanKu())
        }


        return binding.root
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)

            .addToBackStack(null)
            .commit()
    }
    private fun Init() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        binding.zone.setOnClickListener {
            replaceFragment(MapInteractive())
        }

        val limitedList = beritaList.take(3)

        adapterBerita = AdapterBerita(ArrayList(limitedList)) { berita ->
        }


        binding.recyclerView.adapter = adapterBerita

    }

    private fun loadData() {
        val controller = AduanController()
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        lifecycleScope.launch {
            val json = token?.let { controller.GetAllAduan(1, it) }
            val dataArray = json?.getJSONArray("data")

            if (dataArray != null) {
                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val username = item.getJSONObject("user").getString("username")
                    val complaint = item.getString("complaint")
                    val tanggal = item.getString("complaint_date")
                    val jam = tanggal.split(" ")
                    val tourName = item.getJSONObject("tour").getString("tour_name")
                    val mediaArray = item.getJSONArray("media")

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

                        requireActivity().runOnUiThread {
                            adapterBerita.updateData(beritaList.take(3))
                            adapterBerita.notifyDataSetChanged()

                        }
                    }
                }
            }
        }
    }



    private fun ambilGambarDariMedia(
        mediaArray: JSONArray,
        onBitmapReady: (Bitmap) -> Unit
    ) {
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

        if (imagePath != null) {
            val fullUrl = "http://192.168.1.11:8000$imagePath" // Ganti dengan domain kamu

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
            // Kalau tidak ada jpg, pakai default
            val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tahura1)
            onBitmapReady(defaultBitmap)
        }
    }



    fun convertToAmPm(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(time24)
        return outputFormat.format(date)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapterBerita = AdapterBerita(beritaList) { laporan ->
            // Aksi klik
        }
        binding.recyclerView.adapter = adapterBerita
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "Laporin"
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)

        bottomNav.visibility = View.VISIBLE

        setupRecyclerView()
        loadData()


        val list = ArrayList<SlideModel>()
        list.add(SlideModel(R.drawable.tahura1, ScaleTypes.FIT))
        list.add(SlideModel(R.drawable.tahura2, ScaleTypes.FIT))
        binding.imageSlider.setImageList(list, ScaleTypes.FIT)




        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    replaceFragment(Aduan())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }


        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}