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
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterDonation
import com.pareshkumarsharma.gayatrievents.api.model.DonationDisplayModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class DonationEdit : AppCompatActivity() {

    internal lateinit var adapter:PSBSArrayAdapterDonation
    internal lateinit var listView: ListView
    internal lateinit var tblData : DataTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_edit)

        findViewById<Button>(R.id.btnCreateNewDonation).setOnClickListener {
            startActivity(Intent(this,Donate::class.java))
        }

        listView = findViewById(R.id.listview_ExistingDonation)

        tblData = Database.getDonations()

        adapter = PSBSArrayAdapterDonation(this,R.layout.listview_item_donation,tblData.Rows)
        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle(tblData.Rows[i][2])
            builder.setMessage(tblData.Rows[i][3])
            if (tblData.Rows[i][8].toShort() != 2.toShort()) {
                NewPayment.RefId = tblData.Rows[i][1]
                NewPayment.RefCode = 'D'
                NewPayment.RefName = tblData.Rows[i][2]
                NewPayment.RefAmount = tblData.Rows[i][4].toFloat()
                builder.setPositiveButton(
                    "मुल्य चुकायें",
                    DialogInterface.OnClickListener { dialogInterface, i ->
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
        RefreshData()
        super.onResume()
    }

    fun RefreshData(){
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
            if (APICalls.getExistingDonationOfCurrentUser()) {
                val res = APICalls.lastCallObject as Array<DonationDisplayModel>
                for (i in 0..res.size - 1) {
                    var nul_field = "Id"
                    val c = ContentValues()
                    c.put("GlobalId", res[i].GlobalId)
                    c.put("UserGlobalId", res[i].UserGlobalId)
                    val tbl = Database.query("Select Id from Users where GlobalId = '${res[i].UserGlobalId}'")
                    if(tbl.Rows.size>0 && !tbl.Columns.contains("Error"))
                        c.put("UserId",tbl.Rows[0][0])
                    else
                        nul_field += ",UserId"
                    c.put("Motive", res[i].Motive)
                    c.put("Description", res[i].Desc)
                    c.put("Amount", res[i].Amount)
                    c.put("CreatedOn", res[i].CreatedOn)
                    c.put("PaymentStatus", res[i].PaymentStatus)

                    if (Database.getRowCount(
                            "Donation",
                            "GlobalId",
                            c.getAsString("GlobalId").toString()
                        ) == 0
                    )
                        Database.insertTo("Donation", c, nul_field)
                    else
                    {
                        Database.updateTo("Donation", c,"GlobalId=?",listOf(res[i].GlobalId).toTypedArray())
                    }

                }
                tblData = Database.getDonations()

                runOnUiThread {
                    adapter.updateData(tblData.Rows)
                    adapter.notifyDataSetChanged()
                    listView.deferNotifyDataSetChanged()
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