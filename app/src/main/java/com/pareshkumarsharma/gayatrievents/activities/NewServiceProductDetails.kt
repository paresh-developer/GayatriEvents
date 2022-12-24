package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductDetailsRegistrationModel
import com.pareshkumarsharma.gayatrievents.api.model.ServiceProductRegistrationModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database

class NewServiceProductDetails : AppCompatActivity() {
    internal companion object{
        var selectedServiceProductId:Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_service_product_details)

        if(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)!=2){
            onBackPressed()
            finish()
        }

        findViewById<Button>(R.id.btnNewServiceProductSubmit).setOnClickListener {
            findViewById<Button>(R.id.btnNewServiceProductSubmit).isEnabled = false
            val Title = findViewById<EditText>(R.id.edt_ServiceProductTitle).text.toString()
            val Desc = findViewById<EditText>(R.id.edt_ServiceProductDescription).text.toString()
            var Type = 1
            if(findViewById<RadioButton>(R.id.chk_ParagraphType).isChecked)
                Type = 1
            else if (findViewById<RadioButton>(R.id.chk_ListviewType).isChecked)
                Type = 2

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
                if(APICalls.requestNewServiceProductDetailsRegistration
                        (ServiceProductDetailsRegistrationModel(Title,Desc,Type,
                        selectedServiceProductId))
                ){
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
                        findViewById<Button>(R.id.btnNewServiceProductSubmit).isEnabled = true
                    }
                }
            }).start()
        }
    }
}