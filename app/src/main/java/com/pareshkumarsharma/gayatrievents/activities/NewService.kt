package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
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

        var DataServiceType = Database.getServiceTypes()
        var arr_serviceType = mutableListOf<String>()
        for(rr in DataServiceType.Rows){
            arr_serviceType.add(rr[1])
        }

        val adapterServiceType = ArrayAdapter<String>(applicationContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arr_serviceType.toTypedArray())

        spinerServiceType.adapter = adapterServiceType

        DataServiceType = Database.getCities()
        arr_serviceType = mutableListOf<String>()
        for(rr in DataServiceType.Rows){
            arr_serviceType.add(rr[1]+", "+rr[2]+" ,"+rr[3])
        }

        val adapterCity = ArrayAdapter<String>(applicationContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arr_serviceType.toTypedArray())

        spinerCity.adapter = adapterCity
    }
}