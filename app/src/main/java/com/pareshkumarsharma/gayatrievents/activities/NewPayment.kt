package com.pareshkumarsharma.gayatrievents.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.PaymentRequest
import com.pareshkumarsharma.gayatrievents.api.model.PaymentUpdateModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database


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
        var Payment_Global_Id = ""
    }

    var SelectedPaymentMethod = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_payment)

        findViewById<RadioGroup>(R.id.rdo_group).setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.rdo_online) {
                findViewById<LinearLayout>(R.id.lyt_cash).visibility = View.GONE
                findViewById<LinearLayout>(R.id.lyt_online).visibility = View.VISIBLE
            } else {
                findViewById<LinearLayout>(R.id.lyt_cash).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.lyt_online).visibility = View.GONE
            }
        }

        findViewById<ImageButton>(R.id.btn_img_paytm).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 1, RefCode)
            findViewById<ImageButton>(R.id.btn_img_paytm).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_googlepay).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_phonepe).isEnabled = false
        }

        findViewById<ImageButton>(R.id.btn_img_googlepay).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 2, RefCode)
            findViewById<ImageButton>(R.id.btn_img_paytm).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_googlepay).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_phonepe).isEnabled = false
        }

        findViewById<ImageButton>(R.id.btn_img_phonepe).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 3, RefCode)
            findViewById<ImageButton>(R.id.btn_img_paytm).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_googlepay).isEnabled = false
            findViewById<ImageButton>(R.id.btn_img_phonepe).isEnabled = false
        }

        findViewById<Button>(R.id.btn_cash_pay_request).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 0, RefCode)
            findViewById<Button>(R.id.btn_cash_pay_request).isEnabled = false
        }

        findViewById<Button>(R.id.btn_upi_pay_request).setOnClickListener {
            requestPayment(RefName, RefId, RefAmount, 1, RefCode)
            findViewById<Button>(R.id.btn_upi_pay_request).isEnabled = false
        }

        findViewById<Button>(R.id.btn_copyUpi).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Copy","pareshsharma98000@okhdfcbank")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this,"UPI Id Copied",Toast.LENGTH_SHORT).show()
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
                            if (res?.get("txnRef") == null || res?.get("txnRef").toString()
                                    .trim().length == 0
                            ) Payment_Global_Id else res?.get("txnRef")
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
                        var pstat = 3
                        if (status == "SUCCESS")
                            pstat = 2

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
                            "Canceled",
                            Payment_Global_Id,
                            "",
                            "",
                            0,
                            "",
                            3
                        )
                    }
                    finish()
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
                runOnUiThread { Toast.makeText(applicationContext,APICalls.lastCallMessage,Toast.LENGTH_SHORT).show() }
                Payment_Global_Id =
                    APICalls.lastCallMessage.split("Id:")[1].trim()
                        .trim('"')

//                when (selectedPaymentMethod) {
//                    1 -> openPayment(
//                        PaymentManager.Paytm(
//                            this,
//                            "Cat1",
//                            Payment_Global_Id,
//                            refId,
//                            RefAmount
//                        )
//                    )
//                    2 -> openPayment(
//                        PaymentManager.GooglePay(
//                            this,
//                            "Cat1",
//                            Payment_Global_Id,
//                            refId,
//                            RefAmount
//                        )
//                    )
//                    3 -> openPayment(
//                        PaymentManager.PhonePe(
//                            this,
//                            "Cat1",
//                            Payment_Global_Id,
//                            refId,
//                            RefAmount
//                        )
//                    )
//                }
            }
        }).start()
//        if(selectedPaymentMethod == 0)
            finish()
    }

    fun openPayment(intent: Intent) {
        try {
            PaymentActivityResultLauncher.launch(intent)
        }catch (e:Exception){
            runOnUiThread {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_LONG).show()
            }
        }
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