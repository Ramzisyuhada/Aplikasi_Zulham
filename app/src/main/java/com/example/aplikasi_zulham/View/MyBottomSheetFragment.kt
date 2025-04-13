package com.example.aplikasi_zulham.View

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var selectedImageBitmap: Bitmap
    private lateinit var laporan: ViewModelAduan

    // Untuk ActivityResultLauncher untuk membuka galeri
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    try {
                        // Mendapatkan bitmap dari galeri
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        if (bitmap != null) {
                            // Menyimpan gambar dalam ViewModelAduan
                            laporan.Image = bitmap
                            selectedImageBitmap = bitmap

                            // Log untuk memastikan bitmap berhasil dipilih
                            Log.i("ImageSelected", "Gambar berhasil dipilih dan disimpan ke ViewModel.")

                        } else {
                            Log.i("Kosong", "Bitmap tidak ditemukan.")
                        }
                    } catch (e: Exception) {
                        Log.e("Error", "Error saat mengambil gambar: ${e.message}")
                    }
                }
            }
            dismiss()
        }

    // Launcher untuk meminta izin READ_EXTERNAL_STORAGE


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mendapatkan ViewModelAduan
        laporan = ViewModelProvider(requireActivity()).get(ViewModelAduan::class.java)

        val btnGaleri = view.findViewById<LinearLayout>(R.id.btnGaleri)
        val btnKamera = view.findViewById<LinearLayout>(R.id.btnKamera)

        btnGaleri.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        // Aksi saat memilih kamera (masih perlu diimplementasikan di CameraFragment)
        btnKamera.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.aplikasi_zulham.R.id.Frame, CameraFragment())
                .addToBackStack(null)
                .commit()
            dismiss()
        }
    }
}

