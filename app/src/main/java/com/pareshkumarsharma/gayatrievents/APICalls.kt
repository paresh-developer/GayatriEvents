package com.pareshkumarsharma.gayatrievents

import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Dictionary

class APICalls {
    companion object {
        private const val LOGIN_URL = "http://10.0.2.2/GayatriEvents/api/MobileApp/Login"
        private const val REGISTER_URL = "http://10.0.2.2/GayatriEvents/api/MobileApp/Register"
        private const val PANCHANG_DOWNLOAD_URL = "http://10.0.2.2/GayatriEvents/api/Panchang"
        private const val PASSWORD_RESET_REQUEST =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/PasswordResetRequest"
        private const val PASSWORD_RESET =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/PasswordReset"

        internal lateinit var cookies : Map<String,String>
        internal var lastCallMessage = ""
        internal lateinit var lastCallObject: Any

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
                    val bufferedIn = InputStreamReader(urlConnection.inputStream)
                    val respo = bufferedIn.readText()
                    val regModel =
                        Gson().fromJson<UserRegisterModel>(respo, UserRegisterModel::class.java)

                    if(urlConnection.headerFields["Set-Cookie"] != null)
                        if(urlConnection.headerFields["Set-Cookie"]?.size!!.compareTo(0)>0) {
                            val cks = urlConnection.headerFields["Set-Cookie"]?.get(0)?.split(';')
                            cookies = mapOf<String,String>(Pair("token",
                                cks?.get(0)!!.split('=')[1]),
                                Pair("expires",
                                    cks?.get(1)!!.split('=')[1])
                            )
                        }


                    lastCallMessage = regModel.User_Name
                    if (regModel != null)
                        lastCallObject = regModel
                    MainActivity.UserName = lastCallMessage
                    isSuccess = true
                    bufferedIn.close()
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
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
                    val bufferedIn = InputStreamReader(urlConnection.inputStream)
                    isSuccess = true
                    lastCallMessage = bufferedIn.readText()
                    bufferedIn.close()
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        fun downloadPanchang(): ByteArray {

            var bytes: ByteArray = ByteArray(1, { 0 })

            val url = URL(PANCHANG_DOWNLOAD_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("GET")
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    bytes = urlConnection.inputStream.readBytes()
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return bytes
        }

        fun requestForPasswordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            val url = URL(PASSWORD_RESET_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val model = Gson().toJson(passwordResetModel, PasswordResetRequestModel::class.java)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    inp.close()
                    lastCallMessage = respo
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        fun passwordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            val url = URL(PASSWORD_RESET)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val model = Gson().toJson(passwordResetModel, PasswordResetRequestModel::class.java)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    inp.close()
                    lastCallMessage = respo
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }
    }

}