    package com.example.aplikasi_zulham.Controller

    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.util.Log
    import com.example.aplikasi_zulham.Model.Cuaca
    import com.example.aplikasi_zulham.RetrofitInstance
    import org.jetbrains.annotations.Debug
    import org.json.JSONObject
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.io.BufferedReader
    import java.io.InputStream
    import java.io.InputStreamReader
    import java.net.HttpURLConnection
    import java.net.URL
    import android.graphics.Canvas
    import android.graphics.Picture
    import com.caverock.androidsvg.SVG

    class CuacaController {


        suspend fun ambilData(adm4: String): List<Cuaca> {
            val cuacaList = mutableListOf<Cuaca>()

            try {
                val response = RetrofitInstance.api.getWeatherData(adm4)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("CuacaView", body.toString())

                    val dataWrapperList = body?.data
                    if (!dataWrapperList.isNullOrEmpty()) {
                        val cuacaList2D = dataWrapperList[0].cuaca

                        for (subList in cuacaList2D) {
                            for (cuacaTarget in subList) {
                                if (cuacaTarget.t != null &&
                                    cuacaTarget.datetime != null &&
                                    cuacaTarget.local_datetime != null &&
                                    cuacaTarget.image != null && cuacaTarget.weather_desc != null
                                ) {
                                    val cuaca = Cuaca(
                                        cuacaTarget.t,
                                        cuacaTarget.datetime,
                                        cuacaTarget.local_datetime,
                                        cuacaTarget.image,
                                        cuacaTarget.weather_desc
                                    )
                                    cuacaList.add(cuaca)
                                }
                            }
                        }
                    } else {
                        Log.e("CuacaView", "Data kosong")
                    }
                } else {
                    Log.e("CuacaView", "Response gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CuacaView", "Error: ${e.message}")
            }

            return cuacaList
        }




        fun getBitmapFromURL(src: String): Bitmap? {
            return try {
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


        fun svgUrlToBitmap(url: String, width: Int, height: Int): Bitmap? {
            return try {
                val inputStream = URL(url).openStream()
                val svg = SVG.getFromInputStream(inputStream)

                svg.setDocumentWidth(width.toFloat())
                svg.setDocumentHeight(height.toFloat())

                val picture: Picture = svg.renderToPicture()
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawPicture(picture)

                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }





