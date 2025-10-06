package com.example.aplikasi_zulham.View

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Adapter.MediaAdapter
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.databinding.FragmentAduanBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Aduan : Fragment() {

    private var _binding: FragmentAduanBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null

    // Opsi status harus sama dengan enum backend
    private val statusOptions = listOf("pending", "proses", "selesai")
    private val displayLabels by lazy {
        statusOptions.map { it.replaceFirstChar { c -> c.titlecase(Locale("id", "ID")) } }
    }

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
    ): View {
        _binding = FragmentAduanBinding.inflate(inflater, container, false)

        // Siapkan spinner readonly
        val spinner = binding.spinnerStatusAduan
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.isEnabled = false
        spinner.isClickable = false

        // Ambil argumen id_complaint
        val idComplaintArg = arguments?.getInt("id_complaint", -1) ?: -1
        Log.d("ID_COMPLAINT", idComplaintArg.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            val controller = AduanController()
            val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val token = prefs.getString("token", null)
            val role = prefs.getString("role", null)

            if (idComplaintArg <= 0 || token.isNullOrEmpty()) {
                Log.e("Aduan", "Arg id_complaint/token tidak valid")
                return@launch
            }

            try {
                val jsonObject = controller.GetAduanById(idComplaintArg, token)
                val dataObject = jsonObject.getJSONObject("data")
                val userObject = dataObject.getJSONObject("user")

                val username = userObject.getString("username")
                val complaint = dataObject.getString("complaint")
                val complaintId = dataObject.getInt("id_complaint")
                val complaintDate = dataObject.getString("complaint_date")
                val mediaArray = dataObject.getJSONArray("media")
                val statusRaw = dataObject.getString("status") // "pending" | "proses" | "selesai"

                // Media list (gambar/video) untuk slider
                val mediaList = ArrayList<com.example.aplikasi_zulham.Model.Aduan>()
                val baseUrl = "http://13.216.4.3"
                for (i in 0 until mediaArray.length()) {
                    val mediaObj = mediaArray.getJSONObject(i)
                    val mediaPath = mediaObj.getString("path")
                    val mediaType = mediaObj.getString("media_type")
                    val fullUrl = baseUrl + mediaPath
                    val isVideo = mediaType == "video"
                    mediaList.add(com.example.aplikasi_zulham.Model.Aduan(File(fullUrl), isVideo))
                }

                binding.imageSlider.adapter = MediaAdapter(mediaList)
                binding.NamapenggunaID.text = username
                binding.DeskripsiTourisgGuidIDs.text = complaint
                binding.NomerAduanID.text = "#%08d".format(complaintId)
                binding.TanggalAduanID.text = formatTanggal(complaintDate)

                // Set spinner sesuai status dari server (fallback ke index 0 jika tidak dikenal)
                val idx = statusOptions.indexOf(statusRaw.lowercase(Locale.ROOT)).let { if (it >= 0) it else 0 }
                spinner.setSelection(idx, false)

            } catch (e: Exception) {
                Log.e("Aduan", "Gagal memuat detail aduan", e)
            }
        }

        return binding.root
    }

    private fun formatTanggal(tanggalInput: String): String {
        val locale = Locale("id", "ID")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy HH.mm", locale)
        val date = inputFormat.parse(tanggalInput)
        return outputFormat.format(date!!)
    }

    companion object {
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
