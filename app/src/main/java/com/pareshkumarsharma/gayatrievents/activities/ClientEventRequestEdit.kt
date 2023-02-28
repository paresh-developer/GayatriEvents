package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterClientRequest
import com.pareshkumarsharma.gayatrievents.api.model.EventDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class ClientEventRequestEdit : AppCompatActivity() {

    private val CurrentActivity:ClientEventRequestEdit = this

    private lateinit var adapterClientEventRequests: PSBSArrayAdapterClientRequest
    private lateinit var listViewClientEventRequests: ListView
    private lateinit var existingClientRequests : DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_event_request_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        existingClientRequests = Database.getClientRequests()
        listViewClientEventRequests = findViewById<ListView>(R.id.listview_ExistingServices)
        adapterClientEventRequests =
            PSBSArrayAdapterClientRequest(this, R.layout.listview_item_client_request, existingClientRequests.Rows)
        listViewClientEventRequests.adapter = adapterClientEventRequests
        listViewClientEventRequests.setOnItemClickListener { adapterView, view, i, l ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle(existingClientRequests.Rows[i][6])
            builder.setMessage("Do you want to approve this event ?")
            builder.setNegativeButton("Reject", DialogInterface.OnClickListener { dialogInterface, j ->
//                NewService.SGLB = existingServices.Rows[i][1]
//                NewService.ST = existingServices.Rows[i][6]
//                NewService.STL = existingServices.Rows[i][2]
//                NewService.SD = existingServices.Rows[i][3]
//                NewService.SC = existingServices.Rows[i][7]
//                NewService.SA = existingServices.Rows[i][8]
//                NewService.operation = 'U'
//                CurrentActivity.startActivity(Intent(CurrentActivity,NewService::class.java))
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
                    if (APICalls.sendClientEventRequestResponse(existingClientRequests.Rows[i][1],0,findViewById<EditText>(R.id.edt_Reason).text.toString())) {
//                        runOnUiThread {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
                        RefreshData()
                    }
                    else{
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
            builder.setPositiveButton("Approve", DialogInterface.OnClickListener { dialogInterface, j ->
//                ServiceProductEdit.selectedServiceId = existingServices.Rows[i][existingServices.Columns.indexOf("Id")].toInt()
//                val inn = Intent(CurrentActivity,ServiceProductEdit::class.java)
//                CurrentActivity.startActivity(inn)
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
                    if (APICalls.sendClientEventRequestResponse(existingClientRequests.Rows[i][1],1,findViewById<EditText>(R.id.edt_Reason).text.toString())) {
//                        runOnUiThread {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
                        RefreshData()
                    }
                    else{
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
            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialogInterface, j ->  dialogInterface.dismiss()})
            builder.show()
        }
    }

    override fun onResume() {
        RefreshData()
        super.onResume()
    }

    private fun RefreshData(){
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
                    c.put("Title", res[i].EventName)
                    c.put("Details", res[i].EventDetails)
                    c.put("ServiceProductGlobalId", res[i].ServiceProductGlobalId)
                    val tbl = Database.query("Select Id from SERVICE_PRODUCT where GlobalId='${res[i].ServiceProductGlobalId}'")
                    if(tbl.Rows.size>0 && !tbl.Columns.contains("Error"))
                        c.put("ServiceProductId", tbl.Rows[0][0].toInt())
                    val tbl1 = Database.query("Select Id from SERVICE where GlobalId='${res[i].ServiceGlobalId}'")
                    if(tbl1.Rows.size>0 && !tbl1.Columns.contains("Error"))
                        c.put("ServiceId", tbl1.Rows[0][0].toInt())
                    c.put("ServiceGlobalId", res[i].ServiceGlobalId)
                    c.put("DateFixed", res[i].EventDateFixed)
                    c.put("DateStart", res[i].EventDateStart)
                    c.put("DateEnd", res[i].EventDateEnd)
                    c.put("Price", res[i].EventPrice)
                    c.put("Approved", res[i].Approved)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    if(res[i].Reason!=null)
                        c.put("Reason",res[i].Reason)
                    else
                        nul_field += ",Reason"
                    val tbl2 = Database.query("Select Id from Users where GlobalId='${res[i].UserGlobalId}'")
                    if(tbl2.Rows.size>0 && !tbl2.Columns.contains("Error"))
                        c.put("UserId", tbl2.Rows[0][0].toInt())
                    c.put("CreationDate", res[i].CreationTime)
                    if (res[i].Approval_Time!=null)
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
                existingClientRequests = Database.getClientRequests()

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