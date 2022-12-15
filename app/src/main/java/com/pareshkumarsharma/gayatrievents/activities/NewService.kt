package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterSpinner
import com.pareshkumarsharma.gayatrievents.api.model.ServiceRegistrationRequestModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database

class NewService : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_service)

        if(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)!=2){
            onBackPressed()
            finish()
        }

        val spinerServiceType = findViewById<Spinner>(R.id.spinner_ServiceTypelist)
        val spinerCity = findViewById<Spinner>(R.id.spinner_City)

        val DataForList = Database.getServiceTypes()
        val arrOfList = mutableListOf<String>()
        for(row in DataForList.Rows){
            arrOfList.add(row[1])
        }

        val adapterServiceType = PSBSArrayAdapterSpinner(this,R.layout.spinnerview_item,arrOfList)

        spinerServiceType.adapter = adapterServiceType

        val DataForList1 = Database.getCities()
        val arrOfList1 = mutableListOf<String>()
        for(row in DataForList1.Rows){
            arrOfList1.add(row[1]+", "+row[2]+", "+row[3])
        }
        val adapterCity = PSBSArrayAdapterSpinner(this,R.layout.spinnerview_item,arrOfList1)

        spinerCity.adapter = adapterCity
        spinerCity.setSelection(arrOfList1.indexOf("Bhavnagar, GJ, IN"))

        findViewById<Button>(R.id.btnNewServiceRequestSubmit).setOnClickListener {
            findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = false
            val ServiceType = spinerServiceType.selectedItemPosition+1
            val ServiceTitle = findViewById<EditText>(R.id.edt_ServiceTitle).text.toString()
            val ServiceDesc = findViewById<EditText>(R.id.edt_ServiceDescription).text.toString()
            val ServiceAdd = findViewById<EditText>(R.id.edt_ServiceAddress).text.toString()
            val selectedCity = spinerCity.selectedItem.toString()
            val selectedId = arrOfList1.indexOf(selectedCity)
            val selectedInDb = DataForList1.Rows[selectedId][0].toString().toInt()
            val ServiceCity = selectedInDb
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
                if(APICalls.requestNewServiceRegistration(ServiceRegistrationRequestModel(
                     ServiceTitle, ServiceDesc,ServiceType,ServiceAdd,ServiceCity
                ))){
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            APICalls.lastCallMessage,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
                else{
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            APICalls.lastCallMessage,
                            Toast.LENGTH_LONG
                        ).show()
                        findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = true
                    }
                }
            }).start()
        }
    }
}