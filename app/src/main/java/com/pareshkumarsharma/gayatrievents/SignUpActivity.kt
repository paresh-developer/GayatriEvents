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
            runOnUiThread {
                Thread {
                    if (APICalls.register(
                            edTName.text.toString().trim(),
                            edTMobile.text.toString().trim(),
                            edTEmail.text.toString().trim(),
                            edTPassword.text.toString().trim()
                        )
                    ) {
                        MainActivity.UserName = edTName.text.toString()
                        MainActivity.IsLoginDone = 4
                        finish()
                    }
                    else{
                        Snackbar.make(findViewById(R.id.mainSignUpLayout),APICalls.lastCallMessage,Snackbar.LENGTH_LONG).show()
                    }
                }.start();
            }
        }
    }
}