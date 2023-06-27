package com.pareshkumarsharma.gayatrievents

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.activities.NewEvent
import com.pareshkumarsharma.gayatrievents.utilities.DataTable

class Product_Input : AppCompatActivity() {
    internal companion object {
        var PRODUCT_GLOBAL_ID = ""
        var PRODUCT_NAME = ""
        var INPUT_FIELDS = DataTable(mutableListOf(), mutableListOf(), "")
    }

    lateinit var tv_product_name: TextView
    lateinit var btn_submit: Button

    var all_tv = mutableListOf<TextView>()
    var all_edt = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_input)

        val mll = findViewById<LinearLayout>(R.id.input_field_layout)

        tv_product_name = findViewById(R.id.product_name)
        tv_product_name.setText(PRODUCT_NAME)

        val ll = LinearLayout(this)
        ll.layoutParams =
            ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ll.orientation = LinearLayout.HORIZONTAL

        if (NewEvent.INPUT_FILEDS[PRODUCT_GLOBAL_ID] == null)
            NewEvent.INPUT_FILEDS[PRODUCT_GLOBAL_ID] = mutableListOf<String>()
        else
            NewEvent.INPUT_FILEDS[PRODUCT_GLOBAL_ID]?.clear()

        for (fl in INPUT_FIELDS.Rows) {

            NewEvent.INPUT_FILEDS[PRODUCT_GLOBAL_ID]?.add(fl[1])

            val tv = TextView(this)
            tv.setText("> " + fl[2] + " :- ")
            tv.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tv.setTextColor(Color.BLACK)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

            all_tv.add(tv)
            ll.addView(tv)

            val tv1 = EditText(this)
            tv1.setHint(fl[3])
            tv1.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            tv1.setTextColor(Color.BLACK)
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

            all_edt.add(tv1)
            ll.addView(tv1)
        }

        mll.addView(ll)

        btn_submit = findViewById(R.id.btn_submit)
        btn_submit.setOnClickListener {
            if(NewEvent.INPUT_FIELDS_VALUES[PRODUCT_GLOBAL_ID]==null)
                NewEvent.INPUT_FIELDS_VALUES[PRODUCT_GLOBAL_ID] = mutableListOf()
            else
                NewEvent.INPUT_FIELDS_VALUES[PRODUCT_GLOBAL_ID]?.clear()

            for (vl in all_edt) {
                NewEvent.INPUT_FIELDS_VALUES[PRODUCT_GLOBAL_ID]?.add(vl.text.toString())
            }
        }
    }
}