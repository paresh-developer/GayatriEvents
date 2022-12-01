package com.pareshkumarsharma.gayatrievents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.Date

class Panchang : AppCompatActivity() {

    internal companion object {
        var countDot = 0
        var downloadComplete = false
    }

    lateinit var psbArrayAdadaper : PSBSArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panchang)

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            MainActivity.IsLoginDone = 5
            startActivity(Intent(this, LoginActivity::class.java))
        }

//        c.set(1422,1,1)
//        calendar.minDate = c.time.time
//        c.set(1823,1,1)
//        calendar.maxDate = c.time.time

        val nmDay = findViewById<NumberPicker>(R.id.nmDay)
        val nmMonth = findViewById<NumberPicker>(R.id.nmMonth)
        val nmYear = findViewById<NumberPicker>(R.id.nmYear)

        nmDay.minValue = 1
        nmMonth.minValue = 1
        nmDay.maxValue = 31
        nmMonth.maxValue = 12
        nmYear.minValue = 1422
        nmYear.maxValue = 2822

        nmDay.value = SimpleDateFormat("d").format(Date()).toInt()
        nmMonth.value = SimpleDateFormat("M").format(Date()).toInt()
        nmYear.value = SimpleDateFormat("yyyy").format(Date()).toInt()

        val calendar = findViewById<CalendarView>(R.id.calendarView)
        val c = Calendar.getInstance()

        nmDay.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, nmMonth.value - 1, i2)
            calendar.date = c.time.time
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmMonth.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, i2 - 1, nmDay.value)
            calendar.date = c.time.time
            if (i2 == 2)
                nmDay.maxValue = 29
            else
                nmDay.maxValue = 31
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmYear.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(i2, nmMonth.value - 1, nmDay.value)
            calendar.date = c.time.time
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        val listView = findViewById<ListView>(R.id.panchangListView)

        calendar.setDate(System.currentTimeMillis())
        calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->
            c.set(i,i2,i3)
            val PanchangData =Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(c.time),
                i)
            psbArrayAdadaper.UpdateData(PanchangData.Rows.toTypedArray(),PanchangData.Columns)
            psbArrayAdadaper.notifyDataSetChanged()
            listView.deferNotifyDataSetChanged()
        }

        val PanchangData =Database.getPanchangOf(
        SimpleDateFormat("dd-MM-yyyy").format(Date()).toString(),
        SimpleDateFormat("yyyy").format(Date()).toInt())

        psbArrayAdadaper = PSBSArrayAdapter(applicationContext,R.layout.listview_item
            ,PanchangData.Rows.toTypedArray(),PanchangData.Columns)

        listView.adapter = psbArrayAdadaper

        if (!getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("F002", false)) {
            listView.isEnabled = false
            nmDay.isEnabled = false
            nmMonth.isEnabled = false
            nmYear.isEnabled = false
            calendar.isEnabled = false
            Thread {
                APICalls.setContext(applicationContext)
                if (APICalls.downloadPanchang()) {
                    val data = APICalls.lastCallObject as ByteArray
                    val f = File("/data/data/com.pareshkumarsharma.gayatrievents/Panchang.db")
                    if (f.exists())
                        f.delete()
                    f.createNewFile()
                    f.writeBytes(data)
                    runOnUiThread {
                        downloadComplete = true
                        getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE)
                            .edit()
                            .putBoolean("F002", true)
                            .apply()

                        listView.isEnabled = true
                        nmDay.isEnabled = true
                        nmMonth.isEnabled = true
                        nmYear.isEnabled = true
                        calendar.isEnabled = true
                    }
                }
            }.start()
        } else
            downloadComplete = true

        if (!downloadComplete)
            Thread {
                var disStr = "Downloading"
                while (!downloadComplete) {
                    Thread.sleep(600)
                    if (countDot == 4) {
                        disStr = "Downloading"
                        countDot = 0
                    }
                    else
                        disStr = "Downloading" + ".".repeat(countDot)
                    runOnUiThread {
                        findViewById<TextView>(R.id.txtNavigation).text = disStr
                    }
                    countDot++
                }
                runOnUiThread {
                    findViewById<TextView>(R.id.txtNavigation).text = "Panchang"
                }
            }.start()
    }
}