package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.pareshkumarsharma.gayatrievents.activities.MainActivity
import com.pareshkumarsharma.gayatrievents.panchang.Month
import com.pareshkumarsharma.gayatrievents.panchang.Paksha
import com.pareshkumarsharma.gayatrievents.panchang.WeekDay
import com.pareshkumarsharma.gayatrievents.utilities.Database


internal class PSBSArrayAdapterServiceProductForEvent(
    val c: Context,
    val r: Int,
    var data: List<List<String>>
) : ArrayAdapter<List<String>>(c, r, data) {
    //var Identity = 0 // for panchang 1 for festivals
    var SelectedProductId = mutableListOf<String>()
    var SelectedProductNames = mutableListOf<String>()
    var SelectedProductPrices = mutableListOf<String>()
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
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductTitle)
        val txtDesc =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductDescription)
        val txtOwnerAt =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductAt)

        val txtPrice =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.txtServiceProductPrice)
        val chkSelection =
            currentItemView?.findViewById<CheckBox>(com.pareshkumarsharma.gayatrievents.R.id.chkSelection)
//        val lstView_productDetails =
//            currentItemView?.findViewById<ListView>(com.pareshkumarsharma.gayatrievents.R.id.lst_product_details)
//        val btnMoreDetails =
//            currentItemView?.findViewById<Button>(com.pareshkumarsharma.gayatrievents.R.id.btn_more_details)
//
//        btnMoreDetails?.setOnClickListener {
//            val p = position
//            if(btnMoreDetails?.text.toString().contains("More")) {
//                btnMoreDetails?.text = "Less Details⬆️"
//                val tbl = Database.getServicesProductDetails(data[position][1])
//                val adapter = parent?.let { it1 ->
//                    PSBSArrayAdapter_Product_Details(
//                        it1.context,
//                        android.R.layout.simple_spinner_item,
//                        tbl.Rows.toTypedArray(),
//                        tbl.Columns
//                    )
//                }
//                lstView_productDetails?.visibility = View.VISIBLE
//                lstView_productDetails?.adapter = adapter
//            }
//            else{
//                btnMoreDetails?.text = "More Details⬇️"
//                lstView_productDetails?.visibility = View.GONE
//            }
//        }

        chkSelection?.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                if(!SelectedProductId.contains(data[position][1]))
                    SelectedProductId.add(data[position][1])
                if(!SelectedProductNames.contains(data[position][2]))
                    SelectedProductNames.add(data[position][2])
                if(!SelectedProductPrices.contains(data[position][4]))
                    SelectedProductPrices.add(data[position][4])
            }
            else{
                if(SelectedProductId.contains(data[position][1]))
                    SelectedProductId.remove(data[position][1])
                if(SelectedProductNames.contains(data[position][2]))
                    SelectedProductNames.remove(data[position][2])
                if(SelectedProductPrices.contains(data[position][4]))
                    SelectedProductPrices.remove(data[position][4])
            }
        }

        chkSelection?.isChecked = SelectedProductId.contains(data[position][1])
        if(chkSelection?.isChecked==true){
            if(!SelectedProductNames.contains(data[position][2]))
                SelectedProductNames.add(data[position][2])
            if(!SelectedProductPrices.contains(data[position][4]))
                SelectedProductPrices.add(data[position][4])
        }

        try {
            txtTitle?.text = data[position][2]
            txtDesc?.text = data[position][3]
//            txtOwnerAt?.text = " On "+data[position][5].replace('T',' ')
//            txtPrice?.text = "Price: ₹ "+data[position][4].toFloat()
            txtOwnerAt?.text = "Price: ₹ "+data[position][4].toFloat()
        }
        catch (Ex:Exception){
            txtTitle?.text = "Error"
            txtDesc?.text = "Error Message: "+Ex.message
        }

        return currentItemView!!
    }
}