package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.HttpURLConnection


class MainActivity : AppCompatActivity() {

    companion object{
        /**
         * 0 when no login
         * 1 when on Login screen
         * 2 when login done successfully
         * 3 when on Sign Up screen
         * 4 when Sign Up Done
         */
        var IsLoginDone = 0
        var UserName = ""
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
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        if(IsLoginDone != 1) {
            var snakmsg = ""
            if (IsLoginDone == 0)
                snakmsg = "Welcome back! 😎"
            else if(IsLoginDone == 2)
                snakmsg = "Login Done!...👍"
            else if(IsLoginDone == 4)
                snakmsg = "Sign Up Done!...👍"

            if(IsLoginDone != 0)
                Snackbar.make(findViewById(R.id.mainActivityLayout),snakmsg, Snackbar.LENGTH_LONG).show()

            txtHellow.text = "Hellow! $UserName"
        }
        else{
            finish()
        }
    }
}