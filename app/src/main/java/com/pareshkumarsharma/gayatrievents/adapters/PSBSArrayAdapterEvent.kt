package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.activities.ServiceEdit
import com.pareshkumarsharma.gayatrievents.panchang.Month
import com.pareshkumarsharma.gayatrievents.panchang.Paksha
import com.pareshkumarsharma.gayatrievents.panchang.WeekDay


class PSBSArrayAdapterEvent(
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
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item_event,
                parent,
                false
            )
        }

        val txtTitle =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceTitle)
        val txtDesc =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceDescription)
        val txtDateStartEnd =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventDateStartEnd)
        val txtEventRegisteredOn =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventRegisteredOn)
        val txtApproval =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtApproved)
        val txtEventPrice =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventPrice)
        val txtEventId =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtEventId)

        try {

            txtTitle?.text = data[position][6]
            txtDesc?.text = data[position][7]
            if(data[position][8]=="0")
                txtDateStartEnd?.text = "Date Fixed :- "+data[position][9].substring(0,10)
            else
                txtDateStartEnd?.text = "Date From :- "+data[position][9].substring(0,10)+" to "+data[position][10].substring(0,10)
            txtEventRegisteredOn?.text = "Registered On : "+data[position][16].replace('T',' ')

            if((data[position][13]==null || data[position][13].startsWith("000")) && data[position][12]=="0")
                txtApproval?.setText("- Pending Approval - "+data[position][17])
            else if(data[position][13]!=null && data[position][12]=="0")
                txtApproval?.setText("- Rejected - "+data[position][17])
            else if(data[position][13]!=null && data[position][12]=="1")
                txtApproval?.setText("- Approved - "+data[position][17])

            txtEventPrice?.text = "Price :- "+data[position][11] + " /-"
            txtEventId?.text = "Event Id :- EV"+data[position][16].substring(2,4)+data[position][1].substring(3)
        }
        catch (Ex:Exception){
            txtTitle?.text = "Error"
            txtDesc?.text = "Error Message: "+Ex.message
        }

        return currentItemView!!
    }
}