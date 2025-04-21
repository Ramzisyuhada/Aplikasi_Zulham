package com.example.aplikasi_zulham.View

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.aplikasi_zulham.ListAduan

import com.example.aplikasi_zulham.R
import com.example.aplikasi_zulham.databinding.FragmentAdminBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * A simple [Fragment] subclass.
 * Use the [Admin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Admin : Fragment() {


    private var _binding : FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var compassOverlay: CompassOverlay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminBinding.inflate(inflater,container,false)
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName
        setupMap()
        return _binding!!.root;
    }

    private lateinit var tapMarker: Marker

    private fun setupMap() {
            mapView = binding.mapview

            // Set tile source dan aktifkan kontrol multi-touch
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapView.setMultiTouchControls(true)

            // Inisialisasi lokasi awal (Kecamatan Pujut) dan level zoom
            val kecamatanPujut = GeoPoint(-8.899022, 116.296767)
            mapView.controller?.apply {
                setZoom(13.5)
                setCenter(kecamatanPujut)
            }

            compassOverlay = CompassOverlay(requireContext(), mapView)
            compassOverlay.enableCompass()
            mapView.overlays.add(compassOverlay)

            locationOverlay = MyLocationNewOverlay(mapView)
            locationOverlay.enableMyLocation()
            mapView.overlays.add(locationOverlay)

        val locations = listOf(
            GeoPoint(-8.908079, 116.273970), // Kuta
            GeoPoint(-8.901134, 116.355331), // Bukit Merese
            GeoPoint(-8.901345, 116.365467), // Pantai Tanjung Aan
            GeoPoint(-8.882056, 116.323889)  // Sirkuit Mandalika
        )

        for (point in locations) {
            val marker = Marker(mapView)
            marker.position = point
            marker.setOnMarkerClickListener { m, _ ->
                replaceFragment(ListAduan())
                true
            }
            mapView.overlays.add(marker)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Frame, fragment)
            .commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    companion object {
        private const val TAG = "Admin"
    }
}