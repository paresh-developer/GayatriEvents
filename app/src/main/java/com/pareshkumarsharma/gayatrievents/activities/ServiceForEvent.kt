package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterServiceForEvent
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

internal class ServiceForEvent : AppCompatActivity() {

    private val CurrentActivity: ServiceForEvent = this
    private lateinit var adapterService: PSBSArrayAdapterServiceForEvent
    private lateinit var listViewService: ListView
    private lateinit var existingServices: DataTable

    internal companion object {
        var SelectedServiceIds = mutableListOf<String>()
        var SelectedServiceNames = mutableListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_for_event)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnSaveSelected).setOnClickListener {
            SelectedServiceIds = adapterService.Selected_Ids
            SelectedServiceNames = adapterService.Selected_Name
            val tl = Database.getServicesProductFilterByServiceList(
                SelectedServiceIds.joinToString(","),
                NewEvent.SelectedServiceProductIds.joinToString(",")
            )
            val tblrows_p_id = mutableListOf<String>()
            val tblrows_p_name = mutableListOf<String>()
            val tblrows_p_price = mutableListOf<Float>()
            for (r in tl.Rows) {
                tblrows_p_id.add(r[0].trim())
            }
            for (r in tl.Rows) {
                tblrows_p_name.add(r[1].trim())
            }
            for (r in tl.Rows) {
                tblrows_p_price.add(r[2].trim().toFloat())
            }
            NewEvent.SelectedServiceProductIds = tblrows_p_id.intersect(NewEvent.SelectedServiceProductIds.toSet()).toMutableList()
            NewEvent.SelectedServiceProductPriceList = tblrows_p_price.intersect(NewEvent.SelectedServiceProductPriceList.toSet()).toMutableList()
            ServiceProductForEvent.SelectedProductName = tblrows_p_name.intersect(ServiceProductForEvent.SelectedProductName.toSet()).toMutableList()
            ServiceProductForEvent.SelectedProductPrice = tblrows_p_price.intersect(NewEvent.SelectedServiceProductPriceList.toSet()).toMutableList()
            finish()
        }

        existingServices = Database.getServicesForEvent()
        listViewService = findViewById<ListView>(R.id.listview_ExistingServices)
        adapterService =
            PSBSArrayAdapterServiceForEvent(
                this,
                R.layout.listview_item_service_for_event,
                existingServices.Rows
            )
        adapterService.Selected_Ids = SelectedServiceIds
        listViewService.adapter = adapterService
        listViewService.setOnItemClickListener{ adapterView, view, i, l ->
//            NewEvent.Selected_Service_Global_Id = existingServices.Rows[i][1]
//            Toast.makeText(applicationContext,"Service selected",Toast.LENGTH_LONG).show()
//            finish()
//            ServiceProductEdit.selectedServiceId = existingServices.Rows[i][existingServices.Columns.indexOf("Id")].toInt()
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle(existingServices.Rows[i][2])
//            builder.setMessage(existingServices.Rows[i][3])
//            builder.setNegativeButton("Edit", DialogInterface.OnClickListener { dialogInterface, j ->
//                NewService.SGLB = existingServices.Rows[i][1]
//                NewService.ST = existingServices.Rows[i][6]
//                NewService.STL = existingServices.Rows[i][2]
//                NewService.SD = existingServices.Rows[i][3]
//                NewService.SC = existingServices.Rows[i][7]
//                NewService.SA = existingServices.Rows[i][8]
//                NewService.operation = 'U'
//                CurrentActivity.startActivity(Intent(CurrentActivity,NewService::class.java))
//            })
//            builder.setPositiveButton("Products", DialogInterface.OnClickListener { dialogInterface, j ->
//                val inn = Intent(CurrentActivity,ServiceProductEdit::class.java)
//                CurrentActivity.startActivity(inn)
//            })
//            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialogInterface, j ->  })
//            builder.show()
        }
    }


    override fun onResume() {
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
            if (APICalls.getExistingServiceForEvent()) {
                val res = APICalls.lastCallObject as Array<ServiceDisplayModel>
                for (i in 0..res.size - 1) {
                    var nul_field = "Id"
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
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

                }
                existingServices = Database.getServicesForEvent()

                runOnUiThread {
                    adapterService.updateData(existingServices.Rows)
                    adapterService.notifyDataSetChanged()
                    listViewService.deferNotifyDataSetChanged()
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

        super.onResume()
    }
}