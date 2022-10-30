package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    lateinit var editTextEmailMobile: EditText
    lateinit var editTextPassword: EditText

    lateinit var loginThread : Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        MainActivity.IsLoginDone = 1


        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        val txtProcessing = findViewById<TextView>(R.id.txtProcessing)

        editTextEmailMobile = findViewById(R.id.editMobileEmail)
        editTextPassword = findViewById(R.id.editPassword)

        btnLogin.setOnClickListener {
            runOnUiThread {
                loginThread = Thread {
                    if (APICalls.login(
                            editTextEmailMobile.text.toString().trim(),
                            editTextEmailMobile.text.toString().trim(),
                            editTextPassword.text.toString().trim()
                        )
                    ) {
                        MainActivity.IsLoginDone = 2
                        finish()
                    }
                    else{
                        Snackbar.make(findViewById(R.id.mainLoginActivity),APICalls.lastCallMessage,
                            Snackbar.LENGTH_LONG).show()
                    }
                    runOnUiThread { btnLogin.isEnabled = true
                        btnSignUp.isEnabled = true
                        txtProcessing.visibility = View.GONE}
                }
                loginThread.start()
                btnLogin.isEnabled = false
                btnSignUp.isEnabled = false
                txtProcessing.visibility = View.VISIBLE
            }
        }


        btnSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

    override fun onBackPressed() {
        finish()
    }
}