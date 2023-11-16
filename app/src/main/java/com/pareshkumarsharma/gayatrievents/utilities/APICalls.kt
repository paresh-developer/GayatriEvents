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
import java.security.Key
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class APICalls {
    companion object {

        private val key = "KLJFIQACWDMBYTVURZONSHXPEG298573"+ Date().time

        // region URLS
        internal fun updatePath(hostPath: String) {
            HOST = hostPath
        }

        //        internal var HOST = "http://10.0.2.2/GayatriEvents/api/"
//       internal var HOST = "http://geparesh.ddns.net/GayatriEvents/api/"
//        internal var HOST = "http://geparesh.ddns.net/GayatriEventsDemo/api/"
//        var HOST = "http://192.168.1.5/GayatriEvents/api/"
       internal var HOST = "https://paresh98000.wordpress.com/"

        private var LOGIN_URL = "MobileApp/Login"
        private var REGISTER_URL = "MobileApp/Register"
        private var PANCHANG_DOWNLOAD_URL = "Panchang"
        private var PASSWORD_RESET_REQUEST =
            "MobileApp/PasswordResetRequest"
        private var PASSWORD_RESET =
            "MobileApp/PasswordReset"
        private var CHECK_MESSAGER_REQUEST =
            "MobileApp/MobileMessageRequest"
        private var CHECK_MESSAGER_UPDATE =
            "MobileApp/MobileMessageUpdate"
        private var USER_TYPE_CHANGE_REQUEST =
            "MobileApp/UserTypeChangeRequest"
        private var VIEW_EXISTING_SERVICE_OF_CURRENT_USER =
            "Service/"
        private var VIEW_EXISTING_SERVICE_FOR_EVENTS =
            "Service/ForEvent"
        private var SERVICE_REGISTRATION_REQUEST =
            "Service/New"
        private var SERVICE_UPDATION_REQUEST =
            "Service/Update"
        private var SERVICE_PRODUCT_REGISTRATION_REQUEST =
            "Service/NewProduct"
        private var SERVICE_PRODUCT_UPDATION_REQUEST =
            "Service/Update/ServiceProduct"
        private var SERVICE_PRODUCT_DETAILS_REGISTRATION_REQUEST =
            "Service/NewProductDetails"
        private var SERVICE_PRODUCT_DETAILS_UPDATION_REQUEST =
            "Service/Update/ServiceProductDetail"
        private var VIEW_EXISTING_SERVICE_PRODUCT_OF_CURRENT_USER =
            "Service/Product"
        private var VIEW_EXISTING_SERVICE_PRODUCT_DETAIL_OF_CURRENT_USER =
            "Service/ProductDetails"
        private var VIEW_EXISTING_EVENTS_OF_CURRENT_USER =
            "Event/UserEvents"
        private var CLIENT_EVENT_REQUEST =
            "Event/UserEventOrders"
        private var CLIENT_EVENT_REQUEST_RESPONSE =
            "Event/UserResponse"
        private var CLIENT_EVENT_DELETE_REQUEST =
            "Event/DeleteEvent"
        private var EVENT_REGISTRATION_REQUEST =
            "Event/New"
        private var PAYMENT_REGISTRATION_REQUEST =
            "Payment/New"
        private var PAYMENT_UPDATE_REQUEST =
            "Payment/Update"
        private var NEW_DONATION_REQUEST =
            "Donation/New"
        private var AVAILABLE_DONATIONS_OF_USER =
            "Donation/"
        private var AVAILABLE_USER_FOR_PARTNERSHIP =
            "MobileApp/"
        private var AVAILABLE_PARTNERSHIP =
            "Patnership/"
        private var NEW_PARTNERSHIP_REQUEST =
            "Patnership/New"
        // endregion

        // region RESPONSE MESSAGES
        private const val NO_INTERNTET_MSG = "Internet required!"
        // endregion


        internal lateinit var cookies: Map<String, String>
        internal var lastCallMessage = ""
        internal lateinit var lastCallObject: Any

        private lateinit var Cont: Context

        // region Calls
        internal fun login(userEmail: String, userMobile: String, userPass: String): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

            val url = URL(HOST + LOGIN_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

                    if (urlConnection.headerFields["Set-Cookie"] != null)
                        if (urlConnection.headerFields["Set-Cookie"]?.size!!.compareTo(0) > 0) {
                            val cks = urlConnection.headerFields["Set-Cookie"]?.get(0)?.split(';')
                            cookies = mapOf<String, String>(
                                Pair(
                                    "token",
                                    cks?.get(0)!!.split('=')[1]
                                ),
                                Pair(
                                    "expires",
                                    cks.get(1)!!.split('=')[1]
                                )
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
                LogManagement.Log(ex.message.toString(), "API Call")
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

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

            val url = URL(HOST + REGISTER_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.setRequestProperty("Accept", "*/*")
            urlConnection.doOutput = true

            try {
                val outPutStream = urlConnection.outputStream
                val loginModel = UserRegisterModel(userName, userMobile, userEmail, userPass, 0)
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
                LogManagement.Log(ex.message.toString(), "API Call")
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun downloadPanchang(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            var bytes: ByteArray = ByteArray(1, { 0 })

            val url = URL(HOST + PANCHANG_DOWNLOAD_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")

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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestForPasswordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + PASSWORD_RESET_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun passwordReset(passwordResetModel: PasswordResetRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + PASSWORD_RESET)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun messageRequestUpdate(messageModel: MessageModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + CHECK_MESSAGER_UPDATE)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                LogManagement.Log(ex.message.toString(), "API Call")
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun downloadMessageRequests(): Boolean {

            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + CHECK_MESSAGER_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestUserTypeChange(UTCModel: UserTypeChangeRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + USER_TYPE_CHANGE_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewServiceRegistration(ServiceModel: ServiceRegistrationRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_REGISTRATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(ServiceModel, ServiceRegistrationRequestModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewEventRegistration(EventModel: EventRegistrationModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + EVENT_REGISTRATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(EventModel, EventRegistrationModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewServiceUpdation(ServiceModel: ServiceUpdationRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_UPDATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(ServiceModel, ServiceUpdationRequestModel::class.java)
                outPutStream.write(model.toByteArray(Charsets.UTF_8))
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewServiceProductRegistration(ServiceProductModel: ServiceProductRegistrationModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_PRODUCT_REGISTRATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model =
                    Gson().toJson(ServiceProductModel, ServiceProductRegistrationModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestServiceProductUpdation(ServiceProductModel: ServiceProductUpdationModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_PRODUCT_UPDATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model =
                    Gson().toJson(ServiceProductModel, ServiceProductUpdationModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewServiceProductDetailsRegistration(ServiceProductDetailsModel: ServiceProductDetailsRegistrationModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_PRODUCT_DETAILS_REGISTRATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(
                    ServiceProductDetailsModel,
                    ServiceProductDetailsRegistrationModel::class.java
                )
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestServiceProductDetailsUpdation(ServiceProductDetailsModel: ServiceProductDetailsUpdationModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + SERVICE_PRODUCT_DETAILS_UPDATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(
                    ServiceProductDetailsModel,
                    ServiceProductDetailsUpdationModel::class.java
                )
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingServiceOfCurrentUser(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + VIEW_EXISTING_SERVICE_OF_CURRENT_USER)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson<Array<ServiceDisplayModel>>(
                            respo,
                            Array<ServiceDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingServiceForEvent(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + VIEW_EXISTING_SERVICE_FOR_EVENTS)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson<Array<ServiceDisplayModel>>(
                            respo,
                            Array<ServiceDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingServiceProductOfCurrentUser(selectedService: String): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url =
                URL(HOST + VIEW_EXISTING_SERVICE_PRODUCT_OF_CURRENT_USER + "?serviceGlobalId=$selectedService")
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")
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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson<Array<ServiceProductDisplayModel>>(
                            respo,
                            Array<ServiceProductDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingServiceProductDetailOfCurrentUser(selectedServiceProduct: String): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url =
                URL(HOST + VIEW_EXISTING_SERVICE_PRODUCT_DETAIL_OF_CURRENT_USER + "?serviceProductGlobalId=$selectedServiceProduct")
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")
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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson<Array<ServiceProductDetailDisplayModel>>(
                            respo,
                            Array<ServiceProductDetailDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingEventsOfCurrentUser(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + VIEW_EXISTING_EVENTS_OF_CURRENT_USER)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson(
                            respo,
                            Array<EventDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getClientEventRequestsOfCurrentUser(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + CLIENT_EVENT_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson(
                            respo,
                            Array<EventDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun sendClientEventRequestResponse(
            eventGlobalId: String,
            response: String,
            reason: String
        ): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

            val url = URL(HOST + CLIENT_EVENT_REQUEST_RESPONSE)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val loginModel = ClientEventRequestResponse(eventGlobalId, response, reason)
                val model = Gson().toJson(loginModel, loginModel.javaClass)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedIn = InputStreamReader(urlConnection.inputStream)
                    val respo = bufferedIn.readText()
                    bufferedIn.close()
                    lastCallMessage = respo
                    isSuccess = true
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun sendDeleteEventRequest(
            eventGlobalId: String,
            response: String,
            reason: String
        ): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return false
            }

            val url = URL(HOST + CLIENT_EVENT_DELETE_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val loginModel = ClientEventRequestResponse(eventGlobalId, response, reason)
                val model = Gson().toJson(loginModel, loginModel.javaClass)
                outPutStream.write(model.toByteArray())
                outPutStream.flush()
                outPutStream.close()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedIn = InputStreamReader(urlConnection.inputStream)
                    val respo = bufferedIn.readText()
                    bufferedIn.close()
                    lastCallMessage = respo
                    isSuccess = true
                } else {
                    val inp = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = inp.readText()
                    inp.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewPaymentRegistration(paymentModel: PaymentRequest): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + PAYMENT_REGISTRATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(paymentModel, PaymentRequest::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestPaymentStatusUpdate(paymentModel: PaymentUpdateModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + PAYMENT_UPDATE_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(paymentModel, PaymentUpdateModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewDonationRegistration(donationModel: DonationRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + NEW_DONATION_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(donationModel, DonationRequestModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getExistingDonationOfCurrentUser(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + AVAILABLE_DONATIONS_OF_USER)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson<Array<DonationDisplayModel>>(
                            respo,
                            Array<DonationDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getUsersForPartnership(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + AVAILABLE_USER_FOR_PARTNERSHIP)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson(
                            respo,
                            Array<UserRegisterModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun getPartnership(): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + AVAILABLE_PARTNERSHIP)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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

            try {
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inp = InputStreamReader(urlConnection.inputStream)
                    val respo = inp.readText()
                    val model =
                        Gson().fromJson(
                            respo,
                            Array<PartnershipDisplayModel>::class.java
                        )
                    lastCallObject = model
                    inp.close()
                    lastCallMessage = "Ok"
                    isSuccess = true
                } else {
                    val res = InputStreamReader(urlConnection.errorStream)
                    lastCallMessage = res.readText()
                    res.close()
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }

        internal fun requestNewPartnershipRegistration(partnershipModel: PartnershipRequestModel): Boolean {
            var isSuccess = false

            if (!isOnline(Cont)) {
                lastCallMessage = NO_INTERNTET_MSG
                return isSuccess
            }

            val url = URL(HOST + NEW_PARTNERSHIP_REQUEST)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
            urlConnection.setRequestProperty("Content-Type", "application/json")

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
                val model = Gson().toJson(partnershipModel, PartnershipRequestModel::class.java)
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
                LogManagement.Log(ex.message.toString(), "API Call")
                lastCallMessage = ex.message.toString()
            } finally {
                urlConnection.disconnect()
            }

            return isSuccess
        }
        // endregion

        // region Utility
        internal fun setContext(c: Context) {
            Cont = c
        }

        internal fun decodeString(intString: String): String {
            val string_array = intString.trim('#').split('#')
            val newPassStr = ByteArray(string_array.size)
            for (i in 0..string_array.size - 1)
                newPassStr[i] =
                    Math.round((((string_array[i].toDouble() + 90.0) / 34.0) - 88) * 55)
                        .toInt().toByte()
            return newPassStr.decodeToString()
        }

        internal fun encodeString(intString: String): String {
            var encodedStr = "#"
            val byteA = intString.toByteArray(Charsets.UTF_8)
            for (i in 0..byteA.size - 1)
                encodedStr += ((((byteA[i].toDouble() / 55.0) + 88.0) * 34.0) - 90.0).toString() + "#"
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

        internal fun encodeStringComplex(str: String): String {
            var encodedStr = ""
            var key_Array = ByteArray(0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                key_Array = java.util.Base64.getDecoder().decode(key)
            } else
                key_Array = android.util.Base64.decode(key, key.length)

            try {
                //Cipher _Cipher = Cipher.getInstance("AES");
                //Cipher _Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                val _Cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

                // Initialization vector.
                // It could be any value or generated using a random number generator.
                val iv = byteArrayOf(
                    40,
                    154.toByte(),
                    57,
                    248.toByte(),
                    183.toByte(),
                    219.toByte(),
                    50,
                    105,
                    117,
                    8,
                    162.toByte(),
                    59,
                    104,
                    160.toByte(),
                    200.toByte(),
                    31
                )
                val ivspec = IvParameterSpec(iv)
                val SecretKey: Key = SecretKeySpec(key_Array, "AES")
                _Cipher.init(Cipher.ENCRYPT_MODE, SecretKey, ivspec)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    encodedStr = java.util.Base64.getEncoder()
                        .encodeToString(_Cipher.doFinal(str.toByteArray())).trim()
                } else {
                    val crpstrbyt = _Cipher.doFinal(str.toByteArray())
                    encodedStr =
                        android.util.Base64.encodeToString(crpstrbyt, crpstrbyt.size).trim()
                }
            } catch (e: java.lang.Exception) {
                android.util.Log.d(" Encry ", e.message.toString())
            }

            return encodedStr
        }

        internal fun decodeStringComplex(str: String): String {
            var encodedStr = ""
            var key_Array = ByteArray(0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                key_Array = java.util.Base64.getDecoder().decode(key)
            } else
                key_Array = android.util.Base64.decode(key, key.length)

            try {
                //Cipher _Cipher = Cipher.getInstance("AES");
                //Cipher _Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                val _Cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

                // Initialization vector.
                // It could be any value or generated using a random number generator.
                val iv = byteArrayOf(
                    40,
                    154.toByte(),
                    57,
                    248.toByte(),
                    183.toByte(),
                    219.toByte(),
                    50,
                    105,
                    117,
                    8,
                    162.toByte(),
                    59,
                    104,
                    160.toByte(),
                    200.toByte(),
                    31
                )
                val ivspec = IvParameterSpec(iv)
                val SecretKey: Key = SecretKeySpec(key_Array, "AES")
                _Cipher.init(Cipher.DECRYPT_MODE, SecretKey, ivspec)
                var DecodedMessage = ByteArray(0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DecodedMessage = java.util.Base64.getDecoder().decode(str)
                } else {
                    DecodedMessage = android.util.Base64.decode(str, str.length)
                }
                encodedStr = String(_Cipher.doFinal(DecodedMessage)).trim()
            } catch (e: java.lang.Exception) {
                android.util.Log.d(" Decryp ", e.message.toString())
            }

            return encodedStr
        }
        // endregion
    }
}