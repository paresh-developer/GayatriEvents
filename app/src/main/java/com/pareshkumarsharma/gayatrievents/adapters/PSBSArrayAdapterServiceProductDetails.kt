package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView


internal class PSBSArrayAdapterServiceProductDetails(
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
                com.pareshkumarsharma.gayatrievents.R.layout.listview_item_service_product_details,
                parent,
                false
            )
        }

        val txtTitle =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductDetailTitle)
        val txtDesc =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductDetailsDescription)
        val txtOwnerAt =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductDetailAt)

        val listViewDesc = currentItemView?.findViewById<ListView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductDetailsDescriptionListView)

        try {
            var titletxt = ""
            if(data[position][4].toInt()==3)
                titletxt = "Input : "
            titletxt += data[position][2]
            txtTitle?.text = titletxt
//            txtTitle?.text = data[position][2]
            txtOwnerAt?.text = " On "+data[position][5].replace('T',' ')

            if(data[position][4].toInt()==1){
                txtDesc?.text = data[position][3]
            }
            else if(data[position][4].toInt()==2) {
                txtDesc?.text = "> "+data[position][3].replace("\n","\n> ")
            }
        }
        catch (Ex:Exception){
            txtTitle?.text = "Error"
            txtDesc?.text = "Error Message: "+Ex.message
        }

        return currentItemView!!
    }
}