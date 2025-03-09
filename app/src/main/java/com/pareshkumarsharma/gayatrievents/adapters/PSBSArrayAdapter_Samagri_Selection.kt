package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView


internal class PSBSArrayAdapter_Samagri_Selection(
    val c: Context,
    val r: Int,
    val data: MutableList<String>
) : ArrayAdapter<String>(c, r, data as MutableList<String>) {
    //var Identity = 0 // for panchang 1 for festivals
    val selections:MutableList<String> = mutableListOf()

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
                r,
                parent,
                false
            )
        }

        val txt1 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.userName)
        val checkBox =
            currentItemView?.findViewById<CheckBox>(com.pareshkumarsharma.gayatrievents.R.id.chk_User)

        checkBox?.isChecked = selections.contains(data[position])

        checkBox?.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                if(!selections.contains(data[position])){
                    selections.add(data[position])
                }
            }
            else{
                if(selections.contains(data[position])){
                    selections.remove(data[position])
                }
            }
        }

//        if(position==0){
//            txt1?.setTypeface(Typeface.DEFAULT,Typeface.BOLD)
//            txt2?.setTypeface(Typeface.DEFAULT,Typeface.BOLD)
//        }
//        else{
//            txt1?.setTypeface(Typeface.DEFAULT,Typeface.NORMAL)
//            txt2?.setTypeface(Typeface.DEFAULT,Typeface.NORMAL)
//        }

        txt1?.text = data[position]

        return currentItemView!!
    }
}