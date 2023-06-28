package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterEvent
import com.pareshkumarsharma.gayatrievents.api.model.EventDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.utilities.GlobalData

internal class EventEdit : AppCompatActivity() {

    private val CurrentActivity: EventEdit = this

    private lateinit var adapterEvents: PSBSArrayAdapterEvent
    private lateinit var listViewEvents: ListView
    private lateinit var existingEvents: DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_edit)

        findViewById<Button>(R.id.btnCreateNewService).setOnClickListener {
            NewEvent.Operation = 'I'
            startActivity(Intent(this, NewEvent::class.java))
        }

        existingEvents = Database.getEvents(GlobalData.getUserGlobalId())
        listViewEvents = findViewById<ListView>(R.id.listview_ExistingEvents)
        adapterEvents =
            PSBSArrayAdapterEvent(this, R.layout.listview_item_event, existingEvents.Rows)
        listViewEvents.adapter = adapterEvents
        listViewEvents.setOnItemClickListener { adapterView, view, i, l ->
            ServiceProductEdit.selectedServiceId =
                existingEvents.Rows[i][existingEvents.Columns.indexOf("GlobalId")]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("GE : " + existingEvents.Rows[i][1])
            builder.setMessage(
                "\n\nउपसेवाए:\n" + existingEvents.Rows[i][existingEvents.Rows[i].size - 1].toString()
                    .replace(',', '\n')
            )
            if (!existingEvents.Rows[i][13].equals("1") && !existingEvents.Rows[i][13].equals("0")) {

                if (existingEvents.Rows[i][13].equals("1") && existingEvents.Rows[i][13].equals("1")) {
                    // TODO: Create refund logic for 50%
                } else {
                    builder.setNegativeButton(
                        "Delete",
                        DialogInterface.OnClickListener { dialogInterface, i123 ->
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
                                if (APICalls.sendDeleteEventRequest(
                                        existingEvents.Rows[i][1],
                                        "0",
                                        "Deleted By Client"
                                    )
                                ) {
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
                }
            }
            if (existingEvents.Rows[i][14].toShort() != 2.toShort()) {
                var prices = 0.0F
                for (pri in existingEvents.Rows[i][6].split(',')) {
                    prices += pri.trim().toFloat()
                }
                val refId = existingEvents.Rows[i][1]
                val refName = existingEvents.Rows[i][1]
                builder.setPositiveButton(
                    "मुल्य चुकायें",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        NewPayment.RefCode = 'E'
                        NewPayment.RefAmount = prices
                        NewPayment.RefId = refId
                        NewPayment.RefName = refName
                        startActivity(Intent(this, NewPayment::class.java))
                        dialogInterface.dismiss()
                    })
            }

            builder.setNeutralButton(
                "Ok",
                DialogInterface.OnClickListener { dialogInterface, j -> dialogInterface.dismiss() })
            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        RefreshData()
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
            if (APICalls.getExistingEventsOfCurrentUser()) {
                val res = APICalls.lastCallObject as Array<EventDisplayModel>
                for (i in 0..res.size - 1) {
                    var nul_field = "Id"
                    val c = ContentValues()
                    c.put("GlobalId", res[i].EventGlobalId)
                    c.put("ServiceProductGlobalIdList", res[i].ServiceProductGlobalIdList)
                    val tbl =
                        Database.query(
                            "Select group_concat(Id) from SERVICE_PRODUCT where GlobalId in ('${
                                res[i].ServiceProductGlobalIdList.replace(
                                    ",",
                                    "', '"
                                )
                            }')"
                        )
                    if (tbl.Rows.size > 0 && !tbl.Columns.contains("Error"))
                        c.put("ServiceProductIdList", tbl.Rows[0][0])
                    else
                        nul_field += ",ServiceProductIdList"

                    val tbl1 =
                        Database.query(
                            "Select group_concat(Id) from SERVICE where GlobalId='${
                                res[i].ServiceGlobalIdList.replace(
                                    ",",
                                    "', '"
                                )
                            }'"
                        )
                    if (tbl1.Rows.size > 0 && !tbl1.Columns.contains("Error"))
                        c.put("ServiceIdList", tbl1.Rows[0][0])
                    else
                        nul_field += ",ServiceIdList"

                    c.put("ServiceGlobalIdList", res[i].ServiceGlobalIdList)
                    c.put("PriceList", res[i].EventPriceList)
                    c.put("Approved", res[i].Approved)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    c.put("PaymentStatus", res[i].PaymentStatus)
                    c.put("RequestStatus", res[i].RequestStatus)
                    c.put("UserTurn", res[i].UserTurn)
                    if (res[i].Reason != null)
                        c.put("Reason", res[i].Reason)
                    else
                        nul_field += ",Reason"
                    val tbl2 =
                        Database.query("Select Id from Users where GlobalId='${res[i].UserGlobalId}'")
                    if (tbl2.Rows.size > 0 && !tbl2.Columns.contains("Error"))
                        c.put("UserId", tbl2.Rows[0][0].toInt())
                    else
                        nul_field += ",UserId"

                    c.put("CreationDate", res[i].CreationTime)
                    if (res[i].Approval_Time != null)
                        c.put("Approval_Time", res[i].Approval_Time)
                    else
                        nul_field += ",ApprovalTime"

                    if (Database.getRowCount(
                            "Events",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Events", c, nul_field)
                    else {
                        Database.updateTo(
                            "Events",
                            c,
                            "GlobalId=?",
                            listOf(res[i].EventGlobalId).toTypedArray()
                        )
                    }

                    if (res[i].InputFields != null) {
                        // all product + inputfields
                        for (j in 0..res[i].InputFields.size - 1) {
                            // all inputfields
                            for (k in 0..res[i].InputFields.values.elementAt(j).size - 1) {
                                // 1 inputfield
                                val cv1 = ContentValues()
                                var nul_field1 = "Id"
                                cv1.put("Event_Global_Id", res[i].EventGlobalId)
                                val eventId = Database.getRowCount("Select Id from EVENTS where GlobalId='${res[i].EventGlobalId}'")
                                if (eventId != 0)
                                    cv1.put("Event_Id", eventId)
                                else
                                    nul_field1 += ",Event_Id"
                                cv1.put("Product_Global_Id", res[i].InputFields.keys.elementAt(j))

                                val productId = Database.getRowCount("Select Id from Service_Product where GlobalId='${res[i].InputFields.keys.elementAt(j)}'")
                                if (productId != 0)
                                    cv1.put("Product_Id", productId)
                                else
                                    nul_field1 += ",Product_Id"

                                cv1.put(
                                    "SPD_Global_Id",
                                    res[i].InputFields.values.elementAt(j).keys.elementAt(k)
                                )
                                val SPDId = Database.getRowCount("Select Id from Service_Product_Detail where GlobalId='${res[i].InputFields.values.elementAt(j).keys.elementAt(k)}'")
                                if (SPDId != 0)
                                    cv1.put("SPD_Id", SPDId)
                                else
                                    nul_field1 += ",SPD_Id"

                                cv1.put(
                                    "Value",
                                    res[i].InputFields.values.elementAt(j).values.elementAt(k)
                                )

                                val cnt = Database.getRowCount(
                                    "Select count(id) From ProductFieldValues where Event_Global_Id = '${res[i].EventGlobalId}' and Product_Global_Id = '${
                                        res[i].InputFields.keys.elementAt(j)
                                    }' and SPD_Global_Id = '${
                                        res[i].InputFields.values.elementAt(j).keys.elementAt(
                                            k
                                        )
                                    }'"
                                )

                                if (cnt == 0) {
                                    Database.insertTo("ProductFieldValues", cv1, nul_field1)
                                } else {
                                    Database.updateTo(
                                        "ProductFieldValues",
                                        cv1,
                                        " Event_Global_Id = ? And Product_Global_Id = ? And SPD_Global_Id = ?",
                                        listOf(
                                            res[i].EventGlobalId,
                                            res[i].InputFields.keys.elementAt(j),
                                            res[i].InputFields.values.elementAt(j).keys.elementAt(k)
                                        ).toTypedArray()
                                    )
                                }
                            }
                        }
                    }
                }

                existingEvents = Database.getEvents(GlobalData.getUserGlobalId())

                runOnUiThread {
                    adapterEvents.updateData(existingEvents.Rows)
                    adapterEvents.notifyDataSetChanged()
                    listViewEvents.deferNotifyDataSetChanged()
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