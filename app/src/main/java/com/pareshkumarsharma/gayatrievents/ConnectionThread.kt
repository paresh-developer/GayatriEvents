package com.pareshkumarsharma.gayatrievents

import android.util.Log
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.reflect.typeOf

class ConnectionThread(name: String) : Thread(name) {
    override fun run() {
        var a: java.net.URLConnection
//        val url = URL("https://GayatriEvents.ddns.net/GayatriEvents/api/MobileApp")
        val url = URL("https://GayatriEvents.ddns.net/GayatriEvents//api/UserApi")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
//        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        urlConnection.doOutput = true
        try {
//            val `in` = BufferedInputStream(urlConnection.getInputStream())
//            readStream(`in`)
            val outPutStream = urlConnection.outputStream
            val model = "{\n" +
                    "    \"User_Name\": \"Paresh\",\n" +
                    "    \"User_Email\": \"Paresh@98000@hotmail.com\",\n" +
                    "    \"User_Mobile\": \"8128611138\",\n" +
                    "    \"User_Password\": \"Pwd\",\n" +
                    "    \"TableName\": \"MobileAppUser\"\n" +
                    "}"
            outPutStream.write(model.toByteArray())
            outPutStream.flush()
            outPutStream.close()
            val responseCode = urlConnection.responseCode
            if(responseCode == HttpURLConnection.HTTP_OK){
                val bufferedIn = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val respo = bufferedIn.readLine()

                val UsrModel = Gson().fromJson<UserRegisterModel>(respo,UserRegisterModel::class.java)
            }
        } catch (ex: Exception) {
            Log.d("API Call", ex.message.toString())
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun readStream(inputStream: BufferedInputStream) {
        val isr = InputStreamReader(inputStream)
        val br = BufferedReader(isr)
    }
}