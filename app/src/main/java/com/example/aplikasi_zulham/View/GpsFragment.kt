package com.example.aplikasi_zulham.View

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.ViewModel.ViewModelAduan
import com.example.aplikasi_zulham.databinding.FragmentGpsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.util.Locale

class GpsFragment : Fragment(), MapListener {

    private var _binding: FragmentGpsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var compassOverlay: CompassOverlay

    private val PERMISSIONS_REQUEST_CODE = 100
    private var lag = 0.0
    private var long = 0.0

    private lateinit var  datalaporan : ViewModelAduan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGpsBinding.inflate(inflater, container, false)

        Configuration.getInstance().userAgentValue = requireContext().packageName

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datalaporan = ViewModelProvider(requireActivity()).get(ViewModelAduan::class.java)

        binding.lokasi.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("show_cardview", true)
            bundle.putDouble("lag", lag)
            bundle.putDouble("long", long)

            datalaporan.alamat.OnMap = true
            requireActivity().supportFragmentManager.setFragmentResult("request_key", bundle)

            parentFragmentManager.beginTransaction()
                .replace(R.id.Frame, TambahFragment())
                .addToBackStack(null)
                .commit()

        }
        if (hasPermissions()) {
            setupMap()
        } else {
            requestPermissions()
        }
    }

    private fun hasPermissions(): Boolean {
        val context = requireContext()
        return listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
        ).all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ), PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            setupMap()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission denied, cannot load map",
                Toast.LENGTH_SHORT
            ).show()
        }
    }




    private lateinit var tapMarker: Marker
    private lateinit var personMarker: Marker


    private fun setupMap() {
        mapView = binding.mapview
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        mapView.overlays.add(locationOverlay)

        compassOverlay = CompassOverlay(requireContext(), mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        locationOverlay.runOnFirstFix {
            activity?.runOnUiThread {
                mapView.controller.setCenter(locationOverlay.myLocation)
            }
        }

        tapMarker = Marker(mapView)
        tapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(tapMarker)

        val polygon = Polygon()
        polygon.fillPaint.color = Color.argb(50, 255, 0, 0) // transparan merah
        polygon.outlinePaint.color = Color.RED

        polygon.points = listOf(
            GeoPoint(-6.9620, 107.7540), // Titik A
            GeoPoint(-6.9645, 107.7560), // Titik B
            GeoPoint(-6.9655, 107.7590), // Titik C
            GeoPoint(-6.9670, 107.7610), // Titik D
            GeoPoint(-6.9685, 107.7650), // Titik E
            GeoPoint(-6.9700, 107.7680), // Titik F
            GeoPoint(-6.9705, 107.7700), // Titik G
            GeoPoint(-6.9700, 107.7730), // Titik H
            GeoPoint(-6.9680, 107.7750), // Titik I
            GeoPoint(-6.9655, 107.7760), // Titik J
            GeoPoint(-6.9635, 107.7735), // Titik K
            GeoPoint(-6.9620, 107.7540)  // Kembali ke titik awal untuk menutup polygon
        )

        polygon.title = "Area Kabupaten Merah"
        mapView.overlays.add(polygon)

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    val lat = "%.6f".format(p.latitude)
                    val lon = "%.6f".format(p.longitude)
                    lag = p.latitude
                    long = p.longitude
                    datalaporan.alamat.latitude = lag
                    datalaporan.alamat.longitude = long
                    Log.i("Map", "Tapped at: $lat, $lon")

                    tapMarker.position = p
                    tapMarker.title = "Lokasi yang dipilih"
                    tapMarker.snippet = "Lat: $lat, Lon: $lon"
                    mapView.invalidate()
                    binding.Latitude.text = lat
                    binding.Longitude.text = lon
                    binding.alamat.text = getAddressFromCoordinates(p.latitude, p.longitude)
                    // view?.findViewById<TextView>(R.id.textView)?.text = "Tapped: $lat, $lon"

                    Toast.makeText(requireContext(), "Tapped: $lat, $lon", Toast.LENGTH_SHORT)
                        .show()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        })

        mapView.overlays.add(mapEventsOverlay)

        mapView.addMapListener(DelayedMapListener(this, 1000))
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


    override fun onScroll(event: ScrollEvent?): Boolean {
        updateLatLng()
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        updateLatLng()
        return true
    }

    private fun updateLatLng() {
        val center = mapView.mapCenter
        val lat = "%.6f".format(center.latitude)
        val lon = "%.6f".format(center.longitude)
        Log.i("Map", "Center: $lat, $lon")
        binding.Latitude.text = lat
        binding.Longitude.text = lon
        lag = center.latitude
        long = center.longitude
        datalaporan.alamat.latitude = lag
        datalaporan.alamat.longitude = long
        binding.alamat.text = getAddressFromCoordinates(center.latitude,center.longitude)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}