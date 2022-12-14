package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapterSpinner
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

        var DataForList1 = Database.getCities()
        var arrOfList1 = mutableListOf<String>()
        for(row in DataForList1.Rows){
            arrOfList1.add(row[1]+", "+row[2]+", "+row[3])
        }
        val adapterCity = PSBSArrayAdapterSpinner(this,R.layout.spinnerview_item,arrOfList1)

        spinerCity.adapter = adapterCity
    }
}