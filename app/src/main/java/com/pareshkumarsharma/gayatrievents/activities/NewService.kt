package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterSpinner
import com.pareshkumarsharma.gayatrievents.api.model.ServiceRegistrationRequestModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceUpdationRequestModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class NewService : AppCompatActivity() {

    internal companion object {
        var operation: Char = 'I'
        var ST: String = ""
        var STL: String = ""
        var SD: String = ""
        var SA: String = ""
        var SC: String = ""
        var SGLB:String = ""
    }

    lateinit var cities:DataTable
    lateinit var serviceTypes:DataTable
    lateinit var city_arr:MutableList<String>
    lateinit var serviceType_arr:MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_service)

        if (getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType", 0) != 2) {
            onBackPressed()
            finish()
        }

        val spinerServiceType = findViewById<Spinner>(R.id.spinner_ServiceTypelist)
        val spinerCity = findViewById<Spinner>(R.id.spinner_City)

        serviceTypes = Database.getServiceTypes()
        serviceType_arr = mutableListOf<String>()
        for (row in serviceTypes.Rows) {
            serviceType_arr.add(row[1])
        }

        val adapterServiceType = PSBSArrayAdapterSpinner(this, R.layout.spinnerview_item, serviceType_arr)

        spinerServiceType.adapter = adapterServiceType

        cities = Database.getCities()
        city_arr = mutableListOf<String>()
        for (row in cities.Rows) {
            city_arr.add(row[1] + ", " + row[2] + ", " + row[3])
        }
        val adapterCity = PSBSArrayAdapterSpinner(this, R.layout.spinnerview_item, city_arr)

        spinerCity.adapter = adapterCity
        spinerCity.setSelection(city_arr.indexOf("Bhavnagar, GJ, IN"))

        if(operation == 'U'){
            findViewById<Button>(R.id.btnNewServiceRequestSubmit).text = "Update"
            spinerServiceType.setSelection(serviceType_arr.indexOf(ST))
            spinerCity.setSelection(city_arr.indexOf(SC))
            findViewById<EditText>(R.id.edt_ServiceTitle).setText(STL)
            findViewById<EditText>(R.id.edt_ServiceDescription).setText(SD)
            findViewById<EditText>(R.id.edt_ServiceAddress).setText(SA)
        }

        findViewById<Button>(R.id.btnNewServiceRequestSubmit).setOnClickListener {
            findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = false
            val ServiceType = spinerServiceType.selectedItemPosition + 1
            val ServiceTitle = findViewById<EditText>(R.id.edt_ServiceTitle).text.toString()
            val ServiceDesc = findViewById<EditText>(R.id.edt_ServiceDescription).text.toString()
            val ServiceAdd = findViewById<EditText>(R.id.edt_ServiceAddress).text.toString()
            val selectedCity = spinerCity.selectedItem.toString()
            val selectedId = city_arr.indexOf(selectedCity)
            val selectedInDb = cities.Rows[selectedId][0].toString().toInt()
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

                // region Insert On Web
                if (operation == 'I') {
                    if (APICalls.requestNewServiceRegistration(
                            ServiceRegistrationRequestModel(
                                ServiceTitle, ServiceDesc, ServiceType, ServiceAdd, ServiceCity
                            )
                        )
                    ) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = true
                        }
                    }
                }
                // endregion

                // region Update On Web
                else if (operation == 'U') {
                    if (APICalls.requestNewServiceUpdation(
                            ServiceUpdationRequestModel(
                                SGLB,ServiceTitle, ServiceDesc, ServiceType, ServiceAdd, ServiceCity
                            )
                        )
                    ) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = true
                        }
                    }
                }
                // endregion
            }).start()
        }
    }

    override fun onDestroy() {
        operation = 'I'
        super.onDestroy()
    }
}