package com.pareshkumarsharma.gayatrievents.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_Samagri_Selection
import com.pareshkumarsharma.gayatrievents.databinding.ActivitySamagriScrollingBinding
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class SamagriScrollingActivity : AppCompatActivity() {

    companion object{
        val Selected_List_Items = mutableListOf<Int>()
    }

    private lateinit var binding: ActivitySamagriScrollingBinding
    private lateinit var db_eve: DataTable
    private lateinit var db_samagri: DataTable
    private lateinit var adapter_samagri: ArrayAdapter<String>
    private lateinit var adapter_samagri_item: PSBSArrayAdapter_Samagri_Selection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySamagriScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = "GE सामग्री"

        val layout_main = findViewById<NestedScrollView>(R.id.scrolling_activity_content_activity)
        val spiner_samagri = findViewById<Spinner>(R.id.spn_samagri_eve)
//        val lst_content = findViewById<ListView>(R.id.lst_content)
        val layout_content = findViewById<LinearLayout>(R.id.Scroling_Activity_Content)

        db_eve = Database.getSamagriEvents()

        val sm_ev = mutableListOf<String>()

        for (r in db_eve.Rows) {
            sm_ev.add(r[1])
        }

        val adapt_s_e = ArrayAdapter<String>(
            this, R.layout.spinnerview_item_for_samagri,
            sm_ev
        )
        spiner_samagri.adapter = adapt_s_e

//        adapter_samagri = ArrayAdapter(this, R.layout.spinnerview_item_for_samagri_eve, mutableListOf())
//        lstView.adapter = adapter_samagri

//        adapter_samagri_item = PSBSArrayAdapter_Samagri_Selection(application.baseContext,R.layout.listview_item_samagri_selection, mutableListOf<String>("",""))
//        lst_content.adapter = adapter_samagri_item

        spiner_samagri.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                binding.toolbarLayout.title = "GE: " + sm_ev[position]
                val event_id = db_eve.Rows[id.toInt()][0].toInt()
                db_samagri = Database.getSamagriEventsWise(event_id)
                val samagri = mutableListOf<String>()
                layout_content.removeView(layout_content.findViewById(R.id.samagri_item_list))
                val layout_lst_samagri_items = LinearLayout(applicationContext)
                layout_lst_samagri_items.layoutParams =
                    ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                layout_lst_samagri_items.orientation = LinearLayout.VERTICAL
                layout_lst_samagri_items.id = R.id.samagri_item_list
                layout_lst_samagri_items.setBackgroundResource(R.drawable.samagry_border)
                layout_lst_samagri_items.setPadding(20)
                var CountItem = 0
                for (r in db_samagri.Rows) {
                    if(!r[3].startsWith("--")){
                        CountItem++
                    }
                    samagri.add(r[3])
                    val layout_linear = LinearLayout(layout_lst_samagri_items.context)
                    layout_linear.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    layout_linear.orientation = LinearLayout.HORIZONTAL
                    val itemCheckBox = CheckBox(layout_lst_samagri_items.context)
                    itemCheckBox.id = r[0].toInt() + r[1].toInt()
                    val itemNameTextView = TextView(layout_lst_samagri_items.context)
                    itemCheckBox.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    itemCheckBox.scaleX = 1.5f
                    itemCheckBox.scaleY = 1.5f
                    itemNameTextView.layoutParams =
                        ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    itemNameTextView.labelFor = r[0].toInt() + r[1].toInt()
                    itemCheckBox.isFocusable = false
                    if(!r[3].startsWith("--")) {
                        itemNameTextView.text = "$CountItem.) ${r[3]}"
                    }else{
                        itemNameTextView.text = "${r[3]}"
                    }
                    itemNameTextView.typeface = Typeface.DEFAULT_BOLD
                    itemNameTextView.setTextColor(Color.BLACK)
                    itemCheckBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f)
                    itemNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f)
                    if(!r[3].startsWith("--")) {
                        layout_linear.addView(itemCheckBox, WRAP_CONTENT, WRAP_CONTENT)
                    }
                    layout_linear.addView(itemNameTextView, WRAP_CONTENT, WRAP_CONTENT)
                    layout_lst_samagri_items.addView(layout_linear)
                    if(Selected_List_Items.contains(itemCheckBox.id)){
                        itemCheckBox.isChecked = true
                    }
                    itemCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                        if (b){
                            if(!Selected_List_Items.contains(compoundButton.id)){
                                Selected_List_Items.add(compoundButton.id)
                            }
                        }
                        else{
                            if(Selected_List_Items.contains(compoundButton.id)){
                                Selected_List_Items.remove(compoundButton.id)
                            }
                        }
                    }
                }
                for (item in samagri) {

                }
                layout_content.addView(layout_lst_samagri_items)
                //samagri.joinToString("\n")
//                adapter_samagri_item.clear()
//                adapter_samagri_item.addAll(samagri)
//                adapter_samagri_item.notifyDataSetChanged()
//                lst_content.deferNotifyDataSetChanged()
//                val params = lst_content.layoutParams
//                params.height = (75 * samagri.size) + 10
//                lst_content.layoutParams = params
//                adapter_samagri.clear()
//                adapter_samagri.addAll(samagri)
//                adapter_samagri.notifyDataSetChanged()
//                lstView.deferNotifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}