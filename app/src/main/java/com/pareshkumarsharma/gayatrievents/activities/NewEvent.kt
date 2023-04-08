package com.pareshkumarsharma.gayatrievents.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.api.model.EventRegistrationModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.Database
import java.text.SimpleDateFormat
import java.util.*

internal class NewEvent : AppCompatActivity() {
    lateinit var Edt_SelectedService: EditText
    val thisActivity: NewEvent = this
    var selectedPaymentMethod = 0
    private var Payment_Id = ""

    internal companion object {
        var Selected_Service_Global_Id = ""
        var Selected_Service_Product_Global_Id = ""
        var Operation = 'I'
        var Event_Id = ""
        var Event_Global_Id = ""
        var Event_Name = ""
        var Event_Date_Fixed = false
        var Event_Date_Start = ""
        var Event_Date_End = ""
        var Event_Price = 0.0
        var Service_Id = 0
        var Service_GlobalId = ""
        var ServiceProduct_Id = 0
        var ServiceProduct_GlobalId = ""
        var Event_Details = ""
        var SelectedServiceIds = mutableListOf<String>()
        var SelectedServiceProductIds = mutableListOf<String>()
        var SelectedServiceProductPriceList = mutableListOf<Float>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        val rdo_Date_Fixed = findViewById<RadioButton>(R.id.chkEventDateFixed)
        val rdo_Date_Not_Fixed = findViewById<RadioButton>(R.id.chkEventDateNotFixed)

        SelectedServiceIds = mutableListOf<String>()
        SelectedServiceProductIds = mutableListOf<String>()
        SelectedServiceProductPriceList = mutableListOf<Float>()

        ServiceForEvent.SelectedServiceIds = mutableListOf<String>()
        ServiceProductForEvent.SelectedProductId = mutableListOf<String>()
        ServiceProductForEvent.SelectedProductPrice = mutableListOf<Float>()

        val rd_group = findViewById<RadioGroup>(R.id.radioGroup)
        rd_group.setOnCheckedChangeListener { radioGroup, i ->
            if (rdo_Date_Fixed.isChecked) {
                findViewById<TextView>(R.id.txtEventStartDateLabel).text =
                    "तारीख पसंद करे: "
                findViewById<TextView>(R.id.txtEventEndDateLabel).visibility = View.GONE
                findViewById<LinearLayout>(R.id.lytDateEnd).visibility = View.GONE
            } else if (rdo_Date_Not_Fixed.isChecked) {
                findViewById<TextView>(R.id.txtEventStartDateLabel).text =
                    "तारीख की प्रारम्भिक सीमा: "
                findViewById<TextView>(R.id.txtEventEndDateLabel).visibility =
                    View.VISIBLE
                findViewById<LinearLayout>(R.id.lytDateEnd).visibility = View.VISIBLE
            }
        }

        val nmDay_S = findViewById<NumberPicker>(R.id.nmDay_Start)
        val nmMonth_S = findViewById<NumberPicker>(R.id.nmMonth_Start)
        val nmYear_S = findViewById<NumberPicker>(R.id.nmYear_Start)

        nmDay_S.minValue = 1
        nmMonth_S.minValue = 1
        nmDay_S.maxValue = 31
        nmMonth_S.maxValue = 12
        nmYear_S.minValue = SimpleDateFormat("yyyy").format(Date()).toInt()
        nmYear_S.maxValue = 2100

        nmDay_S.value = SimpleDateFormat("d").format(Date()).toInt()
        nmMonth_S.value = SimpleDateFormat("M").format(Date()).toInt()
        nmYear_S.value = SimpleDateFormat("yyyy").format(Date()).toInt()

        nmDay_S.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(nmYear_S.value, nmMonth_S.value - 1, i2)
            nmDay_S.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmMonth_S.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(nmYear_S.value, i2 - 1, nmDay_S.value)
            if (i2 == 2)
                nmDay_S.maxValue = if (nmYear_S.value % 4 == 0) 29 else 28
            else if (i2 == 1 || i2 == 3 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 10 || i2 == 12)
                nmDay_S.maxValue = 31
            else
                nmDay_S.maxValue = 30
            nmDay_S.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmYear_S.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(i2, nmMonth_S.value - 1, nmDay_S.value)
            nmDay_S.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        val nmDay_E = findViewById<NumberPicker>(R.id.nmDay_End)
        val nmMonth_E = findViewById<NumberPicker>(R.id.nmMonth_End)
        val nmYear_E = findViewById<NumberPicker>(R.id.nmYear_End)

        nmDay_E.minValue = 1
        nmMonth_E.minValue = 1
        nmDay_E.maxValue = 31
        nmMonth_E.maxValue = 12
        nmYear_E.minValue = SimpleDateFormat("yyyy").format(Date()).toInt()
        nmYear_E.maxValue = 2100

        nmDay_E.value = SimpleDateFormat("d").format(Date()).toInt()
        nmMonth_E.value = SimpleDateFormat("M").format(Date()).toInt()
        nmYear_E.value = SimpleDateFormat("yyyy").format(Date()).toInt()

        nmDay_E.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(nmYear_E.value, nmMonth_E.value - 1, i2)
            nmDay_E.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmMonth_E.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(nmYear_E.value, i2 - 1, nmDay_E.value)
            if (i2 == 2)
                nmDay_E.maxValue = 29
            else if (i2 == 1 || i2 == 3 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 10 || i2 == 12)
                nmDay_E.maxValue = 31
            else
                nmDay_E.maxValue = 30
            nmDay_E.value = SimpleDateFormat("d").format(c.time).toInt()
        }

        nmYear_E.setOnValueChangedListener { numberPicker, i, i2 ->
            val c = Calendar.getInstance()
            c.set(i2, nmMonth_E.value - 1, nmDay_E.value)
            nmDay_E.value = SimpleDateFormat("d").format(c.time).toInt()
        }

//        Edt_SelectedService = findViewById<EditText>(R.id.selectedService)
//        Edt_SelectedService.setOnClickListener {
//            startActivity(Intent(this,ServiceForEvent::class.java))
//        }
        findViewById<Button>(R.id.btn_popup_select_service).setOnClickListener {
            ServiceForEvent.SelectedServiceIds = SelectedServiceIds
            startActivity(Intent(this, ServiceForEvent::class.java))
        }
//        findViewById<EditText>(R.id.selectedServiceProduct).setOnClickListener {
//            ServiceProductForEvent.SelectedServiceId = ServiceForEvent.SelectedServiceIds
//            ServiceProductForEvent.SelectedProductId = SelectedServiceProductIds
//            startActivity(Intent(this,ServiceProductForEvent::class.java))
//        }
        findViewById<Button>(R.id.btn_popup_select_service_product).setOnClickListener {
            if (SelectedServiceIds != null && SelectedServiceIds.size > 0) {
                ServiceProductForEvent.SelectedServiceId = SelectedServiceIds
                ServiceProductForEvent.SelectedProductId = SelectedServiceProductIds
                startActivity(Intent(this, ServiceProductForEvent::class.java))
            } else {
                Toast.makeText(
                    this,
                    "उपसेवा के लीये प्रथम सेवा पसंद करना अनीवार्य है।",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (Operation == 'U') {
            findViewById<EditText>(R.id.eventName).setText(Event_Name)
            findViewById<EditText>(R.id.eventDesc).setText(Event_Details)
            if (Event_Date_Fixed)
                findViewById<RadioButton>(R.id.chkEventDateFixed).isChecked = true
            else
                findViewById<RadioButton>(R.id.chkEventDateNotFixed).isChecked = true
            findViewById<NumberPicker>(R.id.nmDay_Start).value =
                Event_Date_Start.substring(8, 10).toInt()
            findViewById<NumberPicker>(R.id.nmMonth_Start).value =
                Event_Date_Start.substring(5, 7).toInt()
            findViewById<NumberPicker>(R.id.nmYear_Start).value =
                Event_Date_Start.substring(0, 4).toInt()
            findViewById<NumberPicker>(R.id.nmDay_End).value =
                Event_Date_End.substring(8, 10).toInt()
            findViewById<NumberPicker>(R.id.nmMonth_End).value =
                Event_Date_End.substring(5, 7).toInt()
            findViewById<NumberPicker>(R.id.nmYear_End).value =
                Event_Date_End.substring(0, 4).toInt()
            findViewById<TextView>(R.id.txt_price).text = "" + Event_Price
            findViewById<Button>(R.id.btnSaveNewEvent).text = "Update"
            Selected_Service_Global_Id = Service_GlobalId
            Selected_Service_Product_Global_Id = ServiceProduct_GlobalId
            val tbl = Database.query("Select Title from Service where Id = $Service_Id")
            val tbl1 =
                Database.query("Select Title,Price from SERVICE_PRODUCT where Id = $ServiceProduct_Id")
            if (tbl.Rows.size > 0 && !tbl.Columns.contains("Error"))
                findViewById<EditText>(R.id.selectedService).setText(tbl.Rows[0][0])
            if (tbl1.Rows.size > 0 && !tbl1.Columns.contains("Error")) {
                findViewById<EditText>(R.id.selectedServiceProduct).setText(tbl1.Rows[0][0])
                findViewById<TextView>(R.id.txt_price).text = tbl1.Rows[0][1]
            }
        }

        findViewById<Button>(R.id.btnSaveNewEvent).setOnClickListener {
            findViewById<Button>(R.id.btnSaveNewEvent).isEnabled = false
            val event_name = findViewById<EditText>(R.id.eventName).text.toString()
            val event_desc = findViewById<EditText>(R.id.eventDesc).text.toString()
            val event_date_fixed =
                findViewById<RadioButton>(R.id.chkEventDateFixed).isChecked
            val event_date_start_day =
                findViewById<NumberPicker>(R.id.nmDay_Start).value
            val event_date_start_month =
                findViewById<NumberPicker>(R.id.nmMonth_Start).value
            val event_date_start_year =
                findViewById<NumberPicker>(R.id.nmYear_Start).value
            val event_date_end_day = findViewById<NumberPicker>(R.id.nmDay_End).value
            val event_date_end_month =
                findViewById<NumberPicker>(R.id.nmMonth_End).value
            val event_date_end_year = findViewById<NumberPicker>(R.id.nmYear_End).value
            val event_date_start =
                "$event_date_start_year-$event_date_start_month-$event_date_start_day"
            val event_date_end =
                "$event_date_end_year-$event_date_end_month-$event_date_end_day"
            Thread(Runnable {
                APICalls.setContext(this)
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

                // region Insert On Web
                if (Operation == 'I') {
                    if (APICalls.requestNewEventRegistration(
                            EventRegistrationModel(
                                event_name,
                                event_date_fixed,
                                event_date_start,
                                event_date_end,
                                SelectedServiceProductPriceList.joinToString(),
                                SelectedServiceIds.joinToString(),
                                SelectedServiceProductIds.joinToString(),
                                event_desc
                            )
                        )
                    ) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            findViewById<Button>(R.id.btnSaveNewEvent).isEnabled = true

                            val EventId =
                                APICalls.lastCallMessage.split("Id:")[1].trim()
                                    .trim('"')
                            var EventAmount = 0.0F
                            for (am in SelectedServiceProductPriceList) {
                                EventAmount += am.toFloat()
                            }
                            NewPayment.RefCode = 'E'
                            NewPayment.RefName = event_name
                            NewPayment.RefAmount = EventAmount
                            NewPayment.RefId = EventId
                            startActivity(Intent(this, NewPayment::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                APICalls.lastCallMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            findViewById<Button>(R.id.btnSaveNewEvent).isEnabled = true
                        }
                    }
                }
                // endregion

                // region Update On Web
                else if (Operation == 'U') {
//                    if (APICalls.requestNewServiceUpdation(
//                            ServiceUpdationRequestModel(
//                                NewService.SGLB,ServiceTitle, ServiceDesc, ServiceType, ServiceAdd, ServiceCity
//                            )
//                        )
//                    ) {
//                        runOnUiThread {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                            finish()
//                        }
//                    } else {
//                        runOnUiThread {
//                            Toast.makeText(
//                                applicationContext,
//                                APICalls.lastCallMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                            findViewById<Button>(R.id.btnNewServiceRequestSubmit).isEnabled = true
//                        }
//                    }
                }
                // endregion
            }).start()
        }
//}
    }

    override fun onResume() {
        super.onResume()
        SelectedServiceIds = ServiceForEvent.SelectedServiceIds
        SelectedServiceProductIds = ServiceProductForEvent.SelectedProductId
        SelectedServiceProductPriceList = ServiceProductForEvent.SelectedProductPrice
        if (ServiceForEvent.SelectedServiceIds.count() > 0)
            findViewById<EditText>(R.id.selectedService).setText(
                "> " + ServiceForEvent.SelectedServiceNames.joinToString(
                    "\n> "
                )
            )
        else
            findViewById<EditText>(R.id.selectedService).setText("")
        if (ServiceProductForEvent.SelectedProductId.size > 0)
            findViewById<EditText>(R.id.selectedServiceProduct).setText(
                "> " + ServiceProductForEvent.SelectedProductName.joinToString(
                    "\n> "
                )
            )
        else
            findViewById<EditText>(R.id.selectedServiceProduct).setText("")
        if (ServiceProductForEvent.SelectedProductPrice.count() > 0) {
            var sumPrice = 0.0
            for (p in ServiceProductForEvent.SelectedProductPrice) {
                sumPrice += p.toDouble()
            }
            findViewById<TextView>(R.id.txt_price).text =
                ServiceProductForEvent.SelectedProductPrice.joinToString("\n") + "\n---------\nTotal: " + Math.ceil(
                    sumPrice
                ).toInt()
        } else
            findViewById<TextView>(R.id.txt_price).text = "0.0"
    }

    override fun onDestroy() {
        Operation = 'I'
        super.onDestroy()
    }
}