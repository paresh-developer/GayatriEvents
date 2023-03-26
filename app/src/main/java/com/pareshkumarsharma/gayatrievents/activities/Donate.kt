package com.pareshkumarsharma.gayatrievents.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.utilities.PaymentManager

class Donate : AppCompatActivity() {

    private val thisActivity = this
    private var selectedPaymentMethod = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        val btn_pay = findViewById<Button>(R.id.btn_process_donate_pay)
        btn_pay.setOnClickListener {
            btn_pay.isEnabled = false
            val alert = AlertDialog.Builder(thisActivity)
            alert.setTitle("Select payment mode")
            alert.setSingleChoiceItems(
                listOf("Google Pay", "PayTM", "PhonePe").toTypedArray(),
                0,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    selectedPaymentMethod = i
                })
            alert.setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    Toast.makeText(thisActivity,"Processing Payment",Toast.LENGTH_SHORT).show()
                    when(selectedPaymentMethod){
                        0 -> PaymentManager.GooglePay(thisActivity)
                        1 -> PaymentManager.Paytm(thisActivity)
                        2 -> PaymentManager.PhonePe(thisActivity)
                    }
                })
            alert.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            alert.show()
        }

    }
}