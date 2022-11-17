package com.pareshkumarsharma.gayatrievents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        findViewById<Button>(R.id.btnChangeRequest_UserType).setOnClickListener {
            Toast.makeText(this,"This app is in Development",Toast.LENGTH_LONG).show()
        }
    }
}