package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    lateinit var editTextEmailMobile: EditText
    lateinit var editTextPassword: EditText

    lateinit var loginThread: Thread

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
            var errorstr = ""
            if (editTextEmailMobile.text.toString().trim().length == 0)
                errorstr += "Invalid Email or Mobile\n"
            if (editTextPassword.toString().trim().length == 0)
                errorstr += "Invalid password\n"

            if (errorstr.trim().length != 0) {
                Snackbar.make(
                    findViewById(R.id.mainLoginActivity), errorstr,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            loginThread = Thread {
                APICalls.setContext(applicationContext)
                if (APICalls.login(
                        editTextEmailMobile.text.toString().trim(),
                        editTextEmailMobile.text.toString().trim(),
                        editTextPassword.text.toString().trim()
                    )
                ) {
                    val userModel = APICalls.lastCallObject as UserRegisterModel
                    val string_array = userModel.User_Password.trim('#').split('#')
                    val newPassStr = ByteArray(string_array.size)
                    for (i in 0..string_array.size - 1)
                        newPassStr[i] =
                            Math.round((((string_array[i].toDouble() + 90.0) / 34.0) - 88) * 55)
                                .toInt().toByte()
                    userModel.User_Password = newPassStr.decodeToString()

                    if (APICalls.cookies.containsKey("token"))
                        getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                            .edit()
                            .putString("token", APICalls.cookies["token"])
                            .putString("expires", APICalls.cookies["expires"])
                            .apply()

                    MainActivity.IsLoginDone = 2
                    finish()
                } else {
                    runOnUiThread {
                        Snackbar.make(
                            findViewById(R.id.mainLoginActivity), APICalls.lastCallMessage,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                runOnUiThread {
                    btnLogin.isEnabled = true
                    btnSignUp.isEnabled = true
                    findViewById<TextView>(R.id.txtForgotPassword).visibility = View.VISIBLE
                    txtProcessing.visibility = View.GONE
                }
            }
            loginThread.start()

            runOnUiThread {
                btnLogin.isEnabled = false
                btnSignUp.isEnabled = false
                findViewById<TextView>(R.id.txtForgotPassword).visibility = View.INVISIBLE
                txtProcessing.visibility = View.VISIBLE
            }
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        findViewById<TextView>(R.id.txtForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }



    override fun onBackPressed() {
        finish()
    }
}