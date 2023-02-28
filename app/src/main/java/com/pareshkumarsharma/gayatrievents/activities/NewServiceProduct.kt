package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductRegistrationModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductUpdationModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database

class NewServiceProduct : AppCompatActivity() {

    internal companion object{
        var selectedServiceId:String = "0"
        var operation: Char = 'I'
        var SPT = ""
        var SPD = ""
        var SPP:Float = 0.0f
        var GlobalId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_service_product)

        if(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)!=2){
            onBackPressed()
            finish()
        }

        if(operation == 'U'){
            findViewById<Button>(R.id.btnNewServiceProductSubmit).text = "Update"
            findViewById<EditText>(R.id.edt_ServiceProductTitle).setText(SPT)
            findViewById<EditText>(R.id.edt_ServiceProductDescription).setText(SPD)
            findViewById<EditText>(R.id.edt_ServiceProductPrise).setText(SPP.toString())
        }

        findViewById<Button>(R.id.btnNewServiceProductSubmit).setOnClickListener {
            findViewById<Button>(R.id.btnNewServiceProductSubmit).isEnabled = false
            val ServiceProductTitle = findViewById<EditText>(R.id.edt_ServiceProductTitle).text.toString()
            val ServiceProductDesc = findViewById<EditText>(R.id.edt_ServiceProductDescription).text.toString()
            val ServiceProductPrice = findViewById<EditText>(R.id.edt_ServiceProductPrise).text.toString().toFloat()

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
                if(operation=='I') {
                    if (APICalls.requestNewServiceProductRegistration
                            (
                            ServiceProductRegistrationModel(
                                ServiceProductTitle,
                                ServiceProductDesc,
                                ServiceProductPrice,
                                selectedServiceId
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
                            findViewById<Button>(R.id.btnNewServiceProductSubmit).isEnabled = true
                        }
                    }
                }
                else if(operation == 'U'){
                    if (APICalls.requestServiceProductUpdation
                            (
                            ServiceProductUpdationModel(
                                GlobalId,
                                ServiceProductTitle,
                                ServiceProductDesc,
                                ServiceProductPrice,
                                selectedServiceId
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
                            findViewById<Button>(R.id.btnNewServiceProductSubmit).isEnabled = true
                        }
                    }
                }
            }).start()
        }
    }

    override fun onDestroy() {
        operation = 'I'
        super.onDestroy()
    }
}