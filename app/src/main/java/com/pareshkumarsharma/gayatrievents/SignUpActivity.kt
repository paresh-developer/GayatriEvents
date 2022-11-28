package com.pareshkumarsharma.gayatrievents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class SignUpActivity : AppCompatActivity() {
    lateinit var edTName: EditText
    lateinit var edTMobile: EditText
    lateinit var edTEmail: EditText
    lateinit var edTPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edTName = findViewById(R.id.editName)
        edTMobile = findViewById(R.id.editMobile)
        edTEmail = findViewById(R.id.editEmail)
        edTPassword = findViewById(R.id.editPassword)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            var errorstr = ""
            if(edTName.toString().trim().length == 0)
                errorstr += "Invalid Name\n"
            if(edTMobile.text.toString().trim().length == 0)
                errorstr += "Invalid Mobile\n"
            if(edTEmail.toString().trim().length == 0)
                errorstr += "Invalid Email\n"
            if(edTPassword.toString().trim().length == 0)
                errorstr += "Invalid Password\n"

            if(errorstr.trim().length != 0)
            {
                Snackbar.make(
                    findViewById(R.id.mainSignUpLayout),
                    errorstr,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            Thread {
                APICalls.setContext(applicationContext)
                if (APICalls.register(
                        edTName.text.toString().trim(),
                        edTMobile.text.toString().trim(),
                        edTEmail.text.toString().trim(),
                       APICalls.encodeString(edTPassword.text.toString().trim())
                    )
                ) {
                    MainActivity.IsLoginDone = 4
                    runOnUiThread {
                        MainActivity.UserName = edTName.text.toString()
                        finish()
                    }
                }
                else{
                    runOnUiThread {
                        Snackbar.make(
                            findViewById(R.id.mainSignUpLayout),
                            APICalls.lastCallMessage,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        }
    }
}