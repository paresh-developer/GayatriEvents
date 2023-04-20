package com.pareshkumarsharma.gayatrievents.activities

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pareshkumarsharma.gayatrievents.R
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_Product
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_Service
import com.pareshkumarsharma.gayatrievents.adapters.PSBSArrayAdapter_User
import com.pareshkumarsharma.gayatrievents.api.model.UserRegisterModel
import com.pareshkumarsharma.gayatrievents.utilities.APICalls
import com.pareshkumarsharma.gayatrievents.utilities.DataTable
import com.pareshkumarsharma.gayatrievents.utilities.Database

class Patnership : AppCompatActivity() {

    lateinit var lst_Users:ListView
    lateinit var lst_Service:ListView
    lateinit var lst_Product:ListView

    internal lateinit var adapter_user:PSBSArrayAdapter_User
    internal lateinit var adapter_service: PSBSArrayAdapter_Service
    internal lateinit var adapter_product: PSBSArrayAdapter_Product

    internal lateinit var tbl_data: DataTable
    internal lateinit var tbl_data_Service: DataTable
    internal lateinit var tbl_data_Product: DataTable

    private var currentView = 1
    private var selectedUser = mutableListOf<String>()
    private var selectedServiceId = mutableListOf<String>()
    private var selectedProductId = mutableListOf<String>()

    lateinit var View_User:LinearLayout
    lateinit var View_Service:LinearLayout
    lateinit var View_Product:LinearLayout

    lateinit var Btn_Previous:Button
    lateinit var Btn_Next:Button
    lateinit var Btn_Submit:Button

    lateinit var Txt_Previous:TextView
    lateinit var Txt_Next:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patnership)

        lst_Users = findViewById(R.id.lst_User)
        lst_Service = findViewById(R.id.lst_Service)
        lst_Product = findViewById(R.id.lst_Product)

        tbl_data = Database.getUsers()
//        tbl_data.Rows.add(0, mutableListOf<String>("UserNames","Emails"))
        adapter_user = PSBSArrayAdapter_User(this,R.layout.listview_item_partnership_users,tbl_data.Rows)
        adapter_user.selections = selectedUser
        lst_Users.adapter = adapter_user

        currentView = 0

        View_User = findViewById(R.id.lyt_partner_user)
        View_Service = findViewById(R.id.lyt_partner_service)
        View_Product = findViewById(R.id.lyt_partner_product)

        View_User.visibility = View.VISIBLE
        View_Service.visibility = View.GONE
        View_Product.visibility = View.GONE

        Btn_Previous = findViewById(R.id.btnPrevious)
        Btn_Next = findViewById(R.id.btnNext)
        Btn_Submit = findViewById(R.id.btn_Register)

        Txt_Previous = findViewById(R.id.txt_goprevious_arrow)
        Txt_Next = findViewById(R.id.txt_gonext_arrow)

        Btn_Next.setOnClickListener {
            when(currentView){
                0 -> {
                    // at users selection
                    // go to service
                    selectedUser = adapter_user.selections
                    if(selectedUser.isEmpty())
                    {
                        Toast.makeText(this,"साझेदार चुना नहि गया है",Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    View_User.visibility = View.GONE
                    lst_Users.deferNotifyDataSetChanged()
                    View_Service.visibility = View.VISIBLE
                    View_Product.visibility = View.GONE
                    currentView = 1
                    Btn_Previous.isEnabled = true
                    Txt_Previous.visibility = View.VISIBLE

                    tbl_data_Service = Database.getService(selectedUser.joinToString(","))
                    adapter_service = PSBSArrayAdapter_Service(this,R.layout.listview_item_partnership_service,tbl_data_Service.Rows)
                    adapter_service.selections = selectedServiceId
                    lst_Service.adapter = adapter_service
                    lst_Service.deferNotifyDataSetChanged()
                }
                1 -> {
                    // at service selection
                    // go to product selection
                    selectedServiceId = adapter_service.selections
                    if(selectedServiceId.isEmpty()){
                        Toast.makeText(this,"साझेदारीत सेवायें चुनी नहि गयी है",Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    View_User.visibility = View.GONE
                    View_Service.visibility = View.GONE
                    lst_Product.deferNotifyDataSetChanged()
                    View_Product.visibility = View.VISIBLE
                    currentView = 2
                    Btn_Next.isEnabled = false
                    Txt_Next.visibility = View.INVISIBLE

                    tbl_data_Product = Database.getServiceProduct(selectedServiceId.joinToString(","))
                    adapter_product = PSBSArrayAdapter_Product(this,R.layout.listview_item_partnership_service,tbl_data_Product.Rows)
                    adapter_product.selections = selectedProductId
                    lst_Product.adapter = adapter_product
                    lst_Product.deferNotifyDataSetChanged()
                }
            }
        }

        Btn_Previous.setOnClickListener {
            when(currentView){
                1 -> {
                    // at service selection
                    // go to users selection
                    View_User.visibility = View.VISIBLE
                    View_Service.visibility = View.GONE
                    View_Product.visibility = View.GONE
                    currentView = 0
                    Btn_Previous.isEnabled = false
                    Txt_Previous.visibility = View.INVISIBLE

                    adapter_user.selections = selectedUser
                    adapter_user.notifyDataSetChanged()
                    lst_Users.deferNotifyDataSetChanged()
                }
                2 -> {
                    // at product selection
                    // go to service selection

                    View_User.visibility = View.GONE
                    View_Service.visibility = View.VISIBLE
                    View_Product.visibility = View.GONE
                    currentView = 1
                    Btn_Next.isEnabled = true
                    Txt_Next.visibility = View.VISIBLE

                    adapter_service.selections = selectedServiceId
                    adapter_service.notifyDataSetChanged()
                    lst_Service.deferNotifyDataSetChanged()
                }
            }
        }

        Btn_Submit.setOnClickListener {
            var errormsg = ""
            if(selectedUser.isEmpty()){
                errormsg += "साझेदार चुना नहि गया है\n"
            }
            if(selectedServiceId.isEmpty()){
                errormsg += "साझेदारीत सेवायें चुनी नहि गयी है\n"
            }
            if(selectedProductId.isEmpty()){
                errormsg += "साझेदारीत उपसेवायें चुनी नहि गयी है"
            }
            if(errormsg.trim().length>0)
                Toast.makeText(this,errormsg,Toast.LENGTH_LONG).show()
        }
    }

    fun RefreshUser(){
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
            if(APICalls.getUsersForPartnership()){
                val res = APICalls.lastCallObject as Array<UserRegisterModel>
                for (i in 0..res.size - 1) {
                    val values = ContentValues()
                    values.put("Uname",res[i].User_Name)
                    values.put("Email",res[i].User_Email)
                    values.put("Mobile",res[i].User_Mobile)
                    values.put("GlobalId",res[i].User_GlobalId)

                    if(Database.query("Select * From USERS where EMAIL = '${res[i].User_Email}' Or Mobile = '${res[i].User_Mobile}'").Rows.size==0) {
                        Database.insertTo("USERS", values, "Id,User_Password,User_Type")
                    }
                    else{
                        Database.updateTo(
                            "USERS",
                            values,
                            "GlobalId=?",
                            listOf(res[i].User_GlobalId).toTypedArray()
                        )
                    }

                    tbl_data = Database.getUsers()
//                    tbl_data.Rows.add(0, mutableListOf<String>("UserNames","Emails"))
                    adapter_user.UpdateData(tbl_data.Rows)
                    adapter_user.notifyDataSetChanged()
                    lst_Users.deferNotifyDataSetChanged()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        APICalls.lastCallMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }).start()
    }
}