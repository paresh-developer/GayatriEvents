package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterService
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable

class ServiceEdit : AppCompatActivity() {

    private val CurrentActivity:ServiceEdit = this

    private lateinit var adapterService: PSBSArrayAdapterService
    private lateinit var listViewService: ListView
    private lateinit var existingServices : DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnCreateNewService).setOnClickListener {
            startActivity(Intent(this, NewService::class.java))
        }

        existingServices = Database.getServices()
        listViewService = findViewById<ListView>(R.id.listview_ExistingServices)
        adapterService =
            PSBSArrayAdapterService(this, R.layout.listview_item_service, existingServices.Rows)
        listViewService.adapter = adapterService
        listViewService.setOnItemClickListener { adapterView, view, i, l ->
            ServiceProductEdit.selectedServiceId = existingServices.Rows[i][existingServices.Columns.indexOf("GlobalId")]
            val builder = AlertDialog.Builder(this)
            builder.setTitle(existingServices.Rows[i][2])
            builder.setMessage(existingServices.Rows[i][3])
            builder.setNegativeButton("Edit", DialogInterface.OnClickListener { dialogInterface, j ->
                NewService.SGLB = existingServices.Rows[i][1]
                NewService.ST = existingServices.Rows[i][6]
                NewService.STL = existingServices.Rows[i][2]
                NewService.SD = existingServices.Rows[i][3]
                NewService.SC = existingServices.Rows[i][7]
                NewService.SA = existingServices.Rows[i][8]
                NewService.operation = 'U'
                CurrentActivity.startActivity(Intent(CurrentActivity,NewService::class.java))
            })
            builder.setPositiveButton("Products", DialogInterface.OnClickListener { dialogInterface, j ->
                val inn = Intent(CurrentActivity,ServiceProductEdit::class.java)
                CurrentActivity.startActivity(inn)
            })
            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialogInterface, j ->  })
            builder.show()
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
            if (APICalls.getExistingServiceOfCurrentUser()) {
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
                    if(res[i].Approved)
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
                    else
                    {
                        Database.updateTo("Service", c,"GlobalId=?",listOf(res[i].GlobalId).toTypedArray())
                    }

                }
                existingServices = Database.getServices()

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