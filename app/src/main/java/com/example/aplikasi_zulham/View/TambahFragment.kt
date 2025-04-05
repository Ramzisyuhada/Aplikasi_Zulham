package com.example.aplikasi_zulham.View

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.R
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_zulham.Adapter.AdapterMedia
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Model.Laporan
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import com.example.aplikasi_zulham.util.GpsHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class TambahFragment : Fragment() {

    private var _binding: FragmentTambahBinding? = null
    private val binding get() = _binding!!
    var resizedBitmap : Bitmap? = null
    private lateinit var gpsHelper: GpsHelper
    private lateinit var datalaporan: ViewModelAduan
    private lateinit var adapterBerita: AdapterMedia

    private val media = ArrayList<Laporan>()
    private val currentAduan = Aduan()

    val JenisPelanggaran = listOf("sampah berserakan","vandalisme","kerusakan fasilitas umum", "praktik illegal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun setupMap() {
        binding.mapview.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapview.setMultiTouchControls(true)
        binding.mapview.controller.setZoom(15.0)

        gpsHelper = GpsHelper(requireContext(), binding.mapview)
        gpsHelper.setupLocation(centerOnMyLocation = false)

        binding.mapview.postDelayed({
            gpsHelper.setLocationByLatLng(datalaporan.alamat.latitude, datalaporan.alamat.longitude)
        }, 1000)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        bottomNav.visibility = View.GONE


        datalaporan = ViewModelProvider(requireActivity())[ViewModelAduan::class.java]


        binding.simpan.setOnClickListener {

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)


            parentFragmentManager.beginTransaction().replace(com.example.aplikasi_zulham.R.id.Frame, HomeFragment())

            .addToBackStack(null)
            .commit()

            bottomNav.visibility = View.VISIBLE
            bottomNav.selectedItemId = R.id.home
        }
        datalaporan.Aduan.DeskripsiMasalah?.let {
            value ->
            if (value.isNotEmpty())        binding.catatanEditText.setText(value)

        }
        binding.catatanEditText.addTextChangedListener { text ->
            datalaporan.Aduan.DeskripsiMasalah = text.toString()

        }
        binding.catatanEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scroll.post {
                    binding.scroll.smoothScrollTo(0, binding.catatanEditText.bottom)
                }
            }
        }


        val Adapter_Pelanggaran = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, JenisPelanggaran
        )
        Adapter_Pelanggaran.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.PartKategori.adapter = Adapter_Pelanggaran
        if (datalaporan.Aduan.KategoriAduan.isNotEmpty()) {
            val index = JenisPelanggaran.indexOf(datalaporan.Aduan.KategoriAduan)
            if (index != -1) {
                binding.PartKategori.setSelection(index)
            }
        }
        binding.PartKategori.onItemSelectedListener =  object  :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                datalaporan.Aduan.KategoriAduan = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        if (datalaporan.alamat.OnMap) {
            binding.lok.visibility = View.VISIBLE
            binding.buttonlokasi.visibility = View.GONE
            binding.alertorange.visibility = View.GONE

            setupMap()

            binding.Latitude.text = datalaporan.alamat.latitude.toString()
            binding.Longitude.text = datalaporan.alamat.longitude.toString()

            binding.ubahlokasi.setOnClickListener {

                parentFragmentManager.beginTransaction()
                    .replace(com.example.aplikasi_zulham.R.id.Frame, GpsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }else{
            binding.lok.visibility = View.GONE

        }
        Log.i("Aduan", "Data gambar: ${datalaporan.media.value}")

        initRecycler()


        if ( datalaporan.media.value!!.isNotEmpty() ) {
            binding.recyclerView.visibility = View.VISIBLE



        } else {

            binding.recyclerView.visibility = View.GONE
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahBinding.inflate(inflater, container, false)

        binding.buttonmedia.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.aplikasi_zulham.R.id.Frame, CameraFragment())
                .addToBackStack(null)
                .commit()



        }

        binding.buttonlokasi.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
            bottomNav.visibility = View.GONE

            parentFragmentManager.beginTransaction()
                .replace(com.example.aplikasi_zulham.R.id.Frame, GpsFragment())
                .addToBackStack(null)
                .commit()


        }

        return binding.root
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratioBitmap = bitmap.width.toFloat() / bitmap.height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        val finalWidth = if (ratioMax > ratioBitmap) {
            (maxHeight * ratioBitmap).toInt()
        } else {
            maxWidth
        }

        val finalHeight = if (ratioMax > ratioBitmap) {
            maxHeight
        } else {
            (maxWidth / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }

    private fun initRecycler(){
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val resizedBitmap = datalaporan.Image?.let { resizeBitmap(it,800,800) }
        val adapter = datalaporan.media.value ?: ArrayList()
        resizedBitmap?.let {

            value ->
            val aduan = Aduan()
            aduan.Gambar.add(value)
            adapter.add(aduan)
            datalaporan.Image = null
        }

        adapterBerita = AdapterMedia(ArrayList(adapter), { SelectedImage ->


        }, { aduan, position ->
            val updatedList = datalaporan.media.value?.toMutableList()

            updatedList?.removeAt(position)
            datalaporan.media.value = updatedList as ArrayList<Aduan>?

            adapterBerita.notifyItemRemoved(position)
            if (datalaporan.media.value!!.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE

            }

        })
        datalaporan.media.observe(viewLifecycleOwner) { updatedList ->
            adapterBerita.updateData(updatedList.take(5))
        }
        binding.recyclerView.adapter = adapterBerita

    }
//    private fun initRecycler() {
//        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        Log.i("Aduan", "${ datalaporan.media.value?.count()}")
//
//         resizedBitmap = datalaporan.Image?.let { resizeBitmap(it, 800, 800) }
//
//        val currentList = datalaporan.media.value ?: ArrayList()
//
//        val isAlreadyAdded = currentList.any { aduan ->
//            aduan.Gambar.any { it.sameAs(resizedBitmap) }
//        }
//        if (!isAlreadyAdded && resizedBitmap != null) {
//            val newAduan = Aduan()
//            newAduan.Gambar.add(resizedBitmap!!)
//            currentList.add(newAduan)
//            datalaporan.media.value = currentList
//        }
//
//        val limitedList = datalaporan.media.value?.take(5)
//        adapterBerita = AdapterMedia(ArrayList(limitedList), { selectedImage ->
//        }, { aduan,position ->
//            val updatedList = datalaporan.media.value?.toMutableList()
//
//            updatedList?.removeAt(position)
//            datalaporan.media.value = updatedList as ArrayList<Aduan>?
//
//
//            if (updatedList.isNullOrEmpty()) {
//                binding.recyclerView.visibility = View.GONE
//            } else {
//                binding.recyclerView.visibility = View.VISIBLE
//            }
//
//            adapterBerita.notifyItemRemoved(position)
//
//        })
//        datalaporan.media.observe(viewLifecycleOwner) { updatedList ->
//            adapterBerita.updateData(updatedList.take(5))
//        }
//
//
//        binding.recyclerView.adapter = adapterBerita
//
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}