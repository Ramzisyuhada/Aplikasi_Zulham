package com.example.aplikasi_zulham.View

import android.graphics.BitmapFactory
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
import com.example.aplikasi_zulham.AduanKu
import com.example.aplikasi_zulham.Controller.AduanController
import kotlinx.coroutines.launch

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
        Init()

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
        beritaList.add(
            Laporan(
                "Pungutan Liar di Kawasan Tahura",
                BitmapFactory.decodeResource(resources, R.drawable.tahura1),
                "Beberapa pengunjung melaporkan adanya pungutan liar di pos penjagaan Taman Hutan Raya. Oknum yang tidak bertanggung jawab meminta sejumlah uang agar pengunjung bisa masuk tanpa tiket resmi.",
                "08:15 AM", "Bandung"
            )
        )

        beritaList.add(
            Laporan(
                "Kerusakan Fasilitas di Area Camping",
                BitmapFactory.decodeResource(resources, R.drawable.tahura2),
                "Beberapa fasilitas di area camping Tahura mengalami kerusakan akibat vandalisme. Bangku taman rusak, serta beberapa papan informasi dicoret-coret dengan cat semprot.",
                "09:30 AM", "Bandung"
            )
        )

        beritaList.add(
            Laporan(
                "Penemuan Satwa Langka di Jalur Pendakian",
                BitmapFactory.decodeResource(resources, R.drawable.tahura2),
                "Seorang pendaki berhasil mengabadikan gambar seekor burung langka yang jarang terlihat di kawasan Tahura. Ini merupakan tanda bahwa ekosistem masih terjaga dengan baik.",
                "10:45 AM", "Bandung"
            )
        )

        beritaList.add(
            Laporan(
                "Kebakaran Kecil di Pinggir Hutan",
                BitmapFactory.decodeResource(resources, R.drawable.tahura1),
                "Petugas berhasil memadamkan kebakaran kecil yang terjadi di pinggir hutan. Dugaan sementara, kebakaran berasal dari api unggun yang tidak dipadamkan dengan baik oleh pengunjung.",
                "11:10 AM", "Bandung"
            )
        )

        beritaList.add(
            Laporan(
                "Sampah Berserakan di Area Piknik",
                BitmapFactory.decodeResource(resources, R.drawable.tahura2),
                "Pengunjung mengeluhkan banyaknya sampah yang berserakan di area piknik. Sampah plastik dan sisa makanan ditinggalkan begitu saja tanpa dibuang ke tempat sampah yang tersedia.",
                "12:00 PM", "Bandung"
            )
        )
        val limitedList = beritaList.take(3)

        adapterBerita = AdapterBerita(ArrayList(limitedList)) { berita ->
        }
        binding.recyclerView.adapter = adapterBerita
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "Laporin"
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)

        bottomNav.visibility = View.VISIBLE

        val list = ArrayList<SlideModel>()
        list.add(SlideModel(R.drawable.tahura1, ScaleTypes.FIT))
        list.add(SlideModel(R.drawable.tahura2, ScaleTypes.FIT))
        binding.imageSlider.setImageList(list, ScaleTypes.FIT)


        val Controller = AduanController()

        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token  = prefs.getString("token", null)
        lifecycleScope.launch {
         val Json =    token?.let { Controller.GetAllAduan(1,it) }
        Log.d("JSON", Json?.getJSONArray("data").toString())
        }

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