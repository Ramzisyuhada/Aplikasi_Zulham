package com.example.aplikasi_zulham.View

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aplikasi_zulham.Adapter.MediaAdapter
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.databinding.FragmentAduanBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [Aduan.newInstance] factory method to
 * create an instance of this fragment.
 */
class AduanAdmin : Fragment() {
    private var _binding: FragmentAduanBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null
    private var skipFirstSpinnerCallback = true


    private val statusOptions = listOf("pending", "proses", "selesai")
    private val displayLabels by lazy {
        statusOptions.map { it.replaceFirstChar { c -> c.titlecase(Locale("id", "ID")) } }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAduanBinding.inflate(inflater, container, false)
        val spinner = binding.spinnerStatusAduan
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.isEnabled = false
        spinner.isClickable = false
        val idComplaintArg = arguments?.getInt("id_complaint", -1) ?: -1

        lifecycleScope.launch {
            val id_complaint = arguments?.getInt("id_complaint")
            Log.d("ID_COMPLAINT", id_complaint.toString())

            val controller = AduanController()
            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val token = prefs.getString("token", null)
            val role = prefs.getString("role", null)

            val isAdmin = role.equals("admin", ignoreCase = true)
            spinner.isEnabled = isAdmin
            spinner.isClickable = isAdmin

            id_complaint?.let { id ->
                token?.let { token ->
                    val jsonObject = controller.GetAduanByIdAdmin(id, token)

                    val dataObject = jsonObject.getJSONObject("data")

                    // ✅ Fix bagian ini
                    val username = dataObject.getString("user")

                    val complaint = dataObject.getString("complaint")
                    val idComplaint = dataObject.getInt("id_complaint")
                    val complaintDate = dataObject.getString("complaint_date")
                    val mediaArray = dataObject.getJSONArray("media")
                    val statusRaw = dataObject.getString("status") // "pending" | "proses" | "selesai"

                    Log.d("MEDIA_ARRAY", jsonObject.toString())

                    val mediaList = ArrayList<com.example.aplikasi_zulham.Model.Aduan>()
                    val baseUrl = "http://13.216.4.3/storage/"

                    for (i in 0 until mediaArray.length()) {
                        val mediaObject = mediaArray.getJSONObject(i)
                        val path = mediaObject.getString("path")
                        val type = mediaObject.getString("media_type")

                        val fullUrl = baseUrl + path
                        val isVideo = type == "video"
                        val aduan = com.example.aplikasi_zulham.Model.Aduan(File(fullUrl), isVideo)
                        Log.d("URL",fullUrl)

                        mediaList.add(aduan)
                    }

                    binding.imageSlider.adapter = MediaAdapter(mediaList)
                    binding.NamapenggunaID.text = username
                    binding.DeskripsiTourisgGuidIDs.text = complaint
                    binding.NomerAduanID.text = "#0000$idComplaint"
                    binding.TanggalAduanID.text = formatTanggal(complaintDate)

                    val idx = statusOptions.indexOf(statusRaw.lowercase(Locale.ROOT)).let { if (it >= 0) it else 0 }
                    skipFirstSpinnerCallback = true

                    spinner.setSelection(idx, false)


                    if (isAdmin) {
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>, view: View?, position: Int, id: Long
                            ) {
                                if (skipFirstSpinnerCallback) {
                                    skipFirstSpinnerCallback = false
                                    return
                                }
                                val newStatus = statusOptions[position]
                                if (newStatus != statusRaw) {
                                    lifecycleScope.launch {
                                        controller.UpdateIoByAdmin(id_complaint, token, newStatus)
                                    }
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                    } else {
                        // Non-admin: read-only
                        spinner.onItemSelectedListener = null
                    }

                }
            }
        }

        return binding.root
    }



    fun formatTanggal(tanggalInput: String): String {
        val locale = Locale("id", "ID")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        val outputFormat = SimpleDateFormat("dd MMMM yyyy HH.mm", locale)
        val date = inputFormat.parse(tanggalInput)
        return outputFormat.format(date!!)
    }

}