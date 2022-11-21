package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.io.StringReader
import java.net.URL
import java.net.HttpURLConnection
import java.nio.file.Path
import java.security.MessageDigest
import java.util.Base64.Encoder


class MainActivity : AppCompatActivity() {

    companion object {
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
        lateinit var btnDashboard:Button
    }

    lateinit var txtHellow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this,LoginActivity::class.java))
//        runOnUiThread {
//            val m: ConnectionThread = ConnectionThread("PareshSharma")
//            m.start()
//        }

        txtHellow = findViewById(R.id.txtHellow)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        txtHellow.movementMethod = ScrollingMovementMethod()
        btnLogout.setOnClickListener {
            IsLoginDone = 0
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnDashboard = findViewById<Button>(R.id.btnDashboard)

        btnSetting.setOnClickListener {
            startActivity(Intent(this, Setting::class.java))
        }
        val packapath = this.packageName
        btnDashboard.setOnClickListener {
            startActivity(Intent(this,Dashboard::class.java))
//            val T = Thread{
//                val data = APICalls.downloadPanchang()
//                val f = File("/data/data/" + packapath + "/Panchang.db")
//                if (f.exists())
//                    f.delete()
//                f.createNewFile()
//                f.writeBytes(data)
//                runOnUiThread {
//                    Toastmain.show()
//                    btnDashboard.isEnabled = true
//                }
//            }
//            runOnUiThread {
//                T.start()
//                Toastmain = Toast.makeText(this, "File Downloaded successfully", Toast.LENGTH_LONG)
//                btnDashboard.isEnabled = false
//            }
        }

        if(Database.query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and not name = 'sqlite_sequence' and not name = 'android_metadata';").Rows[0][0].toString().toInt()>0)
            Toast.makeText(this,"Tables Exists",Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()

        if (IsLoginDone != 1) {
            var snakmsg = ""
            if (IsLoginDone == 0)
                snakmsg = "Welcome back! 😎"
            else if (IsLoginDone == 2)
                snakmsg = "Login Done!...👍"
            else if (IsLoginDone == 4)
                snakmsg = "Sign Up Done!...👍"

            if (IsLoginDone != 0)
                Snackbar.make(findViewById(R.id.mainActivityLayout), snakmsg, Snackbar.LENGTH_LONG)
                    .show()

            txtHellow.text = "Hellow! $UserName"
        } else {
            finish()
        }
    }
}