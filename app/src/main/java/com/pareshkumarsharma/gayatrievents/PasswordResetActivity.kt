package com.pareshkumarsharma.gayatrievents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class PasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        val la = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            listOf("Email", "Mobile")
        )

        val edt = findViewById<EditText>(R.id.edt_Password_Reset_Request)
        val edt1 = findViewById<EditText>(R.id.edt_New_Password)
        val edt2 = findViewById<EditText>(R.id.edt_New_Password_Confirm)
        val spin = findViewById<Spinner>(R.id.spinnerView)

        spin.adapter = la
        spin.setSelection(ForgotPasswordActivity.selectedRequestType)
        spin.isEnabled = false

        val btnResetRequest = findViewById<Button>(R.id.btn_RequestForReset)
        btnResetRequest.setOnClickListener {
            var errorstr = ""
            if(edt.text.toString().trim().length == 0)
                errorstr += "Invalid code\n"
            if(edt1.text.toString().trim().length == 0)
                errorstr += "Invalid password\n"
            if(edt2.text.toString().trim().length == 0)
                errorstr += "Invalid confirm password\n"
            if(edt1.text.toString()!=edt2.text.toString())
                errorstr += "password should be match with confirm password\n"

            if(errorstr.trim().length != 0){
                Toast.makeText(this, errorstr, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }


            val T = Thread {
                val model = PasswordResetRequestModel(
                    "",
                    "",
                    edt1.text.toString(),
                    spin.selectedItemPosition == 0,
                    edt.text.toString(),
                    ForgotPasswordActivity.resetRequestId
                )
                APICalls.setContext(applicationContext)
                if (APICalls.passwordReset(model)) {
                    runOnUiThread {
                        Toast.makeText(this, "Password Updated successfully", Toast.LENGTH_LONG)
                            .show()
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