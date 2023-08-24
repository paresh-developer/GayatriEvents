package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_Chogadiya
import com.pareshkumarsharma.gayatrievents.panchang.*
import com.pareshkumarsharma.gayatrievents.utilities.Database
import java.text.SimpleDateFormat
import java.util.*

class Muhurat : AppCompatActivity() {

    internal lateinit var nmDay: NumberPicker
    internal lateinit var nmYear: NumberPicker
    internal lateinit var nmMonth: NumberPicker

    internal lateinit var txtMonth: TextView
    internal lateinit var txtWeekDay: TextView
    internal lateinit var txtSunRise: TextView
    internal lateinit var txtSunSet: TextView

    internal lateinit var radioDayNight: RadioGroup
    internal lateinit var rdo_Day: RadioButton
    internal lateinit var rdo_Night: RadioButton

    internal lateinit var lstMuhurat: ListView

    internal lateinit var adapter: PSBSArrayAdapter_Chogadiya

    internal lateinit var c:Calendar

    private var weekDay = 1
    private var sunrise = "07:00"
    private var sunset = "06:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muhurat)

        nmDay = findViewById<NumberPicker>(R.id.nmDay)
        nmMonth = findViewById<NumberPicker>(R.id.nmMonth)
        nmYear = findViewById<NumberPicker>(R.id.nmYear)

        txtMonth = findViewById(R.id.month)
        txtWeekDay = findViewById(R.id.weekday)
        txtSunRise = findViewById(R.id.sunrise)
        txtSunSet = findViewById(R.id.sunset)

        radioDayNight = findViewById(R.id.rdo_group_chogadiya)
        rdo_Day = findViewById(R.id.rdo_divas)
        rdo_Night = findViewById(R.id.rdo_ratri)

        lstMuhurat = findViewById(R.id.lst_muhurat)

        nmDay.minValue = 1
        nmMonth.minValue = 1
        nmDay.maxValue = 31
        nmMonth.maxValue = 12
        nmYear.minValue = 1900 // we have from 1422
        nmYear.maxValue = 2100

        nmDay.value = SimpleDateFormat("d").format(Date()).toInt()
        nmMonth.value = SimpleDateFormat("M").format(Date()).toInt()
        nmYear.value = SimpleDateFormat("yyyy").format(Date()).toInt()

        adapter = PSBSArrayAdapter_Chogadiya(this,R.layout.listview_chogadiya_item,dayChogadiya(weekDay),dayChogadiyaTime(sunrise))
        lstMuhurat.adapter = adapter

        c = Calendar.getInstance()

        UpdatePanchang(c)

        nmDay.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, nmMonth.value - 1, i2)
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()
            UpdatePanchang(c)
        }

        nmMonth.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, i2 - 1, nmDay.value)

            if (i2 == 2)
                nmDay.maxValue = if (nmYear.value % 4 == 0) 29 else 28
            else if (i2 == 1 || i2 == 3 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 10 || i2 == 12)
                nmDay.maxValue = 31
            else
                nmDay.maxValue = 30

            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()

            UpdatePanchang(c)
        }

        nmYear.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(i2, nmMonth.value - 1, nmDay.value)
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()
            UpdatePanchang(c)
        }

        radioDayNight.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.rdo_divas) {
                if (rdo_Day.isChecked) {
                    c.set(nmYear.value, nmMonth.value - 1, nmDay.value)
                    UpdatePanchang(c)
                } else {
                    c.set(nmYear.value, nmMonth.value - 1, nmDay.value)
                    UpdatePanchang(c)
                }
            } else if (i == R.id.rdo_ratri) {
                if (rdo_Night.isChecked) {
                    c.set(nmYear.value, nmMonth.value - 1, nmDay.value)
                    UpdatePanchang(c)
                } else {
                    c.set(nmYear.value, nmMonth.value - 1, nmDay.value)
                    UpdatePanchang(c)
                }
            }
        }
    }

    fun UpdatePanchang(cl: Calendar) {
        val PanchangData = Database.getPanchangOf(
            SimpleDateFormat("dd-MM-yyyy").format(cl.time),
            nmYear.value
        )

        val monthInt =
            PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

        val monthStr =
            MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                PanchangData.Rows[0][3].toInt()
            ) + " पक्ष"

        sunrise = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
        sunset = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
        weekDay = PanchangData.Rows[0][1].toInt()

        txtMonth.text = "महिना : - " + monthStr + " | "
        txtWeekDay.text = "वार :- " + WeekDayHindi.get(weekDay)
        txtSunRise.text = "सुर्योदय : - " + sunrise + " | "
        txtSunSet.text = "सुर्योस्त : - " + sunset

        if(rdo_Day.isChecked)
            adapter.UpdateData(dayChogadiya(weekDay), dayChogadiyaTime(sunrise))
        else
            adapter.UpdateData(nightChogadiya(weekDay), nightChogadiyaTime(sunset))

        adapter.notifyDataSetChanged()
        lstMuhurat.deferNotifyDataSetChanged()
    }

    fun dayChogadiya(wkday: Int): List<String> {
        val chogadiya = mutableListOf<String>()
        //val onSunDay = 5 // Udveg
        var start: Int = (5 + ((wkday - 1) * 3))
        for (i in 1..8){
            if (start > 7)
                start %= 7
            if(start==0)
                start = 7
            chogadiya.add(Weekday_Chogadiya_Muhurat_Day.get(start))
            start+=1
        }
        return chogadiya.toList()
    }

    fun dayChogadiyaTime(time:String):List<Date> {
        val chogadiya = mutableListOf<Date>()
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,time.substring(0,2).toInt())
        c.set(Calendar.MINUTE,time.substring(3,5).toInt())
        chogadiya.add(c.time)
        for (i in 1..8){
            c.add(Calendar.HOUR_OF_DAY,1)
            c.add(Calendar.MINUTE,30)
            chogadiya.add(c.time)
        }
        return chogadiya.toList()
    }

    fun nightChogadiya(wkday: Int): List<String> {
        val chogadiya = mutableListOf<String>()
        //val onSunDay = 7 // shubh
        var start: Int = (7 + ((wkday - 1) * 2))
        for (i in 1..8){
            if (start > 7)
                start %= 7
            if(start==0)
                start = 7
            chogadiya.add(Weekday_Chogadiya_Muhurat_Night.get(start))
            start+=1
        }
        return chogadiya.toList()
    }

    fun nightChogadiyaTime(time:String):List<Date> {
        val chogadiya = mutableListOf<Date>()
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,time.substring(0,2).toInt()+12)
        c.set(Calendar.MINUTE,time.substring(3,5).toInt())
        chogadiya.add(c.time)
        for (i in 1..8){
            c.add(Calendar.HOUR_OF_DAY,1)
            c.add(Calendar.MINUTE,30)
            chogadiya.add(c.time)
        }
        return chogadiya.toList()
    }
}