package com.example.aplikasi_zulham.View

import Alamat
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_zulham.Adapter.AdapterMedia
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.Model.Aduan
import com.example.aplikasi_zulham.Model.Laporan
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import com.example.aplikasi_zulham.util.GpsHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.io.File
import java.io.IOException
import java.util.Locale

class TambahFragment : Fragment() {

    private var _binding: FragmentTambahBinding? = null
    private val binding get() = _binding!!
    var resizedBitmap : Bitmap? = null
    private lateinit var gpsHelper: GpsHelper
    private lateinit var datalaporan: ViewModelAduan
    private lateinit var adapterBerita: AdapterMedia

    private val media = ArrayList<Laporan>()
    private val currentAduan = Aduan()


    //Var Global

    var Lat:Double = 0.0
    var Long : Double = 0.0
    var File = ArrayList<File>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        datalaporan = ViewModelProvider(requireActivity())[ViewModelAduan::class.java] // ⬅️ pindah ke sini

    }

    private fun setupMap() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        bottomNav.visibility = View.GONE
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 101)
            return
        }

        val  gps = GpsHelper(requireContext())
        gps.getLocation{lat, lon ->
            Log.i("LOKASI", "Lokasi diterima - Lat: $lat, Lon: $lon")
            Log.i("LOKASI", "Alamat hasil geocoder: ${getAddressFromCoordinates(lat, lon)}")

            this.Lat = lat
            this.Long = lon
        }
        datalaporan = ViewModelProvider(requireActivity())[ViewModelAduan::class.java]

        val dialogSucces = LayoutInflater.from(requireContext()).inflate(R.layout.alertdialog_succes_ulasan, null)

        val dialog1 = AlertDialog.Builder(requireContext())
            .setView(dialogSucces)
            .create()
        binding.Tambah.setOnClickListener {
            dialog1.show()
            dialogSucces.findViewById<TextView>(R.id.TextDialog1).text = "Terima kasih atas aduan Anda."
            dialogSucces.findViewById<Button>(R.id.ok).setOnClickListener {
                dialog1.dismiss()
                val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val token:String  = prefs.getString("token", null).toString()
                Log.d("TOKEN",token.toString())
                val Lokasi = Alamat(this.Lat,this.Long)
                val Controller = AduanController()
                val DeskripsiMasalah = binding.catatanEditText.text.toString()
                val alamat =    getAddressFromCoordinates(this.Lat,this.Long)

                val Aduan = Aduan(DeskripsiMasalah,Lokasi,datalaporan.ListFile)
                lifecycleScope.launch {
                    Controller.AddAduan(Aduan,token,alamat)
                }
                parentFragmentManager.beginTransaction()
                    .replace(com.example.aplikasi_zulham.R.id.Frame, HomeFragment())
                    .addToBackStack(null)
                    .commit()
                datalaporan.clear()
            }



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









    }
    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) ?: "Alamat tidak ditemukan"
            } else {
                "Alamat tidak ditemukan"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Gagal mendapatkan alamat"
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahBinding.inflate(inflater, container, false)

        binding.buttonmedia.setOnClickListener {

            val bottomSheet = MyBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)


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

    private fun initRecycler() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = datalaporan.media.value ?: ArrayList()

        adapterBerita = AdapterMedia(ArrayList(adapter), { selectedImage, _ ->

            if (selectedImage.Gambar.isNotEmpty()) {
                binding.img.setImageBitmap(resizeBitmap(selectedImage.Gambar.last(), 800, 800))
                binding.img.visibility = View.VISIBLE
            }

            if (selectedImage.isVideo && selectedImage.videoFile != null) {
                binding.video.apply {
                    visibility = View.VISIBLE
                    setVideoPath(selectedImage.videoFile!!.absolutePath)
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        start()
                    }
                }
            } else {
                binding.video.stopPlayback()

                binding.video.visibility = View.GONE
            }

        }, { aduan, position ->
            val updatedList = datalaporan.media.value?.toMutableList()
            updatedList?.removeAt(position)
            datalaporan.media.value = updatedList as ArrayList<Aduan>?
            datalaporan.ListFile.removeAt(position)
            adapterBerita.notifyItemRemoved(position)

            if (updatedList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.img.visibility = View.GONE
                binding.video.stopPlayback()

                binding.video.visibility = View.GONE
            }
        })

        datalaporan.media.observe(viewLifecycleOwner) { updatedList ->
            adapterBerita.updateData(updatedList.take(3))
        }

        binding.recyclerView.adapter = adapterBerita
    }


    override fun onResume() {
        super.onResume()
        val mediaList = datalaporan.media.value ?: ArrayList()

        if (datalaporan.Image != null) {
            val imageAlreadyAdded = datalaporan.media.value?.any { aduan ->
                aduan.Gambar.isNotEmpty() && aduan.Gambar.last().sameAs(datalaporan.Image)
            } == true

            if (!imageAlreadyAdded) {
                val aduan = Aduan().apply {
                    Gambar.add(datalaporan.Image!!)
                    isVideo = datalaporan.isVideo
                    videoFile = datalaporan.NamaFile
                    datalaporan.NamaFile?.let { datalaporan.ListFile.add(it) }
                }

                mediaList.add(aduan)
                datalaporan.media.value = mediaList
            }
            datalaporan.NamaFile?.let { datalaporan.ListFile.add(it) }
            binding.img.setImageBitmap(resizeBitmap(datalaporan.Image!!, 800, 800))

            datalaporan.Image = null
            datalaporan.NamaFile = null
            datalaporan.isVideo = false

            initRecycler()
        }


        if (mediaList.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.img.visibility = View.VISIBLE

            val lastItem = mediaList.last()
            if (lastItem.isVideo && lastItem.videoFile != null) {
                binding.video.apply {
                    visibility = View.VISIBLE
                    setVideoPath(lastItem.videoFile!!.absolutePath)
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        start()
                    }
                }
            } else {
                binding.video.stopPlayback()
                binding.video.visibility = View.GONE
            }
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.img.visibility = View.GONE
            binding.video.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}