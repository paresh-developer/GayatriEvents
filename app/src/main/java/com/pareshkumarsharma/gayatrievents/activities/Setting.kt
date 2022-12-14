package com.pareshkumarsharma.gayatrievents.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.UserTypeChangeRequestModel

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        if(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)==2){
            findViewById<Button>(R.id.btnChangeRequest_UserType).visibility = View.GONE
        }

        findViewById<Button>(R.id.btnChangeRequest_UserType).setOnClickListener {
            val UserOldType = getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)
            if(UserOldType < 1) {
                Toast.makeText(
                    applicationContext,
                    "You can't change the user type",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            else if(UserOldType != 1){
                Toast.makeText(
                    applicationContext,
                    "You are already changed your user type",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            else if(UserOldType == 1) {
                if (APICalls.requestUserTypeChange(UserTypeChangeRequestModel(UserOldType, 2))){
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}