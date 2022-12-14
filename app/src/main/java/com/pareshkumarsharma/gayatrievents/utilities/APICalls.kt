package com.pareshkumarsharma.gayatrievents.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.pareshkumarsharma.gayatrievents.activities.MainActivity
import com.pareshkumarsharma.gayatrievents.api.model.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class APICalls {
    companion object  {
        private const val LOGIN_URL = "http://10.0.2.2/GayatriEvents/api/MobileApp/Login"
        private const val REGISTER_URL = "http://10.0.2.2/GayatriEvents/api/MobileApp/Register"
        private const val PANCHANG_DOWNLOAD_URL = "http://10.0.2.2/GayatriEvents/api/Panchang"
        private const val PASSWORD_RESET_REQUEST =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/PasswordResetRequest"
        private const val PASSWORD_RESET =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/PasswordReset"
        private const val CHECK_MESSAGER_REQUEST =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/MobileMessageRequest"
        private const val CHECK_MESSAGER_UPDATE =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/MobileMessageUpdate"
        private const val USER_TYPE_CHANGE_REQUEST =
            "http://10.0.2.2/GayatriEvents/api/MobileApp/UserTypeChangeRequest"



        private const val NO_INTERNTET_MSG = "Internet required!"

        internal lateinit var cookies : Map<String,String>
        internal var lastCallMessage = ""
        internal lateinit var lastCallObject: Any

        private lateinit var Cont:Context

        internal fun setContext(c:Context){
                Cont = c
        }

        internal fun login(userEmail: String, userMobile: String, userPass: String): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

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

        internal fun register(
            userName: String,
            userMobile: String,
            userEmail: String,
            userPass: String
        ): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

            val url = URL(REGISTER_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val loginModel = UserRegisterModel(userName, userMobile, userEmail, userPass,0)
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

        internal fun downloadPanchang(): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            var bytes: ByteArray = ByteArray(1, { 0 })

            val url = URL(PANCHANG_DOWNLOAD_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("GET")
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    bytes = urlConnection.inputStream.readBytes()
                    isSuccess = true
                    lastCallObject = bytes
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
                }
            } catch (ex: Exception) {
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestForPasswordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

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
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun passwordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

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
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun messageRequestUpdate(messageModel: MessageModel): Boolean {
            var isSuccess = false

            val url = URL(CHECK_MESSAGER_UPDATE)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if (cookies.size > 0)
                urlConnection.setRequestProperty(
                    "Cookie",
                    "token=" + cookies["token"] + ";expires=" + cookies["expires"]
                )
            else {
                lastCallMessage = "Cookie expire"
                isSuccess = false
                return isSuccess
            }

            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val model = Gson().toJson(messageModel, messageModel.javaClass)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val respo = InputStreamReader(urlConnection.inputStream).readText()
                    lastCallMessage = respo
                    isSuccess = true
                } else {
                    lastCallMessage = InputStreamReader(urlConnection.errorStream).readText()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun downloadMessageRequests(): Boolean {

            var isSuccess = false

            val url = URL(CHECK_MESSAGER_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("GET")
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")

            if (cookies.size > 0)
                urlConnection.setRequestProperty(
                    "Cookie",
                    "token=" + cookies["token"] + ";expires=" + cookies["expires"]
                )
            else {
                lastCallMessage = "Cookie expire"
                return isSuccess
            }

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val res = InputStreamReader(urlConnection.inputStream).readText()
                    lastCallObject = Gson().fromJson(res, Array<MessageModel>::class.java)
                    isSuccess = true
                } else {
                    lastCallMessage =
                        BufferedReader(InputStreamReader(urlConnection.errorStream)).readText()
                }
            } catch (ex: Exception) {
                Log.d("API Call", ex.message.toString())
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestUserTypeChange(UTCModel: UserTypeChangeRequestModel): Boolean {
            var isSuccess = false

            if(!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(USER_TYPE_CHANGE_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val model = Gson().toJson(UTCModel, UserTypeChangeRequestModel::class.java)
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
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun decodeString(intString:String):String{
            val string_array = intString.trim('#').split('#')
            val newPassStr = ByteArray(string_array.size)
            for (i in 0..string_array.size - 1)
                newPassStr[i] =
                    Math.round((((string_array[i].toDouble() + 90.0) / 34.0) - 88) * 55)
                        .toInt().toByte()
            return newPassStr.decodeToString()
        }

        internal fun encodeString(intString: String):String{
            var encodedStr = "#"
            val byteA = intString.toByteArray(Charsets.UTF_8)
            for (i in 0..byteA.size-1)
                encodedStr += ((((byteA[i].toDouble()/55.0)+88.0)*34.0)-90.0).toString()+"#"
            return encodedStr
        }

        internal fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

                    if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                            return true
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                            return true
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                            return true
                        }
                    }
                } else {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        return true
                    }
                }
            }
            return false
        }
    }
}