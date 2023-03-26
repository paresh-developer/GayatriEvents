package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.panchang.EnWeekDay


internal class PSBSFestivalArrayAdapter(
    val c: Context,
    val r: Int,
    var data: Array<List<String>>, var colNames: List<String>,
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
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
                com.pareshkumarsharma.gayatrievents.R.layout.listview_festival_item,
                parent,
                false
            )
        }

        val txt1 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt1OfListViewItem)
        val txt2 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txt2OfListViewItem)
        val txt3 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtWeekday)

        txt1?.text = data[position][0].toString().substring(0,2) + " " + EnWeekDay.get(data[position][3].toInt()).substring(0,3)
        txt2?.text = data[position][1].toString().replace(Regex("goo.gl/[a-zA-Z0-9]+"),"").replace("#~#", "\n").replace("//","")
        txt3?.text = data[position][2].replace("#~#","\n").replace("upto ","")
        return currentItemView!!
    }
}