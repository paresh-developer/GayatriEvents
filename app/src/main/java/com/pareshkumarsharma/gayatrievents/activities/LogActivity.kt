package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.utilities.LogManagement

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        findViewById<EditText>(R.id.editTextTextMultiLine).setText(LogManagement.ReadLogs())

        findViewById<Button>(R.id.btn_Clear).setOnClickListener {
            findViewById<EditText>(R.id.editTextTextMultiLine).text.clear()
        }
    }
}