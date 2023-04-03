package com.pareshkumarsharma.gayatrievents.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.PaymentRequest
import com.pareshkumarsharma.gayatrievents.api.model.PaymentUpdateModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.utilities.PaymentManager


class NewPayment : AppCompatActivity() {
    private val TEZ_REQUEST_CODE = 38009
    lateinit var PaymentActivityResultLauncher: ActivityResultLauncher<Intent>

    companion object {
        var RefCode: Char = ' '
        var RefId = ""
        var RefName = ""
        var RefAmount = 0.0f
        var Status: String = ""
        var TxnRef: String = ""
        var ApprovalRefNo: String = ""
        var TxnId: String = ""
        var ResponseCode: Short = 0
        var Response: String = ""
        var PaymentStatus: Short = 0
    }

    var SelectedPaymentMethod = 0
    var Payment_Global_Id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_payment)

        findViewById<RadioGroup>(R.id.rdo_group).setOnCheckedChangeListener { radioGroup, i ->
            if (i == 0) {
                findViewById<LinearLayout>(R.id.lyt_cash).visibility = View.GONE
                findViewById<LinearLayout>(R.id.lyt_online).visibility = View.VISIBLE
            } else {
                findViewById<LinearLayout>(R.id.lyt_cash).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.lyt_online).visibility = View.GONE
            }
        }

        findViewById<ImageButton>(R.id.btn_img_paytm).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 0, RefCode)
            openPayment(
                PaymentManager.Paytm(
                    this,
                    RefCode + " " + RefId,
                    Payment_Global_Id,
                    "For " + RefName + " ($RefId)",
                    RefAmount
                )
            )
        }

        findViewById<ImageButton>(R.id.btn_img_googlepay).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 0, RefCode)
            openPayment(
                PaymentManager.GooglePay(
                    this,
                    RefCode + " " + RefId,
                    Payment_Global_Id,
                    "For " + RefName + " ($RefId)",
                    RefAmount
                )
            )
        }

        findViewById<ImageButton>(R.id.btn_img_phonepe).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 0, RefCode)
            openPayment(
                PaymentManager.PhonePe(
                    this,
                    RefCode + " " + RefId,
                    Payment_Global_Id,
                    "For " + RefName + " ($RefId)",
                    RefAmount
                )
            )
        }

        PaymentActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    if (result.resultCode === RESULT_OK) {
                        val res = result.data?.extras
                        val status =
                            if (res?.get("Status") == null) "" else res?.get("Status").toString()
                        val txnRef =
                            if (res?.get("txnRef") == null) Payment_Global_Id else res?.get("txnRef")
                                .toString()
                        val approvalRefNo =
                            if (res?.get("ApprovalRefNo") == null) "" else res?.get("ApprovalRefNo")
                                .toString()
                        val response =
                            if (res?.get("response") == null) "" else res?.get("response")
                                .toString()
                        val txnId =
                            if (res?.get("txnId") == null) "" else res?.get("txnId").toString()
                        var responseCode: Short = 0
                        if (res?.get("responseCode") != null && res?.get("responseCode").toString()
                                .trim().length > 0
                        )
                            responseCode = res?.get("responseCode").toString().toShort()
                        var pstat = 0
                        if (status == "SUCCESS")
                            pstat = 2
                        else if (status == "FAILURE")
                            pstat = 3

                        updatePayment(
                            status,
                            txnRef,
                            approvalRefNo,
                            txnId,
                            responseCode,
                            response,
                            pstat.toShort()
                        )
                    } else {
                        updatePayment(
                            "FAIL",
                            Payment_Global_Id,
                            "",
                            "",
                            0,
                            "",
                            3
                        )
                    }
                })
    }

    fun requestPayment(
        ref_name: String,
        refId: String,
        refAmount: Float,
        selectedPaymentMethod: Int,
        refCode: Char
    ) {
        Thread(Runnable {
            if (APICalls.requestNewPaymentRegistration(
                    PaymentRequest(
                        "For " + ref_name + " ($refId)",
                        refId,
                        refAmount,
                        selectedPaymentMethod,
                        refCode
                    )
                )
            ) {
                Payment_Global_Id =
                    APICalls.lastCallMessage.split("Id:")[1].trim()
                        .trim('"')
            }
        }).start()
    }

    fun openPayment(intent: Intent) {
        PaymentActivityResultLauncher.launch(intent)
    }

    fun updatePayment(
        status: String,
        txnRef: String,
        approvalRefNo: String,
        txnId: String,
        responseCode: Short,
        response: String,
        pstat: Short
    ) {
        APICalls.setContext(this)
        APICalls.cookies = mapOf<String, String>(
            Pair(
                "token",
                getSharedPreferences(
                    Database.SHAREDFILE,
                    MODE_PRIVATE
                ).getString("token", "").toString()
            ),
            Pair(
                "expires",
                getSharedPreferences(
                    Database.SHAREDFILE,
                    MODE_PRIVATE
                ).getString("expires", "").toString()
            )
        )
        Thread(Runnable {
            if (APICalls.requestPaymentStatusUpdate(
                    PaymentUpdateModel(
                        status,
                        txnRef,
                        approvalRefNo,
                        txnId,
                        responseCode,
                        response,
                        pstat
                    )
                )
            ) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }).start()
    }
}
// TODO: Check other payment apps
//private fun getInstalledUPIApps(): ArrayList<String>? {
//    val upiList: ArrayList<String> = ArrayList()
//    val uri: Uri = Uri.parse(String.format("%s://%s", "upi", "pay"))
//    val upiUriIntent = Intent()
//    upiUriIntent.data = uri
//    val packageManager: PackageManager = getApplication().getPackageManager()
//    val resolveInfoList =
//        packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
//    if (resolveInfoList != null) {
//        for (resolveInfo in resolveInfoList) {
//            upiList.add(resolveInfo.activityInfo.packageName)
//        }
//    }
//    return upiList
//}