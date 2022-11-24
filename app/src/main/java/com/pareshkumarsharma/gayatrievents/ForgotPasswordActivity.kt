package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.ListAdapter

class ForgotPasswordActivity : AppCompatActivity() {

    companion object{
        internal var resetRequestId: Int = 0
        internal var selectedRequestType: Int = 0 // Email
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val la = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            listOf("Email", "Mobile")
        )
        val edt = findViewById<EditText>(R.id.edt_Password_Reset_Request)
        val spin = findViewById<Spinner>(R.id.spinnerView)
        spin.adapter = la
        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                edt.hint = if (p3 == 1L) "Enter Mobile" else "Enter Email"
                selectedRequestType = spin.selectedItemPosition
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val btnResetRequest = findViewById<Button>(R.id.btn_RequestForReset)
        btnResetRequest.setOnClickListener {
            val T = Thread {
                val model = PasswordResetRequestModel(
                    edt.text.toString(),
                    edt.text.toString(),
                    "",
                    spin.selectedItemPosition == 0,
                    "",
                    0
                )
                if (APICalls.requestForPasswordReset(model)) {
                    runOnUiThread {
                        Toast.makeText(this, "OTP sent", Toast.LENGTH_LONG)
                            .show()
                        APICalls.setContext(applicationContext)
                        resetRequestId = APICalls.lastCallMessage.trim('\"').toInt()
                        startActivity(Intent(this,PasswordResetActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, APICalls.lastCallMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
                runOnUiThread {
                    btnResetRequest.isEnabled = true
                }
            }
            T.start()
            btnResetRequest.isEnabled = false
        }
    }
}