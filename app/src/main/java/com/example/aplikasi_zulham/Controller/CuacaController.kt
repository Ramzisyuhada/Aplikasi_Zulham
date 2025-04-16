package com.example.aplikasi_zulham.Controller

import com.example.aplikasi_zulham.Interface.ApiService
import com.example.aplikasi_zulham.Model.Cuaca
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CuacaController : ApiService{



    override fun <T> AmbilData(): List<T> {

        val apiUrl = URL("https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4=33.25.06.2012")
        val response = StringBuilder()

        with(apiUrl.openConnection() as HttpURLConnection) {

            requestMethod = "GET"


            val responseCode = responseCode
            println("Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(inputStream)).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                }
            } else {
                println("Error: Unable to fetch data from the API.")
            }
        }

        val jsonResponse = JSONObject(response.toString())


        val weatherData = jsonResponse.getJSONArray("data")

        val resultList = mutableListOf<T>()
        for (i in 0 until weatherData.length()) {
            val item = weatherData.getJSONObject(i)

            val data: T = item as T
            resultList.add(data)
        }

        return resultList
    }


}