package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.panchang.Month
import com.pareshkumarsharma.gayatrievents.panchang.Paksha
import com.pareshkumarsharma.gayatrievents.panchang.WeekDay


class PSBSArrayAdapterService(
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
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item_service,
                parent,
                false
            )
        }

        val txtTitle =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceTitle)
        val txtDesc =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceDescription)
        val txtCity =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceCity)
        val txtType =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceType)
        val txtOwner =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceOwner)
        val txtApprov =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceAp)

        try {
            txtTitle?.text = data[position][2]
            txtDesc?.text = data[position][3]
            txtOwner?.text = "By: "+data[position][4]
            var dtime = ""
            if(data[position][9].toInt()==1) {
                txtApprov?.setTextColor(Color.rgb(0,100,0))
                dtime = " Approved On "+data[position][5].replace('T',' ')
            }
            else {
                if(data[position][10].toInt()==1) {
                    txtApprov?.setTextColor(Color.RED)
                    dtime = " Rejected"
                }
                else{
                    txtApprov?.setTextColor(Color.BLUE)
                    dtime = " Pending"
                }
            }
            txtApprov?.text = " "+dtime
            txtType?.text = "Type: "+data[position][6]
            txtCity?.text = " City: "+data[position][7]
        }
        catch (Ex:Exception){
            txtTitle?.text = "Error"
            txtDesc?.text = "Error Message: "+Ex.message
        }

        return currentItemView!!
    }
}