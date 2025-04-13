package com.example.aplikasi_zulham.util

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.util.*

class GpsHelper(
    private var context: Context?,
    private val mapView: MapView?
) {

    lateinit var locationOverlay: MyLocationNewOverlay
    lateinit var compassOverlay: CompassOverlay
    lateinit var tapMarker: Marker

    constructor(context: Context?) : this(null, null) {
        this.context = context
        // Di sini kamu bisa menambahkan logika tambahan jika perlu
    }

    fun setupLocation(centerOnMyLocation: Boolean = true) {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        // GpsMyLocationProvider(context).location
       // locationOverlay.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)

        if (centerOnMyLocation) {
            locationOverlay.enableFollowLocation()
            locationOverlay.runOnFirstFix {
                (context as? Activity)?.runOnUiThread {
                    mapView?.controller?.setCenter(locationOverlay.myLocation)
                }
            }
        } else {
            locationOverlay.enableFollowLocation()
        }

        compassOverlay = CompassOverlay(context, mapView)
        compassOverlay.enableCompass()
        mapView?.overlays?.add(compassOverlay)

        tapMarker = Marker(mapView)
        tapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView?.overlays?.add(tapMarker)
    }


    fun updateTapMarker(p: GeoPoint, title: String = "Lokasi yang dipilih") {
        val lat = "%.6f".format(p.latitude)
        val lon = "%.6f".format(p.longitude)
        tapMarker.position = p
        tapMarker.title = title
        tapMarker.snippet = "Lat: $lat, Lon: $lon"
        mapView?.invalidate()
        Toast.makeText(context, "Tapped: $lat, $lon", Toast.LENGTH_SHORT).show()
    }

    fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        return try {
            val addresses: List<Address>? = geocoder?.getFromLocation(latitude, longitude, 1)
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

    fun setLocationByLatLng(lat: Double, lon: Double) :  Pair<Double, Double> {
        val geoPoint = GeoPoint(lat, lon)

        if (::tapMarker.isInitialized.not()) {
            tapMarker = Marker(mapView)
            tapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView?.overlays?.add(tapMarker)
        }

        updateTapMarker(geoPoint)

        if (::locationOverlay.isInitialized) {
            locationOverlay.disableFollowLocation()
        }

        mapView?.post {
            mapView?.controller?.setCenter(geoPoint)
            mapView?.controller?.setZoom(18.0)
            this.mapView?.invalidate()
        }

        return  Pair(lat,lon)
    }


    fun getLocation(onLocationResult: (lat: Double, lon: Double) -> Unit) {
        val gpsProvider = GpsMyLocationProvider(context)
        gpsProvider.startLocationProvider(object : IMyLocationConsumer {
            override fun onLocationChanged(
                location: android.location.Location?,
                source: IMyLocationProvider?
            ) {
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    Toast.makeText(
                        context,
                        "Latitude: $lat\nLongitude: $lon",
                        Toast.LENGTH_LONG
                    ).show()

                    gpsProvider.stopLocationProvider()

                    // 🔥 Kirim balik hasil lewat callback
                    onLocationResult(lat, lon)

                } else {
                    Toast.makeText(context, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
