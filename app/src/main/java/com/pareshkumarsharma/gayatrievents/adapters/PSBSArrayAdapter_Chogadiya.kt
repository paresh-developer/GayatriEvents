package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


internal class PSBSArrayAdapter_Chogadiya(
    val c: Context,
    val r: Int,
    var chogadiya_data: List<String>, var time_data: List<Date>
) : ArrayAdapter<String>(c, r, chogadiya_data) {
    //var Identity = 0 // for panchang 1 for festivals

    override fun isEmpty(): Boolean {
        return chogadiya_data.isEmpty()
    }

    override fun getCount(): Int {
        return chogadiya_data.size
    }

    internal fun UpdateData(d: List<String>, cols: List<Date>) {
        chogadiya_data = d
        time_data = cols
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

        val txt1 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt1)
        val txt2 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt2)

        txt2?.text = SimpleDateFormat("hh:mm a").format(time_data[position]) + " से " +SimpleDateFormat("hh:mm a").format(time_data[position+1]) +" तक"
        txt1?.text = chogadiya_data[position]

        return currentItemView!!
    }
}