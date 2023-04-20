package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView


internal class PSBSArrayAdapter_User(
    val c: Context,
    val r: Int,
    var data: MutableList<MutableList<String>>
) : ArrayAdapter<List<String>>(c, r, data as List<List<String>>) {
    //var Identity = 0 // for panchang 1 for festivals

    lateinit var selections:MutableList<String>

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun getCount(): Int {
        return data.size
    }

    internal fun UpdateData(d: MutableList<MutableList<String>>) {
        data = d
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
        val txt2 =
            currentItemView?.findViewById<TextView>(com.pareshkumarsharma.gayatrievents.R.id.userGlobalId)
        val checkBox =
            currentItemView?.findViewById<CheckBox>(com.pareshkumarsharma.gayatrievents.R.id.chk_User)

        checkBox?.isChecked = selections.contains(data[position][3])

        checkBox?.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                if(!selections.contains(data[position][3])){
                    selections.add(data[position][3])
                }
            }
            else{
                if(selections.contains(data[position][3])){
                    selections.remove(data[position][3])
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

        txt1?.text = data[position][0]
        txt2?.text = data[position][1]

        return currentItemView!!
    }
}