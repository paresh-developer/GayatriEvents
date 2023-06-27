package com.pareshkumarsharma.gayatrievents

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.utilities.DataTable

class Product_Input : AppCompatActivity() {
    internal companion object {
        var PRODUCT_GLOBAL_ID = ""
        var PRODUCT_NAME = ""
        var INPUT_FIELDS = DataTable(mutableListOf(), mutableListOf(),"")
    }

    lateinit var tv_product_name : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_input)

        val mll = findViewById<LinearLayout>(R.id.input_field_layout)

        tv_product_name = findViewById(R.id.product_name)
        tv_product_name.setText(PRODUCT_NAME)

        val ll = LinearLayout(this)
        ll.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        ll.orientation = LinearLayout.HORIZONTAL

        for (fl in INPUT_FIELDS.Rows) {

            val tv = TextView(this)
            tv.setText("> "+fl[2]+" :- ")
            tv.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tv.setTextColor(Color.BLACK)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

            ll.addView(tv)

            val tv1 = EditText(this)
            tv1.setHint(fl[3])
            tv1.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            tv1.setTextColor(Color.BLACK)
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

            ll.addView(tv1)

        }

        mll.addView(ll)
    }
}