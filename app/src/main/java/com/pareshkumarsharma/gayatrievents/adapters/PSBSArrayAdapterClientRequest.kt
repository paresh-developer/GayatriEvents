package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


internal class PSBSArrayAdapterClientRequest(
    val c: Context,
    val r: Int,
    var data: List<List<String>>
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals
    var SelectedPosition = -1
    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
    }

    internal fun updateData(dataRows:List<List<String>>){
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
                r,
                parent,
                false
            )
        }

        val txtTitle =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventTitle)
//        val txtDesc =
//            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventDescription)
//        val txtDateStartEnd =
//            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtDateStart_End)
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
//            txtTitle?.text = data[position][6]
//            txtDesc?.text = "> Desc. :- "+data[position][7].replace("\n"," ") + "\n> Prod. :- " + data[position][data[position].size-1]
//            if(data[position][8]=="1")
//                txtDateStartEnd?.text = "Date :- Fixed "+data[position][9].substring(0,10)
//            else
//                txtDateStartEnd?.text = "Date :- From "+data[position][9].substring(0,10)+" to "+data[position][10].substring(0,10)
//            txtEventRegisteredOn?.text = "Requested On :- "+data[position][16].replace('T',' ')
//
//            if((data[position][13]==null || data[position][13].startsWith("000")) && data[position][12]=="0")
//                txtApproval?.text = "- Pending Approval - "+data[position][17]
//            else if(data[position][13]!=null && data[position][12]=="0")
//                txtApproval?.text = "- Rejected - "+data[position][17]
//            else if(data[position][13]!=null && data[position][12]=="1")
//                txtApproval?.text = "- Approved - "+data[position][17]
//
//            var sum_price = 0.0
//            for (pri in data[position][11].split(',')){
//                sum_price += pri.trim().toFloat()
//            }
//            txtEventPrice?.text = "Price :- "+sum_price+ " /-"
//            txtEventId?.text = "Event Id :- EV"+data[position][16].substring(2,4)+data[position][1].substring(3)

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

            var str_turn = ""
            if(data[position][17]!= null && data[position][17].replace(",","").trim().length>0)
                str_turn = "बारी - "+data[position][17]
            txtEventPrice?.text = "मुल्य :- " + sum_price + " /-"
            txtEventId?.text = "प्रसंग नं :- EV" + data[position][13].substring(
                2,
                4
            ) + data[position][1].substring(3) + " " + str_turn
        }
        catch (Ex:Exception){
            txtTitle?.text = "Error"
//            txtDesc?.text = "Error Message: "+Ex.message
        }

        return currentItemView!!
    }
}