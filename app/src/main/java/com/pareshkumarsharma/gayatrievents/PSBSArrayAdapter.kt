package com.pareshkumarsharma.gayatrievents

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter


class PSBSArrayAdapter(val c:Context,
                       val r:Int,
                       val data:Array<List<List<String>>>) : ArrayAdapter<List<List<String>>>(c,r,data)
{
    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView which is recyclable view
        // convertView which is recyclable view
        var currentItemView = convertView

        // of the recyclable view is null then inflate the custom layout for the same

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(context).inflate(
                R.layout.,
                parent,
                false
            )
        }
        return super.getView(position, convertView, parent)
    }
}