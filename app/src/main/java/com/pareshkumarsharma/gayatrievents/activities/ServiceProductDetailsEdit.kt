package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterService
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterServiceProduct
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls

class ServiceProductDetailsEdit : AppCompatActivity() {

    internal companion object{
        var selectedServiceId:Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_product_details_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnCreateNewServiceProduct).setOnClickListener {
            // TODO: Create new service
            NewServiceProduct.selectedServiceId = selectedServiceId
            startActivity(Intent(this, NewServiceProduct::class.java))
        }

        val exitingServices = Database.getServices()
        val listView = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
        val adapterService =
            PSBSArrayAdapterServiceProduct(this, R.layout.listview_item_service_product, exitingServices.Rows)
        listView.adapter = adapterService
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
            if (APICalls.getExistingServiceProductOfCurrentUser(selectedServiceId)) {
                val res = APICalls.lastCallObject as Array<ServiceProductDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("ServiceGlobalId", res[i].ServiceGlobalId)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("Price", res[i].Price)
                    c.put("CreationDate", res[i].CreationDate)
                    c.put("ServiceId", selectedServiceId)
                    if (Database.getRowCount(
                            "Service_Product",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service_Product", c, "Id")
                }
                val exitingServices = Database.getServicesProduct(selectedServiceId)
                runOnUiThread {
                    val listView = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
                    val adapterService = PSBSArrayAdapterServiceProduct(
                        this,
                        R.layout.listview_item_service_product,
                        exitingServices.Rows
                    )
                    listView.adapter = adapterService
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