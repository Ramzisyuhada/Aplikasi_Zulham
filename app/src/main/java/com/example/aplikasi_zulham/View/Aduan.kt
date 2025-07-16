package com.example.aplikasi_zulham.View

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.aplikasi_zulham.Adapter.MediaAdapter
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.databinding.FragmentAduanBinding
import com.example.aplikasi_zulham.databinding.FragmentTambahBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Aduan.newInstance] factory method to
 * create an instance of this fragment.
 */
class Aduan : Fragment() {
    private var _binding: FragmentAduanBinding? = null
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
        _binding = FragmentAduanBinding.inflate(inflater, container, false)


        lifecycleScope.launch {


            val id_complaint = arguments?.getInt("id_complaint")
            Log.d("ID_COMPLAINT", id_complaint.toString())

            val Controller = AduanController()
            val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val token = prefs.getString("token", null)

            id_complaint?.let { id ->
                token?.let { token ->
                   val JSONObject= Controller.GetAduanById(id, token)
                    val DataOnject = JSONObject.getJSONObject("data")
                    val UserObject = DataOnject.getJSONObject("user")
                    val Username = UserObject.getString("username")
                    val Complaint = DataOnject.getString("complaint")
                    val IdComplaint = DataOnject.getInt("id_complaint")
                    val ComplaintDate = DataOnject.getString("complaint_date")
                    val Date = ComplaintDate.split(" ")
                    val MediaArray = DataOnject.getJSONArray("media")
                    // Variable Menyimpan Path Video

                    Log.d("MEDIA_ARRAY",JSONObject.toString())
                    var mediaPath: String? = null
                    var mediaType: String? = null

                    val mediaList  =  ArrayList<com.example.aplikasi_zulham.Model.Aduan>()
                    val baseUrl = "http://54.206.192.90:8080"
                    for (i in 0 until MediaArray.length()) {
                        val mediaObject = MediaArray.getJSONObject(i)
                        val mediaPath = mediaObject.getString("path")
                        val mediaType = mediaObject.getString("media_type")

                        val fullUrl = baseUrl + mediaPath

                        val isVideo = mediaType == "video"
                        val aduan = com.example.aplikasi_zulham.Model.Aduan(File(fullUrl), isVideo)

                        mediaList.add(aduan)
                    }

                    binding.imageSlider.adapter = MediaAdapter(mediaList )

                    binding.NamapenggunaID.text = Username
                    binding.DeskripsiTourisgGuidIDs.text = Complaint
                    binding.NomerAduanID.text = "#0000" + IdComplaint.toString()
                    binding.TanggalAduanID.text = formatTanggal(ComplaintDate)
                }
            }
        }
      //  binding.imageSlider.setImageList(list)
        return binding.root
    }


    fun formatTanggal(tanggalInput: String): String {
        val locale = Locale("id", "ID")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy HH.mm", locale)
        val date = inputFormat.parse(tanggalInput)
        return outputFormat.format(date!!)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AduanTerbaru.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Aduan().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}