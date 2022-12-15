package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.panchang.Month
import com.pareshkumarsharma.gayatrievents.panchang.Paksha
import com.pareshkumarsharma.gayatrievents.panchang.WeekDay


class PSBSArrayAdapter(
    val c: Context,
    val r: Int,
    var data: Array<List<String>>, var colNames: List<String>
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals

    override fun isEmpty(): Boolean {
        return data[0].isEmpty()
    }

    override fun getCount(): Int {
        return data[0].size
    }

    internal fun UpdateData(d: Array<List<String>>, cols: List<String>) {
        data = d
        colNames = cols
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView which is recyclable view
        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(context).inflate(
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item,
                parent,
                false
            )
        }

        val txt1 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt1OfListViewItem)
        val txt2 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt2OfListViewItem)

        txt1?.text = colNames[position] + ": "
        txt2?.text = data[0][position].toString().replace("#~#", "\n").replace(", "," ")

        if (txt2?.text.toString().trim().length == 0)
            txt2?.text = "no data"

        when (colNames[position]) {
            "Paksha" -> txt2?.text = Paksha.get(txt2?.text.toString().toInt())
            "AmantMonth" -> txt2?.text = Month.get(txt2?.text.toString().toInt())
            "Weekday" -> txt2?.text = WeekDay.get(txt2?.text.toString().toInt())
            "Festivals" -> txt2?.text = txt2?.text.toString().replace(Regex("goo.gl/[a-zA-Z0-9]+"),"").replace("//","")
        }

        return currentItemView!!
    }
}