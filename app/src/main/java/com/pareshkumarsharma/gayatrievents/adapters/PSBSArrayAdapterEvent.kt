package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


internal class PSBSArrayAdapterEvent(
    val c: Context,
    val r: Int,
    var data: List<List<String>>
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
    }

    internal fun updateData(dataRows: List<List<String>>) {
        data = dataRows
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView which is recyclable view
        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(context).inflate(
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item_event,
                parent,
                false
            )
        }

        val txtTitle =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceTitle)
//        val txtDesc =
//            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceDescription)
//        val txtDateStartEnd =
//            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventDateStartEnd)
        val txtEventRegisteredOn =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventRegisteredOn)
        val txtApproval =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtApproved)
        val txtEventPrice =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventPrice)
        val txtEventId =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventId)
        val txtPayment =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtPayment)

        try {

            txtTitle?.text = data[position][6]
//            txtDesc?.text = "> विवरण :- " + data[position][7].replace(
//                "\n",
//                " "
//            ) + "\n> उपसेवाए :- " + data[position][data[position].size - 1]

//            if (data[position][8] == "1")
//                txtDateStartEnd?.text = "निश्र्चित तारीख :- " + data[position][9].substring(0, 10)
//            else
//                txtDateStartEnd?.text = "अनिश्र्चित तारीख सीमा :- " + data[position][9].substring(
//                    0,
//                    10
//                ) + " से " + data[position][10].substring(0, 10) + " तक"

            txtEventRegisteredOn?.text =
                "दर्ज कीया है तारीख : " + data[position][13].replace('T', ' ').substring(0, 10)

            txtApproval?.setTextColor(Color.BLACK)

            txtPayment?.setTextColor(Color.BLACK)
            var paymentStatus = "मुल्य चुकाना बाकी है"
            if (data[position][16] == "2") {
                paymentStatus = "मुल्य चुका दिया गया है"
                txtPayment?.setTextColor(Color.BLUE)
            }

            txtPayment?.text = paymentStatus

            if (data[position][15] == "0")
                txtApproval?.text = "- स्विकार अपुर्ण - " + data[position][14]
            else if (data[position][15] == "1" && data[position][9] == "0") {
                txtApproval?.text = "- अस्विकार - " + data[position][14]
                txtApproval?.setTextColor(Color.RED)
            } else if (data[position][15] == "1" && data[position][9] == "1") {
                txtApproval?.text = "- स्विकारीत - " + data[position][14]
                txtApproval?.setTextColor(Color.BLUE)
            }

            var sum_price = 0.0
            for (pri in data[position][8].split(',')) {
                sum_price += pri.trim().toFloat()
            }

            txtEventPrice?.text = "मुल्य :- " + Math.ceil(sum_price) + " /-"
            txtEventId?.text = "प्रसंग नं :- EV" + data[position][13].substring(
                2,
                4
            ) + data[position][1].substring(3)
        } catch (Ex: Exception) {
            txtTitle?.text = "Error"
//            txtDesc?.text = "Error Message: " + Ex.message
        }

        return currentItemView!!
    }
}