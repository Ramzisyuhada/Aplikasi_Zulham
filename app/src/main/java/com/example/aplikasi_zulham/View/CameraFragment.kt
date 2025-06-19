package com.example.aplikasi_zulham.View

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.video.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private val CAMERA_PERMISSION_CODE = 1001
    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var laporan: ViewModelAduan
    private val tambahfragment = TambahFragment()
    private var recordIndicator: View? = null

    private var pressStartTime: Long = 0
    private var isLongPress = false
    private val LONG_PRESS_THRESHOLD = 500L
    private val handler = Handler()

    private lateinit var cameraExecutor: ExecutorService



    private var recordTimer: TextView? = null
    private var recordStartTime: Long = 0
    private val timerHandler = Handler()
    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsedSeconds = ((System.currentTimeMillis() - recordStartTime) / 1000).toInt()
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            recordTimer?.text = String.format("%02d:%02d", minutes, seconds)
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordIndicator = view.findViewById(R.id.recordIndicator)
        recordTimer = view.findViewById(R.id.recordTimer)

        previewView = view.findViewById(R.id.viewFinder)
        val btnCapture: Button = view.findViewById(R.id.btnCapture)
        laporan = ViewModelProvider(requireActivity()).get(ViewModelAduan::class.java)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.NavButton)
        bottomNav.visibility = View.GONE
        btnCapture.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressStartTime = System.currentTimeMillis()
                    isLongPress = false
                    handler.postDelayed({
                        isLongPress = true
                        startRecording()
                    }, LONG_PRESS_THRESHOLD)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacksAndMessages(null)
                    if (!isLongPress) {
                        takePhoto()
                    } else {
                        stopRecording()
                    }
                    true
                }

                else -> false
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_PERMISSION_CODE
            )
        } else {
            setupCamera()
        }


        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture?.addListener({
            val cameraProvider = cameraProviderFuture?.get()
            viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    startCamera(cameraProvider)
                }
            })
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider?) {
        val viewFinder = previewView ?: return

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(viewFinder.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )
        } catch (e: Exception) {
            Log.e("CameraFragment", "Gagal memulai kamera", e)
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    laporan.Image = bitmap
                    image.close()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.Frame, tambahfragment)
                        .addToBackStack(null)
                        .commit()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Gagal mengambil foto", exception)
                }
            }
        )
    }

    private fun startRecording() {
        val videoCapture = videoCapture ?: return

        // Tampilkan dan animasikan indikator merah
        recordIndicator?.apply {
            visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                duration = 500
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
            tag = animator // simpan animator untuk nanti diberhentikan
        }

        val file = File(requireContext().cacheDir, "video_${System.currentTimeMillis()}.mp4")
        val outputOptions = FileOutputOptions.Builder(file).build()
        recording = videoCapture.output
            .prepareRecording(requireContext(), outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(requireContext())) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        Toast.makeText(requireContext(), "Mulai merekam", Toast.LENGTH_SHORT).show()

                        handler.postDelayed({
                            stopRecording()
                        }, 20_000)

                        recordTimer?.apply {
                            visibility = View.VISIBLE
                            text = "00:00"
                        }
                        recordStartTime = System.currentTimeMillis()
                        timerHandler.post(timerRunnable)

                    }

                    is VideoRecordEvent.Finalize -> {
                        Toast.makeText(
                            requireContext(),
                            "Rekaman selesai: ${file.absolutePath}",
                            Toast.LENGTH_SHORT
                        ).show()
                        laporan.Video = file
                        Log.i("Format File", file.name.substringAfter('.', ""))

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Frame, tambahfragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }



    }



    private fun stopRecording() {
        recording?.stop()
        recording = null

        // Hentikan animasi dan sembunyikan indikator
        (recordIndicator?.tag as? ObjectAnimator)?.cancel()
        recordIndicator?.visibility = View.GONE

        // Hentikan dan sembunyikan timer
        timerHandler.removeCallbacks(timerRunnable)
        recordTimer?.visibility = View.GONE
    }


    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera()
            } else {
                Toast.makeText(requireContext(), "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        previewView = null
        cameraProviderFuture?.get()?.unbindAll()
        cameraExecutor.shutdown()
    }
}
