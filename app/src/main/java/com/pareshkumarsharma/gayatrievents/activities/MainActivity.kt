package com.pareshkumarsharma.gayatrievents.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.R


class MainActivity : AppCompatActivity() {

    internal companion object {
        /**
         * 0 when no login
         * 1 when on Login screen
         * 2 when login done successfully
         * 3 when on Sign Up screen
         * 4 when Sign Up Done
         */
        var IsLoginDone = 0
        var UserName = ""
        lateinit var Toastmain:Toast
        lateinit var btnPanchang:Button
        var IsRunning = false
    }

    lateinit var txtHellow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IsRunning = true

        if(!getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("LLDone",false))
            startActivity(Intent(this, LoginActivity::class.java))
        else {
            IsLoginDone = 5
            UserName = getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getString("Uname","").toString()
        }
        txtHellow = findViewById(R.id.txtHellow)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        txtHellow.movementMethod = ScrollingMovementMethod()
        btnLogout.setOnClickListener {
            IsLoginDone = 0

            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                .edit()
                .putString("token", "")
                .putString("expires", "")
                .putString("LLUname","")
                .putString("LLMobile","")
                .putString("LLPassword","")
                .putBoolean("LLDone",false)
                .apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnPanchang = findViewById<Button>(R.id.btnPanchang)

        btnSetting.setOnClickListener {
            startActivity(Intent(this, Setting::class.java))
        }
        val packapath = this.packageName
        btnPanchang.setOnClickListener {
            startActivity(Intent(this, Panchang::class.java))
            IsLoginDone = 5
        }

        findViewById<Button>(R.id.btnService).setOnClickListener {
            if(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getInt("LLUType",0)==2) {
                startActivity(Intent(this, ServiceEdit::class.java))
            }
            else
                Toast.makeText(applicationContext,"You cannot access Services",Toast.LENGTH_LONG).show()
        }

        val DatabaseSetup = getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("F001",false)
        if(!DatabaseSetup)
        {
            Database.checkDatabaseSetup()
            getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                .edit()
                .putBoolean("F001",true)
                .apply()
        }

//        startActivity(Intent(this,Panchang::class.java))

//        if(Database.query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and not name = 'sqlite_sequence' and not name = 'android_metadata';").Rows[0][0].toString().toInt()>0)
//            Toast.makeText(this,"Tables Exists",Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()

        if (IsLoginDone != 1) {
            var snakmsg = ""
            if (IsLoginDone == 0)
                snakmsg = "Welcome back! 😎"
            else if (IsLoginDone == 2) {
                snakmsg = "Login Done!...👍"
                txtHellow.text = "Hellow! $UserName"
            }
            else if (IsLoginDone == 4) {
                snakmsg = "Sign Up Done!...👍"
            }
            if (IsLoginDone != 0 && snakmsg.trim().length>0)
                Snackbar.make(findViewById(R.id.mainActivityLayout), snakmsg, Snackbar.LENGTH_LONG)
                    .show()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        IsRunning = false
        super.onDestroy()
    }
}