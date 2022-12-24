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
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterServiceProductDetails
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDetailDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable

class ServiceProductDetailsEdit : AppCompatActivity() {

    internal companion object{
        var selectedServiceProductId:Int = 0
    }

    private lateinit var adapterServiceProductDetails: PSBSArrayAdapterServiceProductDetails
    private lateinit var listViewServiceProductDetail: ListView
    private lateinit var existingServiceProductDetails : DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_product_details_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnCreateNewServiceProductDetails).setOnClickListener {
            NewServiceProductDetails.selectedServiceProductId = selectedServiceProductId
            startActivity(Intent(this, NewServiceProductDetails::class.java))
        }

        existingServiceProductDetails = Database.getServicesProductDetails(selectedServiceProductId)
        listViewServiceProductDetail = findViewById<ListView>(R.id.listview_ExistingServicesProductDetails)
        val adapterService =
            PSBSArrayAdapterServiceProductDetails(this, R.layout.listview_item_service_product_details, existingServiceProductDetails.Rows)
        listViewServiceProductDetail.adapter = adapterService
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
            if (APICalls.getExistingServiceProductDetailOfCurrentUser(selectedServiceProductId)) {
                val res = APICalls.lastCallObject as Array<ServiceProductDetailDisplayModel>
                for (i in 0..res.size - 1) {
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("ServiceProductGlobalId", res[i].ServiceProductGlobalId)
                    c.put("Title", res[i].Title)
                    c.put("SmallDesc", res[i].Desc)
                    c.put("Type", res[i].Type)
                    c.put("CreationDate", res[i].CreationDate)
                    c.put("ServiceProductId", selectedServiceProductId)
                    if (Database.getRowCount(
                            "Service_Product_Detail",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Service_Product_Detail", c, "Id")
                }
                existingServiceProductDetails = Database.getServicesProductDetails(selectedServiceProductId)
                runOnUiThread {
                    listViewServiceProductDetail = findViewById<ListView>(R.id.listview_ExistingServicesProductDetails)
                    adapterServiceProductDetails = PSBSArrayAdapterServiceProductDetails(
                        this,
                        R.layout.listview_item_service_product_details,
                        existingServiceProductDetails.Rows
                    )
                    listViewServiceProductDetail.adapter = adapterServiceProductDetails
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