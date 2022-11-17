package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            MainActivity.IsLoginDone = 0
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}