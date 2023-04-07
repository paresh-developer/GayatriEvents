package com.pareshkumarsharma.gayatrievents.utilities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri


class PaymentManager {
    companion object {

        private val TEZ_REQUEST_CODE = 38009

        private val GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
        private val PHONEPE_PACKAGE_NAME = "com.phonepe.app"
        private val PAYTM_PACKAGE_NAME = "net.one97.paytm"
        private val BHIM_PACKAGE_NAME = "in.org.npci.upiapp"
        private val AMAZON_PACKAGE_NAME = "in.amazon.mShop.android.shopping"

        private val UPI_ID = "pareshsharma98000@okhdfcbank"

        internal fun GooglePay(activity: Activity,categoryCode:String, tranId:String,tranNote:String, amount:Float):Intent {
            // Google Pay Payment info
            // https://developers.google.com/pay/india/api/web/create-payment-method
            val uri = Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", UPI_ID) // Payee address or business virtual payment address (VPA).
                .appendQueryParameter("pn", "Gayatri Event") // Payee name or business name.
                .appendQueryParameter("mc", categoryCode) // Business retailer category code.
                .appendQueryParameter("tr", tranId) // Transaction reference ID. (Business specific ID. Must be unique for each request.)
                .appendQueryParameter("tn", tranNote) // Transaction note. It is the description appearing in the Google Pay payflow. (Maximum length is 80 characters)
                .appendQueryParameter("am", amount.toString()) // Transaction amount. (Up to two decimal digits are allowed. This should be set in the details object instead of the supportedInstruments object.)
                .appendQueryParameter("cu", "INR") // Currency code. (This should be set in the details object instead of supportedInstruments object. Only the Indian rupee (INR) is currently supported.)
                .appendQueryParameter("url", "http://localhost/GayatriEvents")  // Transaction reference URL.
                .build()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME)
            return intent
        }

        internal fun PhonePe(activity: Activity,categoryCode:String, tranId:String,tranNote:String, amount:Float):Intent {
            // Google Pay Payment info
            // https://developers.google.com/pay/india/api/web/create-payment-method
            val uri = Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", UPI_ID) // Payee address or business virtual payment address (VPA).
                .appendQueryParameter("pn", "Pareshkumar Sharma") // Payee name or business name.
                .appendQueryParameter("mc", categoryCode) // Business retailer category code.
                .appendQueryParameter("tr", tranId) // Transaction reference ID. (Business specific ID. Must be unique for each request.)
                .appendQueryParameter("tn", tranNote) // Transaction note. It is the description appearing in the Google Pay payflow. (Maximum length is 80 characters)
                .appendQueryParameter("am", amount.toString()) // Transaction amount. (Up to two decimal digits are allowed. This should be set in the details object instead of the supportedInstruments object.)
                .appendQueryParameter("cu", "INR") // Currency code. (This should be set in the details object instead of supportedInstruments object. Only the Indian rupee (INR) is currently supported.)
                .appendQueryParameter("url", "http://localhost/GayatriEvents")  // Transaction reference URL.
                .build()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.setPackage(PHONEPE_PACKAGE_NAME)
            return intent
        }

        internal fun Paytm(activity: Activity,categoryCode:String, tranId:String,tranNote:String, amount:Float): Intent {
            // Google Pay Payment info
            // https://developers.google.com/pay/india/api/web/create-payment-method
            val uri = Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", UPI_ID) // Payee address or business virtual payment address (VPA).
                .appendQueryParameter("pn", "Pareshkumar Sharma") // Payee name or business name.
                .appendQueryParameter("mc", categoryCode) // Business retailer category code.
                .appendQueryParameter("tr", tranId) // Transaction reference ID. (Business specific ID. Must be unique for each request.)
                .appendQueryParameter("tn", tranNote) // Transaction note. It is the description appearing in the Google Pay payflow. (Maximum length is 80 characters)
                .appendQueryParameter("am", amount.toString()) // Transaction amount. (Up to two decimal digits are allowed. This should be set in the details object instead of the supportedInstruments object.)
                .appendQueryParameter("cu", "INR") // Currency code. (This should be set in the details object instead of supportedInstruments object. Only the Indian rupee (INR) is currently supported.)
                .appendQueryParameter("url", "http://localhost/GayatriEvents")  // Transaction reference URL.
                .build()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.setPackage(PAYTM_PACKAGE_NAME)
            return intent
        }

        private fun appInstalledOrNot(activity: Activity,uri: String): Boolean {
            val pm: PackageManager = activity.packageManager
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
            }
            return false
        }
    }
}