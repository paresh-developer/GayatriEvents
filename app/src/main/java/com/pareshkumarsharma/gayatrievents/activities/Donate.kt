package com.pareshkumarsharma.gayatrievents.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.DonationRequestModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls

class Donate : AppCompatActivity() {

    private val thisActivity = this
    private var selectedPaymentMethod = 0

    lateinit var txt_motive: EditText
    lateinit var txt_amount: EditText
    lateinit var btn_pay: Button
    lateinit var txt_desc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        txt_motive = findViewById<EditText>(R.id.edt_Title)
        txt_amount = findViewById<EditText>(R.id.edt_amount)
        btn_pay = findViewById<Button>(R.id.btn_process_donate_pay)
        txt_desc = findViewById<EditText>(R.id.edt_desc)

        btn_pay.setOnClickListener {
            btn_pay.isEnabled = false
            requestDonation(DonationRequestModel(txt_motive.text.toString(),txt_desc.text.toString(),txt_amount.text.toString().toFloat()))
            btn_pay.isEnabled = true
        }

    }

    fun requestDonation(
        model: DonationRequestModel
    ) {
        Thread(Runnable {
            if (APICalls.requestNewDonationRegistration(model)
            ) {
                NewPayment.RefId =
                    APICalls.lastCallMessage.split("Id:")[1].trim()
                        .trim('"')
                NewPayment.RefCode = 'D'
                NewPayment.RefName = txt_motive.text.toString()
                NewPayment.RefAmount = txt_amount.text.toString().toFloat()
                startActivity(Intent(this, NewPayment::class.java))
                finish()
            }
        }).start()
    }
}