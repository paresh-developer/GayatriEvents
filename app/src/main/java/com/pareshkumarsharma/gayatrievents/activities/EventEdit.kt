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
            builder.setTitle(existingEvents.Rows[i][6])
            builder.setMessage(existingEvents.Rows[i][7])
            if (!existingEvents.Rows[i][18].equals("1") && !existingEvents.Rows[i][18].equals("0")) {
//                builder.setNegativeButton(
//                    "Edit",
//                    DialogInterface.OnClickListener { dialogInterface, j ->
//                        NewEvent.Event_Id = existingEvents.Rows[i][0]
//                        NewEvent.Event_Global_Id = existingEvents.Rows[i][1]
//                        NewEvent.ServiceProduct_GlobalId = existingEvents.Rows[i][2]
//                        NewEvent.ServiceProduct_Id = existingEvents.Rows[i][3].toInt()
//                        NewEvent.Service_GlobalId = existingEvents.Rows[i][4]
//                        NewEvent.Service_Id = existingEvents.Rows[i][5].toInt()
//                        NewEvent.Event_Name = existingEvents.Rows[i][6]
//                        NewEvent.Event_Details = existingEvents.Rows[i][7]
//                        NewEvent.Event_Date_Fixed =
//                            existingEvents.Rows[i][8].toString().trim() == "1"
//                        NewEvent.Event_Date_Start = existingEvents.Rows[i][9]
//                        NewEvent.Event_Date_End = existingEvents.Rows[i][10]
//                        NewEvent.Event_Price = existingEvents.Rows[i][11].toDouble()
//                        NewEvent.Operation = 'U'
//                        CurrentActivity.startActivity(Intent(CurrentActivity, NewEvent::class.java))
//                    })
                if(existingEvents.Rows[i][18].equals("1") && existingEvents.Rows[i][18].equals("1")){
                    // TODO: Create refund logic for 50%
                }
                else {
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
                                        0,
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
            if (existingEvents.Rows[i][19].toShort() != 2.toShort() && existingEvents.Rows[i][18].toInt() <= existingEvents.Rows[i][12].toInt()) {
                var prices = 0.0F
                for (pri in existingEvents.Rows[i][11].split(',')) {
                    prices += pri.trim().toFloat()
                }
                val refId = existingEvents.Rows[i][1]
                val refName = existingEvents.Rows[i][6]
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
                    c.put("Title", res[i].EventName)
                    c.put("Details", res[i].EventDetails)
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
                    c.put("DateFixed", res[i].EventDateFixed)
                    c.put("DateStart", res[i].EventDateStart)
                    c.put("DateEnd", res[i].EventDateEnd)
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