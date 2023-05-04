package com.pareshkumarsharma.gayatrievents.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter
import com.pareshkumarsharma.gayatrievents.adapters.PSBSFestivalArrayAdapter
import com.pareshkumarsharma.gayatrievents.panchang.MonthHindi
import com.pareshkumarsharma.gayatrievents.panchang.PakshaHindi
import com.pareshkumarsharma.gayatrievents.panchang.WeekDayHindi
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class Panchang : AppCompatActivity() {

    internal companion object {
        var countDot = 0
        var downloadComplete = false
    }

    lateinit var psbArrayAdadaper: PSBSArrayAdapter
    lateinit var psbFestivalArrayAdadaper: PSBSFestivalArrayAdapter
    var monthStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panchang)

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
//            MainActivity.IsLoginDone = 5
//            getSharedPreferences()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
        }

        val nmDay = findViewById<NumberPicker>(R.id.nmDay)
        val nmMonth = findViewById<NumberPicker>(R.id.nmMonth)
        val nmYear = findViewById<NumberPicker>(R.id.nmYear)
        val listView = findViewById<ListView>(R.id.panchangListView)
        val listView_festival = findViewById<ListView>(R.id.monthFestivalList)
        val calendar = findViewById<CalendarView>(R.id.calendarView)

        if (!getSharedPreferences(Database.SHAREDFILE, MODE_PRIVATE).getBoolean("F002", false)) {
            listView.isEnabled = false
            listView_festival.isEnabled = false
            nmDay.isEnabled = false
            nmMonth.isEnabled = false
            nmYear.isEnabled = false
            calendar.visibility = View.GONE
            Thread {
                APICalls.setContext(applicationContext)
                APICalls.cookies = mapOf<String, String>(
                    Pair(
                        "token",
                        getSharedPreferences(
                            Database.SHAREDFILE,
                            MODE_PRIVATE
                        ).getString("token", "").toString()
                    ),
                    Pair(
                        "expires",
                        getSharedPreferences(
                            Database.SHAREDFILE,
                            MODE_PRIVATE
                        ).getString("expires", "").toString()
                    )
                )
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
                        calendar.visibility = View.VISIBLE

                        val PanchangData = Database.getPanchangOf(
                            SimpleDateFormat("dd-MM-yyyy").format(Date()).toString(),
                            SimpleDateFormat("yyyy").format(Date()).toInt()
                        )

                        val FestivalPanchangData = Database.getPanchangFestivalOf(
                            SimpleDateFormat("%-MM-yyyy").format(Date()).toString(),
                            SimpleDateFormat("yyyy").format(Date()).toInt()
                        )

                        val monthInt =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString()
                                .toInt()

                        monthStr =
                            MonthHindi.get(monthInt)  + ", " + PakshaHindi.get(
                                PanchangData.Rows[0][2].toInt()
                            ) + " पक्ष"+ ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
                        val tithi_nd_paksh = PanchangData.Rows[0][1]
                        val vikram_savat =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
                        val shak_savat =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
                        val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
                        val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
                        val chandroday =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
                        val chandrast =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
                        val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
                        val moonsign =
                            PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

                        findViewById<TextView>(R.id.txtNavigation).text = monthStr
                        findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
                        findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
                        findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
                        findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
                        findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
                        findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
                        findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
                        findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
                        findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign

                        psbArrayAdadaper = PSBSArrayAdapter(
                            applicationContext,
                            R.layout.listview_item,
                            PanchangData.Rows.toTypedArray(),
                            PanchangData.Columns
                        )

                        psbFestivalArrayAdadaper = PSBSFestivalArrayAdapter(
                            applicationContext,
                            R.layout.listview_item,
                            FestivalPanchangData.Rows.toTypedArray(),
                            FestivalPanchangData.Columns
                        )

                        listView.adapter = psbArrayAdadaper
                        listView_festival.adapter = psbFestivalArrayAdadaper
                    }
                } else {
                    downloadComplete = true
                    runOnUiThread {
                        Toast.makeText(this, APICalls.lastCallMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        } else {
            downloadComplete = true

            val PanchangData = Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(Date()).toString(),
                SimpleDateFormat("yyyy").format(Date()).toInt()
            )

            val FestivalPanchangData = Database.getPanchangFestivalOf(
                SimpleDateFormat("%-MM-yyyy").format(Date()).toString(),
                SimpleDateFormat("yyyy").format(Date()).toInt()
            )

            val monthInt =
                PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

            monthStr =
                MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                    PanchangData.Rows[0][2].toInt()
                ) + " पक्ष"+ ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
            val tithi_nd_paksh = PanchangData.Rows[0][1]
            val vikram_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
            val shak_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
            val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
            val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
            val chandroday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
            val chandrast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
            val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
            val moonsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

            findViewById<TextView>(R.id.txtNavigation).text = monthStr
            findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
            findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
            findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
            findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
            findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
            findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
            findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
            findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
            findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign

            psbArrayAdadaper = PSBSArrayAdapter(
                applicationContext,
                R.layout.listview_item,
                PanchangData.Rows.toTypedArray(),
                PanchangData.Columns
            )

            psbFestivalArrayAdadaper = PSBSFestivalArrayAdapter(
                applicationContext,
                R.layout.listview_item,
                FestivalPanchangData.Rows.toTypedArray(),
                FestivalPanchangData.Columns
            )

            listView.adapter = psbArrayAdadaper
            listView_festival.adapter = psbFestivalArrayAdadaper
        }

//        c.set(1422,1,1)
//        calendar.minDate = c.time.time
//        c.set(1823,1,1)
//        calendar.maxDate = c.time.time

        nmDay.minValue = 1
        nmMonth.minValue = 1
        nmDay.maxValue = 31
        nmMonth.maxValue = 12
        nmYear.minValue = 1900 // we have from 1422
        nmYear.maxValue = 2100

        nmDay.value = SimpleDateFormat("d").format(Date()).toInt()
        nmMonth.value = SimpleDateFormat("M").format(Date()).toInt()
        nmYear.value = SimpleDateFormat("yyyy").format(Date()).toInt()

        val c = Calendar.getInstance()

        nmDay.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, nmMonth.value - 1, i2)
            calendar.date = c.time.time
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()

            val PanchangData = Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(c.time),
                nmYear.value
            )

            val monthInt =
                PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

            monthStr =
                MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                    PanchangData.Rows[0][2].toInt()
                ) + " पक्ष" + ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
            val tithi_nd_paksh = PanchangData.Rows[0][1]
            val vikram_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
            val shak_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
            val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
            val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
            val chandroday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
            val chandrast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
            val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
            val moonsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

            findViewById<TextView>(R.id.txtNavigation).text = monthStr
            findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
            findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
            findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
            findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
            findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
            findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
            findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
            findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
            findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign

            psbArrayAdadaper.UpdateData(PanchangData.Rows.toTypedArray(), PanchangData.Columns)
            psbArrayAdadaper.notifyDataSetChanged()
            listView.deferNotifyDataSetChanged()
        }

        nmMonth.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(nmYear.value, i2 - 1, nmDay.value)
            calendar.date = c.time.time
            if (i2 == 2)
                nmDay.maxValue = if (nmYear.value % 4 == 0) 29 else 28
            else if (i2 == 1 || i2 == 3 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 10 || i2 == 12)
                nmDay.maxValue = 31
            else
                nmDay.maxValue = 30
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()

            val PanchangData = Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(c.time),
                nmYear.value
            )

            val monthInt =
                PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

            monthStr =
                MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                    PanchangData.Rows[0][2].toInt()
                ) + " पक्ष"+ ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
            val tithi_nd_paksh = PanchangData.Rows[0][1]
            val vikram_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
            val shak_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
            val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
            val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
            val chandroday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
            val chandrast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
            val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
            val moonsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

            findViewById<TextView>(R.id.txtNavigation).text = monthStr
            findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
            findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
            findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
            findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
            findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
            findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
            findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
            findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
            findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign


            val FestivalPanchangData = Database.getPanchangFestivalOf(
                SimpleDateFormat("%-MM-yyyy").format(c.time).toString(),
                SimpleDateFormat("yyyy").format(c.time).toInt()
            )

            psbArrayAdadaper.UpdateData(PanchangData.Rows.toTypedArray(), PanchangData.Columns)
            psbArrayAdadaper.notifyDataSetChanged()
            listView.deferNotifyDataSetChanged()

            psbFestivalArrayAdadaper.UpdateData(
                FestivalPanchangData.Rows.toTypedArray(),
                FestivalPanchangData.Columns
            )
            psbFestivalArrayAdadaper.notifyDataSetChanged()
            listView_festival.deferNotifyDataSetChanged()
        }

        nmYear.setOnValueChangedListener { numberPicker, i, i2 ->
            c.set(i2, nmMonth.value - 1, nmDay.value)
            calendar.date = c.time.time
            nmDay.value = SimpleDateFormat("d").format(c.time).toInt()

            val PanchangData = Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(c.time),
                i2
            )

            val FestivalPanchangData = Database.getPanchangFestivalOf(
                SimpleDateFormat("%-MM-yyyy").format(c.time).toString(),
                i2
            )

            val monthInt =
                PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

            monthStr =
                MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                    PanchangData.Rows[0][2].toInt()
                ) + " पक्ष" + ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
            val tithi_nd_paksh = PanchangData.Rows[0][1]
            val vikram_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
            val shak_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
            val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
            val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
            val chandroday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
            val chandrast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
            val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
            val moonsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

            findViewById<TextView>(R.id.txtNavigation).text = monthStr
            findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
            findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
            findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
            findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
            findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
            findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
            findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
            findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
            findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign

            psbArrayAdadaper.UpdateData(PanchangData.Rows.toTypedArray(), PanchangData.Columns)
            psbArrayAdadaper.notifyDataSetChanged()
            listView.deferNotifyDataSetChanged()

            psbFestivalArrayAdadaper.UpdateData(
                FestivalPanchangData.Rows.toTypedArray(),
                FestivalPanchangData.Columns
            )
            psbFestivalArrayAdadaper.notifyDataSetChanged()
            listView_festival.deferNotifyDataSetChanged()
        }

        calendar.date = System.currentTimeMillis()
        calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->
            c.set(i, i2, i3)
            nmYear.value = i
            nmMonth.value = i2 + 1
            nmDay.value = i3
            val PanchangData = Database.getPanchangOf(
                SimpleDateFormat("dd-MM-yyyy").format(c.time),
                i
            )

            val FestivalPanchangData = Database.getPanchangFestivalOf(
                SimpleDateFormat("%-MM-yyyy").format(c.time).toString(),
                SimpleDateFormat("yyyy").format(c.time).toInt()
            )

            val monthInt =
                PanchangData.Rows[0][PanchangData.Columns.indexOf("AmantMonth")].toString().toInt()

            monthStr =
                MonthHindi.get(monthInt) + ", " + PakshaHindi.get(
                    PanchangData.Rows[0][2].toInt()
                ) + " पक्ष" + ", " + WeekDayHindi.get(PanchangData.Rows[0][0].toInt())
            val tithi_nd_paksh = PanchangData.Rows[0][1]
            val vikram_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("VikramSamvat")]
            val shak_savat = PanchangData.Rows[0][PanchangData.Columns.indexOf("ShakSamvat")]
            val suryoday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunrise")]
            val suryast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunset")]
            val chandroday = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonrise")]
            val chandrast = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonset")]
            val sunsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Sunsign")]
            val moonsign = PanchangData.Rows[0][PanchangData.Columns.indexOf("Moonsign")]

            findViewById<TextView>(R.id.txtNavigation).text = monthStr
            findViewById<TextView>(R.id.txt_thi_nd_paksha).text = tithi_nd_paksh
            findViewById<TextView>(R.id.txt_panchang_v_s).text = " : " + vikram_savat
            findViewById<TextView>(R.id.txt_panchang_s_s).text = " : " + shak_savat
            findViewById<TextView>(R.id.txt_suryodhay).text = " : " + suryoday
            findViewById<TextView>(R.id.txt_suryast).text = " : " + suryast
            findViewById<TextView>(R.id.txt_chandrodhay).text = " : " + chandroday
            findViewById<TextView>(R.id.txt_chandrast).text = " : " + chandrast
            findViewById<TextView>(R.id.txt_surya_rashi).text = " : " + sunsign
            findViewById<TextView>(R.id.txt_chandra_rashi).text = " : " + moonsign

            psbArrayAdadaper.UpdateData(PanchangData.Rows.toTypedArray(), PanchangData.Columns)
            psbArrayAdadaper.notifyDataSetChanged()
            listView.deferNotifyDataSetChanged()

            psbFestivalArrayAdadaper.UpdateData(
                FestivalPanchangData.Rows.toTypedArray(),
                FestivalPanchangData.Columns
            )
            psbFestivalArrayAdadaper.notifyDataSetChanged()
            listView_festival.deferNotifyDataSetChanged()
        }

        if (!downloadComplete)
            Thread {
                var disStr = "Downloading"
                while (!downloadComplete) {
                    Thread.sleep(600)
                    if (countDot == 4) {
                        disStr = "पंचांग प्राप्त किया जा रहा है..."
                        countDot = 0
                    } else
                        disStr = "पंचांग प्राप्त किया जा रहा है..." + ".".repeat(countDot)
                    runOnUiThread {
                        findViewById<TextView>(R.id.txtNavigation).text = disStr
                    }
                    countDot++
                }
                runOnUiThread {
                    findViewById<TextView>(R.id.txtNavigation).text = monthStr
                }
            }.start()
    }
}