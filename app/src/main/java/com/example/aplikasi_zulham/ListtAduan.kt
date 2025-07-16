package com.example.aplikasi_zulham

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListtAduanBinding.inflate(inflater, container, false)

        val dialogloading = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        lifecycleScope.launch {
            val controller = AduanController()
            val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val token = prefs.getString("token", null)
            val json = token?.let { controller.GetAllAduan(1, it) }
            val jsonArray = json?.getJSONArray("data")

            if (jsonArray == null || jsonArray.length() == 0) {
                binding.emptyStateGroup.visibility = View.VISIBLE
                dialogloading.dismiss()
                return@launch
            } else {
                binding.emptyStateGroup.visibility = View.GONE
            }

            var selesai = 0
            val total = jsonArray.length()

            for (i in 0 until total) {
                val item = jsonArray.getJSONObject(i)
                val complaint = item.getString("complaint")
                val idComplaint = item.getInt("id_complaint")
                val complaintDate = item.getString("complaint_date")
                val mediaArray = item.getJSONArray("media")

                ambilGambarDariMedia(mediaArray) { bitmap ->
                    val view = inflater.inflate(R.layout.card_aduan, binding.cardContainer, false)
                    view.findViewById<TextView>(R.id.KeluhanID).text = complaint
                    view.findViewById<ImageView>(R.id.IconAduanID).setImageBitmap(bitmap)
                    view.findViewById<TextView>(R.id.LamaUploadID).text = convertToCustomFormat(complaintDate)
                    binding.cardContainer.addView(view)

                    view.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("id_complaint", idComplaint)
                        }
                        val aduan = Aduan().apply { arguments = bundle }
                        replaceFragment(aduan)
                    }

                    selesai++
                    if (selesai == total) {
                        dialogloading.dismiss()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToCustomFormat(input: String): String {
        val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(input, formatterInput)
        val formatterOutput = DateTimeFormatter.ofPattern("EEEE MMMM d | hh.mm a", Locale("id", "ID"))
        return dateTime.format(formatterOutput)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        val text = requireActivity().findViewById<TextView>(R.id.head)
        text.text = "Aduan Terbaru"
        bottomNav.visibility = View.GONE
    }

    fun convertToAmPm(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time24)
        return outputFormat.format(date)
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
            val fullUrl = "http://54.206.192.90:8080$imagePath"

            Glide.with(requireContext())
                .asBitmap()
                .load(fullUrl)
                .error(R.drawable.tahura1)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        onBitmapReady(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Log.e("ListAduan", "Gagal load gambar dari $fullUrl")
                        onBitmapReady(BitmapFactory.decodeResource(resources, R.drawable.tahura1))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tahura1)
            onBitmapReady(defaultBitmap)
        }
    }
}
