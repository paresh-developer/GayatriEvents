package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterService
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterServiceProduct
import com.pareshkumarsharma.gayatrievents.api.model.ServiceDisplayModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable

class ServiceProductEdit : AppCompatActivity() {


    internal val CurrentActivity = this

    private lateinit var adapterService: PSBSArrayAdapterServiceProduct
    private lateinit var listViewServiceProduct: ListView
    private lateinit var existingServiceProducts : DataTable

    internal companion object{
        var selectedServiceId:String = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_product_edit)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnCreateNewServiceProduct).setOnClickListener {
            NewServiceProduct.selectedServiceId = selectedServiceId
            startActivity(Intent(this, NewServiceProduct::class.java))
        }

        existingServiceProducts = Database.getServicesProduct(selectedServiceId)
        listViewServiceProduct = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
        adapterService =
            PSBSArrayAdapterServiceProduct(this, R.layout.listview_item_service_product, existingServiceProducts.Rows)
        listViewServiceProduct.adapter = adapterService

        listViewServiceProduct.setOnItemClickListener { adapterView, view, i, l ->
            ServiceProductDetailsEdit.selectedServiceProductId = existingServiceProducts.Rows[i][existingServiceProducts.Columns.indexOf("GlobalId")]
            val builder = AlertDialog.Builder(this)
            builder.setTitle(existingServiceProducts.Rows[i][2].toString())
            builder.setMessage(existingServiceProducts.Rows[i][3].toString())
            builder.setPositiveButton(
                "Edit",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    NewServiceProduct.selectedServiceId = selectedServiceId
                    NewServiceProduct.operation = 'U'
                    NewServiceProduct.GlobalId = existingServiceProducts.Rows[i][1]
                    NewServiceProduct.SPT = existingServiceProducts.Rows[i][2]
                    NewServiceProduct.SPD = existingServiceProducts.Rows[i][3]
                    NewServiceProduct.SPP = existingServiceProducts.Rows[i][4].toFloat()
                    CurrentActivity.startActivity(Intent(CurrentActivity,NewServiceProduct::class.java))
                })
            builder.setNeutralButton(
                "Details",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    val inn = Intent(CurrentActivity, ServiceProductDetailsEdit::class.java)
                    CurrentActivity.startActivity(inn)
                })
            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialogInterface, j -> })
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
                    else
                        Database.updateTo("Service_Product", c,"GlobalId=?",listOf(res[i].GlobalId).toTypedArray())
                }
                existingServiceProducts = Database.getServicesProduct(selectedServiceId)
                runOnUiThread {
                    listViewServiceProduct = findViewById<ListView>(R.id.listview_ExistingServicesProduct)
                    adapterService = PSBSArrayAdapterServiceProduct(
                        this,
                        R.layout.listview_item_service_product,
                        existingServiceProducts.Rows
                    )
                    listViewServiceProduct.adapter = adapterService
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