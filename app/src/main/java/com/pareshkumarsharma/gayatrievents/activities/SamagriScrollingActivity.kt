package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_Samagri_Selection
import com.pareshkumarsharma.gayatrievents.databinding.ActivitySamagriScrollingBinding
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class SamagriScrollingActivity : AppCompatActivity() {

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
        val lst_content = findViewById<ListView>(R.id.lst_content)

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

        adapter_samagri_item = PSBSArrayAdapter_Samagri_Selection(application.baseContext,R.layout.listview_item_samagri_selection, mutableListOf<String>("",""))
        lst_content.adapter = adapter_samagri_item
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
                for (r in db_samagri.Rows) {
                    samagri.add(r[3])
                }
                //samagri.joinToString("\n")
                adapter_samagri_item.clear()
                adapter_samagri_item.addAll(samagri)
                adapter_samagri_item.notifyDataSetChanged()
                lst_content.deferNotifyDataSetChanged()
                val params = lst_content.layoutParams
                params.height = (75 * samagri.size) + 10
                lst_content.layoutParams = params
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