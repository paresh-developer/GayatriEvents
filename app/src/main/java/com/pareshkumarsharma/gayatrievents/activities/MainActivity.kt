package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.PartnershipDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDetailDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDisplayModel
import com.pareshkumarsharma.gayatrievents.services.PanchangNotification
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.utilities.GlobalData


internal class MainActivity : AppCompatActivity() {
    internal companion object {
        /**
         * 0 when no login
         * 1 when on Login screen
         * 2 when login done successfully
         * 3 when on Sign Up screen
         * 4 when Sign Up Done
         */
        var IsLoginDone = 0
        var UserName = ""
        lateinit var btnPanchang: Button
        var IsRunning = false
        var Instance = 1
    }

    lateinit var txtHellow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IsRunning = true

        val DatabaseSetup =
            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("F001", false)
        if (!DatabaseSetup) {
            Database.activity = this
            Database.checkDatabaseSetup()
            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                .edit()
                .putBoolean("F001", true)
                .apply()
        }

        if (!getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("LLDone", false))
            startActivity(Intent(this, LoginActivity::class.java))
        else {
            IsLoginDone = 2
            UserName =
                getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getString("LLName", "")
                    .toString()
        }

        GlobalData.setUserGlobalId(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE))

        txtHellow = findViewById(R.id.txtHellow)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        txtHellow.movementMethod = ScrollingMovementMethod()
        btnLogout.setOnClickListener {
            IsLoginDone = 0

            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                .edit()
                .putString("token", "")
                .putString("expires", "")
                .putString("LLUname", "")
                .putString("LLMobile", "")
                .putString("LLPassword", "")
                .putBoolean("LLDone", false)
                .apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnPanchang = findViewById<Button>(R.id.btnPanchang)

        btnSetting.setOnClickListener {
            startActivity(Intent(this, Setting::class.java))
        }
        val packapath = this.packageName
        btnPanchang.setOnClickListener {
            startActivity(Intent(this, Panchang::class.java))
            IsLoginDone = 2
        }

        findViewById<Button>(R.id.btnService).setOnClickListener {
            if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) == 2) {
                startActivity(Intent(this, ServiceEdit::class.java))
            } else
                Toast.makeText(applicationContext, "You cannot access Services", Toast.LENGTH_LONG)
                    .show()
        }

        findViewById<Button>(R.id.btnEvent).setOnClickListener {
            startActivity(Intent(this, EventEdit::class.java))
        }

        findViewById<Button>(R.id.btnSaleRequests).setOnClickListener {
            if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) == 2) {
                startActivity(Intent(this, ClientEventRequestEdit::class.java))
            } else
                Toast.makeText(
                    applicationContext,
                    "You cannot access Client Requests",
                    Toast.LENGTH_LONG
                )
                    .show()
        }

        findViewById<Button>(R.id.btnDonate).setOnClickListener {
            startActivity(Intent(this, DonationEdit::class.java))
        }

        findViewById<Button>(R.id.btnLogScreen).setOnClickListener {
            if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
                Toast.makeText(
                    applicationContext,
                    "You cannot access Client Requests",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, LogActivity::class.java))
        }

        findViewById<Button>(R.id.btnMuhurat).setOnClickListener {
            startActivity(Intent(this, Muhurat::class.java))
        }

        findViewById<Button>(R.id.btnPartnership).setOnClickListener {
            Toast.makeText(
                    applicationContext,
                    "this is Under development",
                    Toast.LENGTH_LONG
                ).show()
//            if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
//                Toast.makeText(
//                    applicationContext,
//                    "You cannot access Client Requests",
//                    Toast.LENGTH_LONG
//                ).show()
//                return@setOnClickListener
//            }
//            startActivity(Intent(this, Patnership::class.java))
        }

        RefreshServiceData()
        RefreshPartnershipData()
    }

    override fun onResume() {
        super.onResume()

        if (IsLoginDone != 1) {
            var snakmsg = ""
            if (IsLoginDone == 0) {
                snakmsg = "स्वागत है! 😎"
                IsLoginDone = 100
            } else if (IsLoginDone == 2) {
                snakmsg = "प्रवेश हो गया!...👍"
                txtHellow.text = "नमस्ते! $UserName"
                IsLoginDone = 100
            } else if (IsLoginDone == 4) {
                snakmsg = "प्रवेश हो गया!...👍"
            }
            if (IsLoginDone != 0 && snakmsg.trim().length > 0)
                Snackbar.make(findViewById(R.id.mainActivityLayout), snakmsg, Snackbar.LENGTH_LONG)
                    .show()
        } else {
            finish()
        }
//        sendTomorrowNotification()
    }

    override fun onDestroy() {
        IsRunning = false
        super.onDestroy()
    }

    private fun sendTomorrowNotification() {
        if (!PanchangNotification.Is_Running)
            startService(Intent(this, PanchangNotification::class.java))
    }

    private fun RefreshServiceData() {
        Thread(Runnable {
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
            if (APICalls.getExistingServiceOfCurrentUser()) {
                val res = APICalls.lastCallObject as Array<ServiceDisplayModel>
                for (i in 0..res.size - 1) {
                    var nul_field = "Id"
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    val tbl2 =
                        Database.query("Select Id from Users where GlobalId='${res[i].UserGlobalId}'")
                    if (tbl2.Rows.size > 0 && !tbl2.Columns.contains("Error"))
                        c.put("UserId", tbl2.Rows[0][0].toInt())
                    else
                        nul_field += ",UserId"
                    c.put("ServiceType", res[i].ServiceType)
                    c.put("City", res[i].City)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("SAddress", res[i].Address)
                    c.put("Owner", res[i].Owner)
                    c.put("Approved", res[i].Approved)
                    c.put("RequestStatus", res[i].RequestStatus)
                    if (res[i].Approved)
                        c.put("ApprovalTime", res[i].ApprovalTime)
                    else
                        nul_field += ",ApprovalTime"

                    if (Database.getRowCount(
                            "Service",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service", c, nul_field)
                    else {
                        Database.updateTo(
                            "Service",
                            c,
                            "GlobalId=?",
                            listOf(res[i].GlobalId).toTypedArray()
                        )
                    }
                    RefreshServiceProductData(res[i].GlobalId)
                }
            } else {
//                runOnUiThread {
//                    Toast.makeText(
//                        applicationContext,
//                        APICalls.lastCallMessage,
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
            }
        }).start()
    }

    private fun RefreshServiceProductData(selectedService: String = "0") {
        Thread(Runnable {
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
            if (APICalls.getExistingServiceProductOfCurrentUser(selectedService)) {
                val res = APICalls.lastCallObject as Array<ServiceProductDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("ServiceGlobalId", res[i].ServiceGlobalId)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("Price", res[i].Price)
                    c.put("CreationDate", res[i].CreationDate)
                    c.put("ServiceId", ServiceProductEdit.selectedServiceId)
                    if (Database.getRowCount(
                            "Service_Product",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service_Product", c, "Id")
                    else
                        Database.updateTo(
                            "Service_Product",
                            c,
                            "GlobalId=?",
                            listOf(res[i].GlobalId).toTypedArray()
                        )
                    RefreshServiceProductDetailsData(res[i].GlobalId)
                }
            } else {
//                runOnUiThread {
//                    Toast.makeText(
//                        applicationContext,
//                        APICalls.lastCallMessage,
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
            }
        }).start()
    }

    private fun RefreshServiceProductDetailsData(selectedServiceProduct: String = "0") {
        Thread(Runnable {
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
            if (APICalls.getExistingServiceProductDetailOfCurrentUser(selectedServiceProduct)) {
                val res = APICalls.lastCallObject as Array<ServiceProductDetailDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("ServiceProductGlobalId", res[i].ServiceProductGlobalId)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("Type", res[i].Type)
                    c.put("CreationDate", res[i].CreationDate)
                    c.put("ServiceProductId", ServiceProductDetailsEdit.selectedServiceProductId)
                    if (Database.getRowCount(
                            "Service_Product_Detail",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service_Product_Detail", c, "Id")
                    else
                        Database.updateTo(
                            "Service_Product_Detail",
                            c,
                            "GlobalId=?",
                            listOf(res[i].GlobalId).toTypedArray()
                        )
                }
            } else {
//                runOnUiThread {
//                    Toast.makeText(
//                        applicationContext,
//                        APICalls.lastCallMessage,
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
            }
        }).start()
    }

    fun RefreshPartnershipData() {
        Thread(Runnable {
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
            if (APICalls.getPartnership()) {
                val res = APICalls.lastCallObject as Array<PartnershipDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    var nul_field = "Id"
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    val tbl2 =
                        Database.query("Select Id from Users where GlobalId='${res[i].UserGlobalId}'")
                    if (tbl2.Rows.size > 0 && !tbl2.Columns.contains("Error"))
                        c.put("UserId", tbl2.Rows[0][0].toInt())
                    else
                        nul_field += ",UserId"
                    c.put("PartnerUserIdList", res[i].PartnerUserGlobalIdList)
                    c.put("PartnershipShare", res[i].PartnershipShare)
                    c.put("PartnershipType", res[i].PatnershipType)
                    c.put("CreatedOn", res[i].CreatedOn)
                    c.put("Approval", res[i].Approval)
                    c.put("RequestStatus", res[i].RequestStatus)
                    nul_field += ",ApprovedOn"
                    if (Database.getRowCount(
                            "Partnership",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Partnership", c, nul_field)
                    else
                        Database.updateTo(
                            "Partnership",
                            c,
                            "GlobalId=?",
                            listOf(res[i].GlobalId).toTypedArray()
                        )

                    val res1 = res[i].Product
                    for(rr in res1) {
                        val c1 = ContentValues()
                        var nul_field1 = "Id"
                        c1.put("GlobalId", rr.GlobalId)
                        c1.put("ServiceGlobalId", rr.ServiceGlobalId)
                        c1.put("ProductGlobalId", rr.ProductGlobalId)
                        c1.put("Partnership_Request_GlobalId", res[i].GlobalId)
                        c1.put("Turn", rr.Turn)
                        c1.put("CreatedOn", rr.CreatedOn)
                        var tbl3 =
                            Database.query("Select Id from Partnership Where GlobalId='${res[i].GlobalId}'")
                        if (tbl3.Rows.size > 0 && !tbl3.Columns.contains("Error"))
                            c1.put("Partnership_Request_Id", tbl3.Rows[0][0].toInt())
                        else
                            nul_field1 += ",Partnership_Request_Id"

                        tbl3 =
                            Database.query("Select Id from Service Where GlobalId='${rr.ServiceGlobalId}'")
                        if (tbl3.Rows.size > 0 && !tbl3.Columns.contains("Error"))
                            c1.put("ServiceId", tbl3.Rows[0][0].toInt())
                        else
                            nul_field1 += ",ServiceId"

                        tbl3 =
                            Database.query("Select Id from Service_Product Where GlobalId='${rr.ProductGlobalId}'")
                        if (tbl3.Rows.size > 0 && !tbl3.Columns.contains("Error"))
                            c1.put("ProductId", tbl3.Rows[0][0].toInt())
                        else
                            nul_field1 += ",ProductId"

                        if (Database.getRowCount(
                                "Partnership_Product",
                                "GlobalId",
                                c1.getAsString("GlobalId").toString()
                            ) == 0
                        )
                            Database.insertTo("Partnership_Product", c1, nul_field1)
                        else
                            Database.updateTo(
                                "Partnership_Product",
                                c1,
                                "GlobalId=?",
                                listOf(c1.getAsString("GlobalId")).toTypedArray()
                            )
                    }
                }
            }
        }).start()
    }
}