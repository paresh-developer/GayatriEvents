package com.pareshkumarsharma.gayatrievents.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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

    lateinit var btn_clear:Button
    lateinit var rdo_grp:RadioGroup
    lateinit var rdo_monthly:RadioButton
    lateinit var rdo_agyaras:RadioButton
    lateinit var rdo_pitar:RadioButton

    lateinit var donationTitleData : MutableList<String>
    lateinit var donationDescData : MutableList<String>
    lateinit var donationPriceData : MutableList<Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        txt_motive = findViewById<EditText>(R.id.edt_Title)
        txt_amount = findViewById<EditText>(R.id.edt_amount)
        btn_pay = findViewById<Button>(R.id.btn_process_donate_pay)
        txt_desc = findViewById<EditText>(R.id.edt_desc)

        btn_clear = findViewById(R.id.btn_remove_selected)
        rdo_grp = findViewById(R.id.rdo_grp_donation)
        rdo_monthly = findViewById(R.id.rdo_btn_monthly)
        rdo_agyaras = findViewById(R.id.rdo_btn_agyaras)
        rdo_pitar = findViewById(R.id.rdo_btn_pitar)

        btn_pay.setOnClickListener {
            btn_pay.isEnabled = false
            requestDonation(DonationRequestModel(txt_motive.text.toString(),txt_desc.text.toString(),txt_amount.text.toString().toFloat()))
            btn_pay.isEnabled = true
        }

        btn_clear.setOnClickListener {
            rdo_grp.clearCheck()
            txt_motive.text.clear()
            txt_desc.text.clear()
            txt_amount.text.clear()
            btn_clear.isEnabled = false
        }

        donationTitleData = mutableListOf()
        donationDescData = mutableListOf()
        donationPriceData = mutableListOf()

        donationTitleData.add("मासिक दान")
        donationTitleData.add("अग्यारस दान")
        donationTitleData.add("पित्रौकी प्रसंनता हेतु दान")

        donationDescData.add("एक मास तक आटा, दाल, चावल व अन्य जीवन जरुरी वस्तुके लीये")
        donationDescData.add("अग्यारसी आटा, फल व अन्य सात्विक वस्तुके लीये")
        donationDescData.add("पित्रोकी पसंदीदा वस्तु और वस्त्रो हेतु")

        donationPriceData.add(250.00F)
        donationPriceData.add(100.00F)
        donationPriceData.add(250.00F)

        rdo_grp.setOnCheckedChangeListener { radioGroup, i ->
            btn_clear.isEnabled = true
            if(i == rdo_monthly.id){
                txt_motive.setText(donationTitleData[0])
                txt_desc.setText(donationDescData[0])
                txt_amount.setText(donationPriceData[0].toString())
            } else if(i == rdo_agyaras.id){
                txt_motive.setText(donationTitleData[1])
                txt_desc.setText(donationDescData[1])
                txt_amount.setText(donationPriceData[1].toString())
            } else if(i == rdo_pitar.id){
                txt_motive.setText(donationTitleData[2])
                txt_desc.setText(donationDescData[2])
                txt_amount.setText(donationPriceData[2].toString())
            }
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