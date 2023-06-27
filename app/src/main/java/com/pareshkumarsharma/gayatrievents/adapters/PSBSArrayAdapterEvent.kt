package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.utilities.LogManagement


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
            var product_status = "\n"
            val product_name = data[position][data[0].size - 1].split(',')
            val product_st = data[position][7].split(',')

            for (p in 0..product_name.size - 1) {
                product_status += product_name[p] + " "
                if (product_st.size > 1) {
                    if (product_st[p] == "0")
                        product_status += "Pending"
                    else if (product_st[p] == "1")
                        product_status += "Rejected"
                    else if (product_st[p] == "2")
                        product_status += "Approved"
                } else {
                    if (product_st[0] == "0")
                        product_status += "Pending"
                    else if (product_st[0] == "1")
                        product_status += "Rejected"
                    else if (product_st[0] == "2")
                        product_status += "Approved"
                }
                product_status += "\n"
            }

            txtEventRegisteredOn?.text =
                "तारीख : " + data[position][11].replace('T', ' ')
                    .substring(0, 10) + product_status

            txtApproval?.setTextColor(Color.BLACK)

            txtPayment?.setTextColor(Color.BLACK)
            var paymentStatus = "मुल्य चुकाना बाकी है"
            if (data[position][14] == "2") {
                paymentStatus = "मुल्य चुका दिया गया है"
                txtPayment?.setTextColor(Color.BLUE)
            }

            txtPayment?.text = paymentStatus

//            txtApproval?.text = "Sts: " + data[position][13] + " Apr: " + data[position][7]
            if (data[position][13] == "0")
                txtApproval?.text = "- Pending - " + data[position][12]
            else if (data[position][13] == "1")
                txtApproval?.text = data[position][12]
//            else if (data[position][13] == "1" && data[position][7] == "0") {
//                txtApproval?.text = "- अस्विकार - " + data[position][12]
//                txtApproval?.setTextColor(Color.RED)
//            } else if (data[position][13] == "1" && data[position][7] == "1") {
//                txtApproval?.text = "- स्विकारीत - " + data[position][12]
//                txtApproval?.setTextColor(Color.BLUE)
//            }

            var sum_price = 0.0
            for (pri in data[position][6].split(',')) {
                sum_price += pri.trim().toFloat()
            }

            txtEventPrice?.text = "मुल्य :- " + Math.ceil(sum_price) + " /-"
            txtEventId?.text = "प्रसंग नं :- EV" + data[position][11].substring(
                2,
                4
            ) + data[position][1].substring(3)
        } catch (Ex: Exception) {
            LogManagement.Log("Adapter error : ", Ex.message + "\n" + Ex.stackTraceToString())
        }

        return currentItemView!!
    }
}