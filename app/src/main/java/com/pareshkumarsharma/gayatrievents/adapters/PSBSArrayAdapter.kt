package com.pareshkumarsharma.gayatrievents.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pareshkumarsharma.gayatrievents.panchang.*
import com.pareshkumarsharma.gayatrievents.panchang.MonthHindi
import com.pareshkumarsharma.gayatrievents.panchang.PakshaHindi
import com.pareshkumarsharma.gayatrievents.panchang.WeekDay


internal class PSBSArrayAdapter(
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

        when(colNames[position]){
            "Tithi" -> txt1?.text = "તિથી : "
            "Weekday" -> txt1?.text = "વાર : "
            "Paksha" -> txt1?.text = "પક્ષ : "
            "AmantMonth" -> txt1?.text = "મહિનો : "
            "Festivals"-> txt1?.text = "તહેવાર : "
            "Sunrise" -> txt1?.text = "સુર્યોદય : "
            "Sunset" -> txt1?.text = "સુર્યાસ્ત : "
            "Nakshatra" -> txt1?.text = "નક્ષત્ર : "
            "Moonsign" -> txt1?.text = "ચંદ્ર રાશી : "
            "Sunsign" -> txt1?.text = "સુર્ય રાશી : "
            "Yoga" -> txt1?.text = "યોગ : "
            "Karan" -> txt1?.text = "કરણ : "
            "Moonrise" -> txt1?.text = "ચંદ્રોદય : "
            "Moonset" -> txt1?.text = "ચંદ્ર અસ્ત : "
            "VikramSamvat" -> txt1?.text = "વિક્રમ સંવત : "
            "ShakSamvat" -> txt1?.text = "શક સંવત : "
        }

        when (colNames[position]) {
            "Paksha" -> txt2?.text = PakshaHindi.get(txt2?.text.toString().toInt())
            "AmantMonth" -> txt2?.text = MonthHindi.get(txt2?.text.toString().toInt())
            "Weekday" -> txt2?.text = WeekDay.get(txt2?.text.toString().toInt())
            "Festivals" -> txt2?.text = txt2?.text.toString().replace(Regex("goo.gl/[a-zA-Z0-9]+"),"").replace("//","")
        }

        return currentItemView!!
    }
}