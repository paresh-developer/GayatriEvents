package com.pareshkumarsharma.gayatrievents.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.utilities.LogManagement

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        findViewById<EditText>(R.id.editTextTextMultiLine).setText(LogManagement.ReadLogs())
    }
}