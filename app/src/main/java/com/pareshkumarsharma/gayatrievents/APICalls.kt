package com.pareshkumarsharma.gayatrievents

import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class APICalls {
    companion object {
        private const val LOGIN_URL = "http://192.168.1.2/GayatriEvents/api/MobileApp/Login"
        private const val REGISTER_URL = "http://192.168.1.2/GayatriEvents/api/MobileApp/Register"

        var lastCallMessage = ""

        fun login(userEmail: String, userMobile: String, userPass: String): Boolean {
            var isSuccess = false

            val url = URL(LOGIN_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val loginModel = UserLoginModel(userMobile, userEmail, userPass)
                val model = Gson().toJson(loginModel, loginModel.javaClass)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedIn = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val respo = bufferedIn.readText()
                    val regModel =
                        Gson().fromJson<UserRegisterModel>(respo, UserRegisterModel::class.java)
                    lastCallMessage = regModel.User_Name
                    MainActivity.UserName = lastCallMessage
                    isSuccess = true
                }
                else{
                    lastCallMessage = BufferedReader(InputStreamReader(urlConnection.errorStream)).readText()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        fun register(
            userName: String,
            userMobile: String,
            userEmail: String,
            userPass: String
        ): Boolean {
            var isSuccess = false

            val url = URL(REGISTER_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val loginModel = UserRegisterModel(userName, userMobile, userEmail, userPass)
                val model = Gson().toJson(loginModel, loginModel.javaClass)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedIn = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    isSuccess = true
                    lastCallMessage = bufferedIn.readText()
                }
                else
                    lastCallMessage = BufferedReader(InputStreamReader(urlConnection.errorStream)).readText()
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }
    }

}