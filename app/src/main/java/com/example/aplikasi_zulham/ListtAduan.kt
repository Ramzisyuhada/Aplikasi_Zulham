package com.example.aplikasi_zulham

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.View.Aduan
import com.example.aplikasi_zulham.databinding.FragmentListtAduanBinding
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListtAduan.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListAduan : Fragment() {
    private var _binding: FragmentListtAduanBinding? = null
    private val binding get() = _binding!!

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListtAduanBinding.inflate(inflater,container,false)
//        for (i in 1..5) {
//            val view = inflater.inflate(R.layout.card_aduan, binding.cardContainer, false)
//            view.findViewById<ImageView>(R.id.IconAduanID).setImageResource(R.drawable.tahura1)
//            binding.cardContainer.addView(view)
//            view.setOnClickListener {
//                replaceFragment(Aduan())
//            }
//        }


        /*
        * Mengambil Data Dari Api Dari Alamat Website
        * */


        lifecycleScope.launch {
            val Controller = AduanController()
            val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val token = prefs.getString("token", null)
            val Json = token?.let { Controller.GetAllAduan(1,it) }
            val JsonArray = Json?.getJSONArray("data")
            if(JsonArray != null){
                for(i in 0 until JsonArray.length()){
                    val Item = JsonArray.getJSONObject(i)
                    val Complaint = Item.getString("complaint")
                    val ComplaintDate = Item.getString("complaint_date")
                    val Date = ComplaintDate.split(" ")
                    val MediaArray = Item.getJSONArray("media")
                    ambilGambarDariMedia(MediaArray) { Bitmap ->
                        val view = inflater.inflate(R.layout.card_aduan, binding.cardContainer, false)
                        view.findViewById<TextView>(R.id.KeluhanID).text = Complaint
                        view.findViewById<ImageView>(R.id.IconAduanID).setImageBitmap(Bitmap)
                        binding.cardContainer.addView(view)
                        view.setOnClickListener {
                            replaceFragment(Aduan())
                        }
                    }
                }
            }
        }
        return binding.root
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)

            .addToBackStack(null)
            .commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        val text = requireActivity().findViewById<TextView>(com.example.aplikasi_zulham.R.id.head)
        text.text = "Aduan Terbaru"


        bottomNav.visibility = View.GONE
    }
/*
* Meng Convert Dari Jam 3 digit Ke PM
* */
    fun convertToAmPm(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(time24)
        return outputFormat.format(date)
    }

    /*
    * Meng Convert Gambar Ke Bitmap
    * variable FullUrl adalah Variable alamat ip Untuk menyimpan gambar dimana
    * */

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

    companion object {
        val TAG = "192.168.128.113"
    }
}