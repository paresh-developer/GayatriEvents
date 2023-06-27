package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.UserRegisterModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import com.pareshkumarsharma.gayatrievents.utilities.GlobalData

internal class LoginActivity : AppCompatActivity() {

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
                        APICalls.encodeStringComplex(editTextPassword.text.toString().trim())
                    )
                ) {
                    val userModel = APICalls.lastCallObject as UserRegisterModel
                    val string_array = userModel.User_Password.trim('#').split('#')

                    if (APICalls.cookies.containsKey("token"))
                        getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                            .edit()
                            .putString("token", APICalls.cookies["token"])
                            .putString("expires", APICalls.cookies["expires"])
                            .putString("LLUname",userModel.User_Email)
                            .putString("LLName",userModel.User_Name)
                            .putString("LLMobile",userModel.User_Mobile)
                            .putString("LLPassword",userModel.User_Password)
                            .putInt("LLUType",userModel.User_Type)
                            .putBoolean("LLDone",true)
                            .apply()

                    val values = ContentValues()
                    values.put("Uname",userModel.User_Name)
                    values.put("Email",userModel.User_Email)
                    values.put("Mobile",userModel.User_Mobile)
                    values.put("User_Password",userModel.User_Password)
                    values.put("User_Type",1)
                    values.put("GlobalId",userModel.User_GlobalId)

                    if(Database.query("Select * From USERS where EMAIL = '${userModel.User_Email}' Or Mobile = '${userModel.User_Mobile}'").Rows.size==0) {
                        Database.insertTo("USERS", values, "Id")
                    }
                    else{
                        Database.updateTo("USERS",values,"GlobalId = ?",listOf(userModel.User_GlobalId).toTypedArray())
                    }

                    runOnUiThread { GlobalData.setUserGlobalId(getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)) }

                    if(!MainActivity.IsRunning)
                        startActivity(Intent(applicationContext, MainActivity::class.java))
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

        findViewById<TextView>(R.id.tv_link_privacyPolice).setOnClickListener {
            startActivity(Intent(this, PrivacyPolicy::class.java))
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}