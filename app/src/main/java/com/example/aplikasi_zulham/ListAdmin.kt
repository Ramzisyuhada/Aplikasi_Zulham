package com.example.aplikasi_zulham

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aplikasi_zulham.Controller.AduanController
import com.example.aplikasi_zulham.View.AduanAdmin
import com.example.aplikasi_zulham.databinding.FragmentListtAduanBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TAG = "ListAdmin"

class ListAdmin : Fragment() {

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
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val controller = AduanController()
                val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val token = prefs.getString("token", null)
                val destinasiId = prefs.getInt("DestinasiID", -1)

                if (token.isNullOrEmpty() || destinasiId <= 0) {
                    Log.e(TAG, "Token/DestinasiID invalid. token=$token, destinasiId=$destinasiId")
                    showEmpty()
                    return@launch
                }

                val json: JSONObject = controller.GetAllAduanAdmin(destinasiId, token)
                Log.d(TAG, "Response: $json")

                // 1) Cek status response
                val statusResp = json.optString("status", "")
                if (statusResp != "success") {
                    Log.e(TAG, "API not success. status=$statusResp message=${json.optString("message")}")
                    showEmpty()
                    return@launch
                }

                // 2) Ambil "data" aman: bisa Array, Object, atau null
                val dataNode = json.opt("data")
                val jsonArray: JSONArray = when (dataNode) {
                    is JSONArray -> dataNode
                    is JSONObject -> JSONArray().put(dataNode)
                    else -> JSONArray()
                }

                if (jsonArray.length() == 0) {
                    showEmpty()
                    return@launch
                } else {
                    binding.emptyStateGroup.visibility = View.GONE
                }

                // Bersihkan container dulu (jika ada sisa)
                binding.cardContainer.removeAllViews()

                var sudah = 0
                val total = jsonArray.length()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(i) ?: continue
                    val complaint = item.optString("complaint", "-")
                    val idComplaint = item.optInt("id_complaint", -1)
                    val complaintDate = item.optString("complaint_date", "")
                    val mediaArray = item.optJSONArray("media") ?: JSONArray()

                    ambilGambarDariMedia(mediaArray) { bitmap ->
                        if (!isAdded) return@ambilGambarDariMedia
                        val card = inflater.inflate(R.layout.card_aduan, binding.cardContainer, false)

                        card.findViewById<TextView>(R.id.KeluhanID)?.text = complaint
                        card.findViewById<ImageView>(R.id.IconAduanID)?.setImageBitmap(bitmap)
                        card.findViewById<TextView>(R.id.LamaUploadID)?.text =
                            if (complaintDate.isNotBlank()) convertToCustomFormatSafe(complaintDate) else "-"

                        // Tambahkan ke container
                        binding.cardContainer.addView(card)

                        // Klik → buka detail admin
                        card.setOnClickListener {
                            if (idComplaint > 0) {
                                val bundle = Bundle().apply { putInt("id_complaint", idComplaint) }
                                val aduan = AduanAdmin().apply { arguments = bundle }
                                replaceFragment(aduan)
                            }
                        }

                        sudah++
                        if (sudah == total && dialogloading.isShowing) {
                            dialogloading.dismiss()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memuat daftar aduan", e)
                showEmpty()
            } finally {
                if (dialogloading.isShowing) dialogloading.dismiss()
            }
        }

        return binding.root
    }

    private fun showEmpty() {
        if (!isAdded) return
        binding.emptyStateGroup.visibility = View.VISIBLE
        binding.cardContainer.removeAllViews()
    }

    private fun replaceFragment(fragment: Fragment) {
        if (!isAdded) return
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToCustomFormatSafe(input: String): String {
        return try {
            val inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dt = LocalDateTime.parse(input, inFmt)
            val outFmt = DateTimeFormatter.ofPattern("EEEE MMMM d | hh.mm a", Locale("id", "ID"))
            dt.format(outFmt)
        } catch (e: Exception) {
            Log.w(TAG, "Format tanggal tidak sesuai: $input", e)
            input
        }
    }

    private fun ambilGambarDariMedia(
        mediaArray: JSONArray,
        onBitmapReady: (Bitmap) -> Unit
    ) {
        var imagePath: String? = null

        for (j in 0 until mediaArray.length()) {
            val mediaItem = mediaArray.optJSONObject(j) ?: continue
            val mediaType = mediaItem.optString("media_type", "")
            val path = mediaItem.optString("path", "")
            if (mediaType == "image" && (path.endsWith(".jpg", true) || path.endsWith(".png", true))) {
                imagePath = path
                break
            }
        }

        if (imagePath.isNullOrBlank()) {
            onBitmapReady(BitmapFactory.decodeResource(resources, R.drawable.tahura1))
            return
        }

        val fullUrl = "http://13.216.4.3$imagePath"

        Glide.with(this)
            .asBitmap()
            .load(fullUrl)
            .error(R.drawable.tahura1)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onBitmapReady(resource)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Log.e(TAG, "Gagal load gambar dari $fullUrl")
                    onBitmapReady(BitmapFactory.decodeResource(resources, R.drawable.tahura1))
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        val text = requireActivity().findViewById<TextView>(R.id.head)
        text.text = "Aduan Terbaru"
        bottomNav.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
