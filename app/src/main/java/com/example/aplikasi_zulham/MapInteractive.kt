package com.example.aplikasi_zulham

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.example.aplikasi_zulham.databinding.FragmentMapInteractiveBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView

class MapInteractive : Fragment() {
    private var _binding: FragmentMapInteractiveBinding? = null
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
        _binding = FragmentMapInteractiveBinding.inflate(inflater, container, false)
        return binding.root  // Corrected this line to use the binding's root view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(com.example.aplikasi_zulham.R.id.NavButton)
        bottomNav.visibility = View.GONE
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName

        setupMap()
    }
    private lateinit var tapMarker: Marker
    private fun setupMap() {
        mapView = binding.mapView

        // Set tile source dan aktifkan kontrol multi-touch
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)

        // Inisialisasi lokasi awal (Kecamatan Pujut) dan level zoom
        val kecamatanPujut = GeoPoint(-8.899022, 116.296767)
        mapView.controller?.apply {
            setZoom(13.5)
            setCenter(kecamatanPujut)
        }

        // Tambahkan compass overlay
        compassOverlay = CompassOverlay(requireContext(), mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        // Tambahkan location overlay
        locationOverlay = MyLocationNewOverlay(mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        // URL untuk Overpass API (multi-amenity)
        val url = """
        https://overpass-api.de/api/interpreter?data=[out:json];
        (
          node["amenity"="toilets"](around:8000,-8.899022,116.296767);
          node["amenity"="restaurant"](around:8000,-8.899022,116.296767);
          node["amenity"="atm"](around:8000,-8.899022,116.296767);
          node["amenity"="cafe"](around:8000,-8.899022,116.296767);
          node["amenity"="parking"](around:8000,-8.899022,116.296767);
        );
        out;
    """.trimIndent()

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val elements = response.getJSONArray("elements")
                for (i in 0 until elements.length()) {
                    val element = elements.getJSONObject(i)
                    val lat = element.getDouble("lat")
                    val lon = element.getDouble("lon")
                    val tags = element.optJSONObject("tags")
                    val name = tags?.optString("name") ?: "Tempat"
                    val amenityType = tags?.optString("amenity") ?: "unknown"

                    val marker = Marker(mapView).apply {
                        position = GeoPoint(lat, lon)
                        title = "$name\n($amenityType)"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        val iconRes = when (amenityType) {
                            "toilets" -> R.drawable.baseline_wc_24
                            "restaurant" -> R.drawable.baseline_restaurant_24
                            "atm" -> R.drawable.baseline_local_atm_24
                            "cafe" -> R.drawable.baseline_local_cafe_24
                            "parking" -> R.drawable.baseline_local_parking_24
                            else -> R.drawable.baseline_location_pin_24
                        }

                        val drawable = ContextCompat.getDrawable(requireContext(), iconRes)?.mutate()
                        val color = when (amenityType) {
                            "toilets" -> Color.parseColor("#4CAF50")
                            "restaurant" -> Color.parseColor("#FF5722")
                            "atm" -> Color.parseColor("#3F51B5")
                            "cafe" -> Color.parseColor("#795548")
                            "parking" -> Color.parseColor("#607D8B")
                            else -> Color.parseColor("#F44336")
                        }
                        drawable?.setTint(color)
                        icon = drawable
                    }

                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            },
            { error ->
                Log.e("OverpassAPI", "Error: ${error.message}")
            }
        )

        // Tambahkan request ke queue Volley
        Volley.newRequestQueue(requireContext()).add(request)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leaks by clearing the binding
    }
}
