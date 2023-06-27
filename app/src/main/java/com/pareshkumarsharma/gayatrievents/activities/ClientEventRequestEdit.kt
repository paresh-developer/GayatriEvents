package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterClientRequest
import com.pareshkumarsharma.gayatrievents.api.model.EventDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.utilities.GlobalData


internal class ClientEventRequestEdit : AppCompatActivity() {

    private val CurrentActivity: ClientEventRequestEdit = this

    private lateinit var adapterClientEventRequests: PSBSArrayAdapterClientRequest
    private lateinit var listViewClientEventRequests: ListView
    private lateinit var existingClientRequests: DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_event_request_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        existingClientRequests = Database.getClientRequests(GlobalData.getUserGlobalId())
        listViewClientEventRequests = findViewById<ListView>(R.id.listview_ExistingServices)
        adapterClientEventRequests =
            PSBSArrayAdapterClientRequest(
                this,
                R.layout.listview_item_client_request,
                existingClientRequests.Rows
            )
        listViewClientEventRequests.adapter = adapterClientEventRequests
        listViewClientEventRequests.setOnItemClickListener { adapterView, view, i, l ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("GE : " + existingClientRequests.Rows[i][1])
            builder.setMessage(
                "\n\nउपसेवाए:\n" + existingClientRequests.Rows[i][existingClientRequests.Rows[i].size - 1].toString()
                    .replace(',', '\n')
            )
            val product_Col = existingClientRequests.Columns.indexOf("ProductName")
            val products = existingClientRequests.Rows[i][product_Col]
            val product_arr = products.split(",")
            val product_list = mutableListOf<String>()
            val bool_list = mutableListOf<Boolean>()
            val approval_list = mutableListOf<Int>()
            for (p in product_arr) {
                product_list.add(p.trim())
                bool_list.add(false)
                approval_list.add(0)
            }
            if (existingClientRequests.Rows[i][13].toInt() == 0) {
                builder.setNegativeButton(
                    "अस्विकार करे",
                    DialogInterface.OnClickListener { dialogInterface, j ->

                        val selectedProductList = mutableListOf<Int>()

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Select product")
                        builder.setMultiChoiceItems(
                            product_list.toTypedArray(),
                            bool_list.toBooleanArray(),
                            DialogInterface.OnMultiChoiceClickListener { dialogInterface, i, b ->
                                if (b) {
                                    if (!selectedProductList.contains(i))
                                        selectedProductList.add(i)
                                } else {
                                    if (selectedProductList.contains(i))
                                        selectedProductList.remove(i)
                                }
                            })
                        builder.setPositiveButton(
                            "Submit",
                            DialogInterface.OnClickListener { dialogInterface, i3 ->
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
                                    for (i2 in 0..approval_list.size-1) {
                                        if (selectedProductList.contains(i2))
                                            approval_list[i2] = 1
                                        else
                                            approval_list[i2] = 1
                                    }
                                    if (APICalls.sendClientEventRequestResponse(
                                            existingClientRequests.Rows[i][1],
                                            approval_list.joinToString(","),
                                            findViewById<EditText>(R.id.edt_Reason).text.toString()
                                        )
                                    ) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                applicationContext,
                                                APICalls.lastCallMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        RefreshData()
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
                            })
                        builder.show()
                    })
                builder.setPositiveButton(
                    "स्विकार करे",
                    DialogInterface.OnClickListener { dialogInterface, j ->
                        val selectedProductList = mutableListOf<Int>()

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Select product")
                        builder.setMultiChoiceItems(
                            product_list.toTypedArray(),
                            bool_list.toBooleanArray(),
                            DialogInterface.OnMultiChoiceClickListener { dialogInterface, i, b ->
                                if (b) {
                                    if (!selectedProductList.contains(i))
                                        selectedProductList.add(i)
                                } else {
                                    if (selectedProductList.contains(i))
                                        selectedProductList.remove(i)
                                }
                            })
                        builder.setPositiveButton(
                            "Submit",
                            DialogInterface.OnClickListener { dialogInterface, i3 ->
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
                                    for (i1 in 0..approval_list.size - 1) {
                                        if (selectedProductList.contains(i1))
                                            approval_list[i1] = 2
                                        else
                                            approval_list[i1] = 1
                                    }
                                    if (APICalls.sendClientEventRequestResponse(
                                            existingClientRequests.Rows[i][1],
                                            approval_list.joinToString(","),
                                            findViewById<EditText>(R.id.edt_Reason).text.toString()
                                        )
                                    ) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                applicationContext,
                                                APICalls.lastCallMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        RefreshData()
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
                            })
                        builder.show()
                    })
            }
            builder.setNeutralButton(
                "Call",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    dialogInterface.dismiss();
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${existingClientRequests.Rows[i][17]}")
                    startActivity(intent)
                })
            if (existingClientRequests.Rows[i][13].toInt() == 1) {
                builder.setNegativeButton(
                    "ओर्डर तैयार है",
                    DialogInterface.OnClickListener { dialogInterface, j ->
                    })
            }
            builder.show()
        }
    }


    override fun onResume() {
        RefreshData()
        super.onResume()
    }

    private fun RefreshData() {
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
            if (APICalls.getClientEventRequestsOfCurrentUser()) {
                val res = APICalls.lastCallObject as Array<EventDisplayModel>
                for (i in 0..res.size - 1) {
                    var nul_field = "Id"
                    val c = ContentValues()
                    c.put("GlobalId", res[i].EventGlobalId)
                    c.put("ServiceProductGlobalIdList", res[i].ServiceProductGlobalIdList)
                    val tbl = Database.query(
                        "Select group_concat(Id) from SERVICE_PRODUCT where GlobalId in ('${
                            res[i].ServiceProductGlobalIdList.replace(
                                ",",
                                "', '"
                            )
                        }')"
                    )
                    if (tbl.Rows.size > 0 && !tbl.Columns.contains("Error"))
                        c.put("ServiceProductIdList", tbl.Rows[0][0])
                    val tbl1 = Database.query(
                        "Select group_concat(Id) from SERVICE where GlobalId in ('${
                            res[i].ServiceGlobalIdList.replace(
                                ",",
                                "', '"
                            )
                        }')"
                    )
                    if (tbl1.Rows.size > 0 && !tbl1.Columns.contains("Error"))
                        c.put("ServiceIdList", tbl1.Rows[0][0])
                    c.put("ServiceGlobalIdList", res[i].ServiceGlobalIdList)
                    c.put("PriceList", res[i].EventPriceList)
                    c.put("Approved", res[i].Approved)
                    c.put("PaymentStatus", res[i].PaymentStatus)
                    c.put("RequestStatus", res[i].RequestStatus)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    c.put("UserTurn", res[i].UserTurn)
                    c.put("OrderReady", res[i].OrderRead)
                    c.put("ClientMobile", res[i].ClientMobile)
                    if (res[i].Reason != null)
                        c.put("Reason", res[i].Reason)
                    else
                        nul_field += ",Reason"
                    val tbl2 =
                        Database.query("Select Id from Users where GlobalId='${res[i].UserGlobalId}'")
                    if (tbl2.Rows.size > 0 && !tbl2.Columns.contains("Error"))
                        c.put("UserId", tbl2.Rows[0][0].toInt())
                    c.put("CreationDate", res[i].CreationTime)
                    if (res[i].Approval_Time != null)
                        c.put("Approval_Time", res[i].Approval_Time)
                    else
                        nul_field += ",ApprovalTime"
                    nul_field += ",ServiceProductId,ServiceId,UserId"

                    if (Database.getRowCount(
                            "Client_EVENTS_Request",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Client_EVENTS_Request", c, nul_field)
                    else {
                        Database.updateTo(
                            "Client_EVENTS_Request",
                            c,
                            "GlobalId=?",
                            listOf(res[i].EventGlobalId).toTypedArray()
                        )
                    }

                }
                existingClientRequests = Database.getClientRequests(GlobalData.getUserGlobalId())

                runOnUiThread {
                    adapterClientEventRequests.updateData(existingClientRequests.Rows)
                    adapterClientEventRequests.notifyDataSetChanged()
                    listViewClientEventRequests.deferNotifyDataSetChanged()
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