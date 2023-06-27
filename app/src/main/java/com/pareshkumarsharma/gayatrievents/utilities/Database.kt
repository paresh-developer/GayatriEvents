package com.pareshkumarsharma.gayatrievents.utilities

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.pareshkumarsharma.gayatrievents.activities.MainActivity

internal class Database {
    internal companion object {
        internal val DBPATH = "/data/data/com.pareshkumarsharma.gayatrievents/main.db"
        internal val DBPATH_PANCHANG = "/data/data/com.pareshkumarsharma.gayatrievents/Panchang.db"
        private lateinit var sqlite: SQLiteDatabase
        private lateinit var cur: Cursor
        internal var lastError = ""
        internal lateinit var activity: MainActivity

        internal val SHAREDFILE = "shared_pref"

        internal fun openConnection(default: Int = 0) {
            if (default == 0)
                sqlite = SQLiteDatabase.openOrCreateDatabase(DBPATH, null)
            else
                sqlite = SQLiteDatabase.openOrCreateDatabase(DBPATH_PANCHANG, null)
        }

        internal fun closeConnection() {
            sqlite.close()
        }

        internal fun query(query: String): DataTable {
            var errorstr = ""
            val columns = ArrayList<String>()
            val data = ArrayList<String>()
            val row = mutableListOf<MutableList<String>>()
            try {
                openConnection()
                cur = sqlite.rawQuery(query, null)
                for (col in cur.columnNames)
                    columns.add(col)
                while (cur.moveToNext()) {
                    for (i in 0..columns.size - 1)
                        data.add(cur.getString(i))
                    row.add(data)
                }
            } catch (Ex: java.lang.Exception) {
                lastError = Ex.message.toString()
                errorstr = Ex.message.toString()
            } finally {
                cur.close()
                closeConnection()
            }
            return DataTable(columns, row, errorstr)
        }

        private fun checkTableExists(tableName: String = ""): Boolean {
            var flag = false
            try {
                if (tableName.trim().length == 0)
                    if (query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and not name = 'sqlite_sequence' and not name = 'android_metadata';").Rows[0][0].toString()
                            .toInt() > 0
                    )
                        flag = true
                    else
                        if (query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and name = '$tableName';").Rows[0][0].toString()
                                .toInt() > 0
                        )
                            flag = true
            } catch (ex: java.lang.Exception) {
                lastError = ex.message.toString()
            }
            return flag
        }

        internal fun checkDatabaseSetup() {
            try {
                Toast.makeText(
                    activity.applicationContext,
                    "Working On SETUP Please wait...",
                    Toast.LENGTH_SHORT
                ).show()
                openConnection()
                // some table exists
                if (!checkTableExists("USERS")) {
                    //User table not exists
                    sqlite.execSQL(
                        "Create table USERS (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "Uname text," +
                                "Email text," +
                                "Mobile text," +
                                "User_Password text," +
                                "User_Type int" +
                                ");"
                    )
                }

                if (!checkTableExists("USER_TYPE")) {
                    //User type table not exists
                    sqlite.execSQL(
                        "Create table USER_TYPE (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Type_Name text" +
                                ");"
                    )
                    sqlite.execSQL("Insert into USER_TYPE (Type_Name) Values ('Client'),('Service Provider')")
//                    var c = ContentValues()
//                    c.put("Type_Name","Client")
//                    sqlite.insert("USER_TYPE","Id",c)
//                    c.clear()
//                    c = ContentValues()
//                    c.put("Type_Name","Brahman")
//                    sqlite.insert("USER_TYPE","Id",c)
//                    c.clear()
//                    c = ContentValues()
//                    c.put("Type_Name","Service Provider")
//                    sqlite.insert("USER_TYPE","Id",c)
                }

                if (!checkTableExists("SERVICE_TYPE")) {
                    //User type table not exists
                    sqlite.execSQL(
                        "Create table SERVICE_TYPE (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "Service_Type_Name text" +
                                ");")
                    sqlite.execSQL("Insert into Service_TYPE (Service_Type_Name) Values ('Brahman'),('Hall_Booking'),('Kariyana'),('Catering'),('Delivery'),('Photo_VIdeo_Studio'),('Decoration');")
                }

                if (!checkTableExists("SERVICE")) {
                    //User type table not exists
                    sqlite.execSQL(
                        "Create table SERVICE (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "Owner text," +
                                "Title text," +
                                "SmallDesc text," +
                                "ServiceType int," +
                                "SAddress text," +
                                "City int," +
                                "Approved int," +
                                "ApprovalTime datetime," +
                                "RequestStatus int," +
                                "UserId int,"+
                                "UserGlobalId text"+
                                ");")
                }

                if (!checkTableExists("SERVICE_PRODUCT")) {
                    //User type table not exists
                    sqlite.execSQL(
                        "Create table SERVICE_PRODUCT (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceGlobalId int," +
                                "ServiceId int," +
                                "Title text," +
                                "SmallDesc text," +
                                "Price Numeric," +
                                "CreationDate datetime" +
                                ");")
                }

                if (!checkTableExists("SERVICE_PRODUCT_DETAIL")) {
                    //User type table not exists
                    sqlite.execSQL(
                        "Create table SERVICE_PRODUCT_DETAIL (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceProductGlobalId int," +
                                "ServiceProductId int," +
                                "Title text," +
                                "SmallDesc text," +
                                "Type Integer," +
                                "CreationDate datetime" +
                                ");")
                }

                if (!checkTableExists("EVENTS")) {
                    sqlite.execSQL(
                        "Create table EVENTS (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceProductGlobalIdList text," +
                                "ServiceProductIdList text," +
                                "ServiceGlobalIdList text," +
                                "ServiceIdList text," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text," +
                                "RequestStatus int," +
                                "PaymentStatus int," +
                                "UserTurn text" +
                                ");")
                }

                if (!checkTableExists("Client_EVENTS_Request")) {
                    sqlite.execSQL(
                        "Create table Client_EVENTS_Request (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceProductGlobalIdList text," +
                                "ServiceProductIdList text," +
                                "ServiceGlobalIdList text," +
                                "ServiceIdList text," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text," +
                                "RequestStatus int," +
                                "PaymentStatus int," +
                                "UserTurn text," +
                                "OrderReady int," +
                                "ClientMobile text" +
                                ");")
                }

                if (!checkTableExists("EVENTS_Update")) {
                    sqlite.execSQL(
                        "Create table EVENTS_Update (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "EventGlobalId text," +
                                "EventId int," +
                                "GlobalId text," +
                                "ServiceProductGlobalIdList text," +
                                "ServiceProductIdList text," +
                                "ServiceGlobalIdList text," +
                                "ServiceIdList text," +
                                "Title text," +
                                "Details text," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
                                ");")
                }

                if (!checkTableExists("EVENTS_Backup")) {
                    sqlite.execSQL(
                        "Create table EVENTS_Backup (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "EventGlobalId text," +
                                "EventId int," +
                                "EventUpdateGlobalId text," +
                                "EventUpdateId int," +
                                "GlobalId text," +
                                "ServiceProductGlobalIdList text," +
                                "ServiceProductIdList text," +
                                "ServiceGlobalIdList text," +
                                "ServiceIdList text," +
                                "Title text," +
                                "Details text," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
                                ");")
                }

                if (!checkTableExists("DONATION")) {
                    //table not exists
                    sqlite.execSQL(
                        "Create table DONATION (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "Motive text," +
                                "Description text," +
                                "Amount float," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreatedOn datetime," +
                                "PaymentStatus int" +
                                ");"
                    )
                }

                if (!checkTableExists("Partnership")) {
                    //table not exists
                    sqlite.execSQL(
                        "Create table Partnership (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "UserId Id," +
                                "UserGlobalId text," +
                                "PartnerUserIdList text," +
                                "ApprovedOn datetime," +
                                "RequestStatus int," +
                                "Approval int," +
                                "PartnershipShare float," +
                                "PartnershipType int," +
                                "CreatedOn datetime" +
                                ");"
                    )
                }

                if (!checkTableExists("Partnership_Product")) {
                    //table not exists
                    sqlite.execSQL(
                        "Create table Partnership_Product (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "Partnership_Request_Id int," +
                                "Partnership_Request_GlobalId text," +
                                "ServiceId int," +
                                "ServiceGlobalId text," +
                                "ProductId int," +
                                "ProductGlobalId text," +
                                "Turn int," +
                                "CreatedOn datetime" +
                                ");"
                    )
                }

                Toast.makeText(activity.applicationContext, "SETUP Completed", Toast.LENGTH_LONG)
                    .show()
            } catch (ex: java.lang.Exception) {
                lastError = ex.message.toString()
                Toast.makeText(
                    activity.applicationContext,
                    "Error : " + ex.message,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                closeConnection()
            }
        }

        internal fun insertTo(tableName: String, values: ContentValues, colmnHack: String) {
            try {
                openConnection()
                sqlite.insert(tableName, colmnHack, values)
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
        }

        internal fun updateTo(
            tableName: String,
            values: ContentValues,
            whereStr: String,
            whereArg: Array<String>
        ) {
            try {
                openConnection()
                sqlite.update(tableName, values, whereStr, whereArg)
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
        }

        internal fun getPanchangOf(dt: String, yr: Int): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
                var c = sqlite.rawQuery(
                    "Select Weekday,Tithi,Paksha,AmantMonth,Festivals,Sunrise,SunSet,Nakshatra,Moonsign,Sunsign,Yoga,Karan,Moonrise,Moonset,VikramSamvat,ShakSamvat " +
                            "from DKP$yr where EventDate = \"${dt}\"", null
                )
                tbl = getDataTableFromCursor(c)

                // check for adik mas
                if (tbl.Rows.size > 0 && tbl.Rows[0][tbl.Columns.indexOf("AmantMonth")].toString()
                        .toInt() == 0
                ) {
                    c = sqlite.rawQuery("select distinct AmantMonth from dkp$yr", null)
                    val tmpTbl = getDataTableFromCursor(c)
                    c.close()
                    var adikmas = -1
                    for (rCnt in 0..tmpTbl.Rows.size - 1) {
                        if (tmpTbl.Rows[rCnt][0].toString().toInt() == 0) {
                            adikmas = rCnt; break
                        }
                    }
                    if (adikmas == -1 || adikmas == tmpTbl.Rows.size - 1) {
                        adikmas = tmpTbl.Rows.first()[0].toString().toInt()
                    } else {
                        adikmas = tmpTbl.Rows[adikmas + 1][0].toString().toInt()
                    }
                    tbl.Rows[0][tbl.Columns.indexOf("AmantMonth")] = (adikmas + 12).toString()
                }

                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        internal fun getPanchangFestivalOf(dt: String, yr: Int): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
                val c = sqlite.rawQuery(
                    "Select EventDate,Festivals,Tithi,Weekday " +
                            "from DKP$yr where EventDate like \"${dt}\" and length(trim(Festivals))>0",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        internal fun getServiceTypes(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select  * from Service_type", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        fun getUserGlobalId(username:String,mobile:String,password:String):String{
            var id=""
            try {
                openConnection()
                val c = sqlite.rawQuery("select GlobalId from users where email = '$username' and mobile = '$mobile' and User_Password = '$password'", null)
                val tbl = getDataTableFromCursor(c)
                c.close()
                if(tbl!=null && tbl.Rows.count()>0)
                    id = tbl.Rows[0][0]
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return id
        }

        internal fun getCities(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
//                val c = sqlite.query("Cities","Select id,name,state_code,country_code from Cities where country_code='IN' and state_code='GJ' order by id", null)
                val c = sqlite.query(
                    "Cities",
                    listOf("id", "cname", "state_code", "country_code").toTypedArray(),
                    "country_code=? and state_code=?",
                    listOf("IN", "GJ").toTypedArray(),
                    null,
                    null,
                    "id"
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        internal fun getEvents(userGlobalId:String): DataTable {
            var tbl = DataTable(mutableListOf(), mutableListOf(mutableListOf()), "")
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id,GlobalId,ServiceProductGlobalIdList,ServiceProductIdList,ServiceGlobalIdList,ServiceIdList,PriceList,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason,RequestStatus,PaymentStatus from Events where UserGlobalId = '$userGlobalId'",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
                tbl.Columns.add("ProductName")
                for (row in tbl.Rows) {
                    val c1 = sqlite.rawQuery(
                        "Select Title from Service_Product Where GlobalId in ('${
                            row[tbl.Columns.indexOf("ServiceProductGlobalIdList")].replace(
                                ",",
                                "','"
                            )
                        }')", null
                    )
                    val tbl_tmp = getDataTableFromCursor(c1)
                    c1.close()
                    row.add("")
                    for (r1 in tbl_tmp.Rows) {
                        row[tbl.Columns.size - 1] += r1[0] + ","
                    }

                    if(tbl_tmp.Rows.size>0) {
                        row[tbl.Columns.size - 1] =
                            row[tbl.Columns.size - 1].substring(
                                0,
                                row[tbl.Columns.size - 1].length - 1
                            )
                    }
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return tbl
        }

        internal fun getClientRequests(userGlobalId:String): DataTable {
            var tbl = DataTable(mutableListOf(), mutableListOf(mutableListOf()), "")
            try {
                openConnection()

                // check vendor services
                val c_ven_ser = sqlite.rawQuery("Select GlobalId from Service where UserGlobalId = '$userGlobalId'", null)
                val tbl_ven_ser = getDataTableFromCursor(c_ven_ser)
                c_ven_ser.close()
                val vendor_services = mutableListOf<String>()
                for (ser in tbl_ven_ser.Rows){
                    vendor_services.add(ser[0])
                }

                // for partnership checking
                val c_ven_pat_ser = sqlite.rawQuery("Select GlobalId from Partnership Where PartnerUserIdList like '$userGlobalId,%' Or PartnerUserIdList like '%,$userGlobalId,%' Or PartnerUserIdList like '%,$userGlobalId' Or PartnerUserIdList like '$userGlobalId'", null)
                val tbl_ven_pat_ser = getDataTableFromCursor(c_ven_pat_ser)
                c_ven_pat_ser.close()
                if(tbl_ven_pat_ser.Rows.size>0) {
                    val partn_id = tbl_ven_pat_ser.Rows[0][0]
                    val c_ven_partner_ser = sqlite.rawQuery("Select DISTINCT ServiceGlobalId From Partnership_Product Where Partnership_Request_GlobalId = '$partn_id'", null)
                    val tbl_ven_partner_ser = getDataTableFromCursor(c_ven_partner_ser)
                    for (ser in tbl_ven_partner_ser.Rows) {
                        if(!vendor_services.contains(ser[0]))
                            vendor_services.add(ser[0])
                    }
                }

                // if no services are available
                if(vendor_services.isEmpty()){
                    tbl.Columns.add("Title")
                    tbl.Rows.add(mutableListOf<String>("You don't have any service yet."))
                    return tbl
                }

                // if vendor has any services
                val c = sqlite.rawQuery(
                    "Select Id,GlobalId,ServiceProductGlobalIdList,ServiceProductIdList,ServiceGlobalIdList,ServiceIdList,PriceList,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason,RequestStatus,PaymentStatus,UserTurn,OrderReady,ClientMobile from Client_EVENTS_Request",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()

                // filter by vendor service
                val ind_of_ser_list_c = tbl.Columns.indexOf("ServiceGlobalIdList")
                val ind_of_prod_list_c = tbl.Columns.indexOf("ServiceProductGlobalIdList")
                val ind_of_prise_list_c = tbl.Columns.indexOf("PriceList")
                val ind_of_user_list_c = tbl.Columns.indexOf("UserTurn")

                val ind_row_delete = mutableListOf<MutableList<String>>()

                for (all_ser in tbl.Rows){
                    val sers = all_ser[ind_of_ser_list_c] // ser1, ser2
                    val prds = all_ser[ind_of_prod_list_c] // prd1, prd2
                    val prss = all_ser[ind_of_prise_list_c] // 1.0, 0.1
                    val usrtrn = all_ser[ind_of_user_list_c] // user1, user2

                    val sers_list = mutableListOf<String>()
                    val prds_list = mutableListOf<String>()
                    val prss_list = mutableListOf<String>()
                    val user_turn_list = mutableListOf<String>()

                    for (eser in sers.split(',')){ // service added in list
                        sers_list.add(eser.trim())
                    }

                    for (eser in prds.split(',')){ // product added in list
                        prds_list.add(eser.trim())
                    }

                    for (eser in prss.split(',')){ // price added in list
                        prss_list.add(eser.trim())
                    }

                    for (eser in usrtrn.split(',')){ // user turn  added in list
                        user_turn_list.add(eser.trim())
                    }

                    // got common services
                    val int_service = vendor_services.intersect(sers_list)
                    if(int_service.size == 0) {
                        ind_row_delete.add(all_ser)
                        continue
                    }
                    all_ser[ind_of_ser_list_c] = int_service.joinToString()

                    // get products of that services
                    val c_ven_prd = sqlite.rawQuery("Select GlobalId from SERVICE_PRODUCT where ServiceGlobalId in ('${int_service.joinToString("', '")}')", null)
                    val tbl_ven_prd = getDataTableFromCursor(c_ven_prd)
                    c_ven_prd.close()
                    val vendor_products = mutableListOf<String>()
                    for (prd in tbl_ven_prd.Rows){
                        vendor_products.add(prd[0])
                    }

                    // get common products
                    val int_product = vendor_products.intersect(prds_list)
                    all_ser[ind_of_prod_list_c] = int_product.joinToString()

                    // get index of common products for prices
                    val indes_of_common_product = mutableListOf<Int>()
                    for (inp in int_product){
                        indes_of_common_product.add(prds_list.indexOf(inp))
                    }

                    // create new price list
                    val new_price_list = mutableListOf<String>()
                    for (ind in indes_of_common_product){
                        new_price_list.add(prss_list[ind])
                    }

                    // create new user turn list
                    val new_user_turn_list = mutableListOf<String>()
                    for (ind in indes_of_common_product){
                        new_user_turn_list.add(user_turn_list[ind])
                    }

                    // update price
                    all_ser[ind_of_prise_list_c] = new_price_list.joinToString()
                    all_ser[ind_of_user_list_c] = new_user_turn_list.joinToString()
                }

                for (dr in ind_row_delete){
                    if(tbl.Rows.contains(dr))
                        tbl.Rows.remove(dr)
                }

                if(tbl.Rows.count()==0){
                    tbl.Rows.add(mutableListOf("You don't have any event yet"))
                    return tbl
                }

                // Product names will be added
                tbl.Columns.add("ProductName")
                for (row in tbl.Rows) {
                    val c1 = sqlite.rawQuery(
                        "Select Title from Service_Product Where GlobalId in ('${
                            row[tbl.Columns.indexOf("ServiceProductGlobalIdList")].replace(
                                ", ",
                                "','"
                            )
                        }')", null
                    )
                    val tbl_tmp = getDataTableFromCursor(c1)
                    c1.close()
                    row.add("")
                    for (r1 in tbl_tmp.Rows) {
                        row[tbl.Columns.size - 1] += r1[0] + ","
                    }
                    row[tbl.Columns.size - 1] =
                        row[tbl.Columns.size - 1].substring(0, row[tbl.Columns.size - 1].length - 1)
                }
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return tbl
        }

        internal fun getServices(userGlobalId:String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Owner,ApprovalTime,(Select Service_Type_Name from Service_Type Where Id=ServiceType)ServiceType,City,SAddress,Approved,RequestStatus from Service where UserGlobalId = '$userGlobalId'",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            else {
                for (row in tbl.Rows) {
                    row[tbl.Columns.indexOf("City")] =
                        getCityOf(row[tbl.Columns.indexOf("City")].toInt())
                }
            }
            return tbl
        }

        internal fun getServicesForEvent(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Owner,ApprovalTime,(Select Service_Type_Name from Service_Type Where Id=ServiceType)ServiceType,City,SAddress,Approved,RequestStatus from Service Where Approved = 1 and RequestStatus = 1",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            else {
                for (row in tbl.Rows) {
                    row[tbl.Columns.indexOf("City")] =
                        getCityOf(row[tbl.Columns.indexOf("City")].toInt())
                }
            }
            return tbl
        }

        internal fun getServicesProduct(serviceGlobalId: String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Price,CreationDate,ServiceId,ServiceGlobalId from Service_Product Where ServiceGlobalId='$serviceGlobalId'",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getServicesProductByServiceList(serviceGlobalId: String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Price,CreationDate,ServiceId,ServiceGlobalId from Service_Product Where ServiceGlobalId in ('${
                        serviceGlobalId.replace(
                            ",",
                            "', '"
                        )
                    }')", null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getServicesProductFilterByServiceList(
            serviceGlobalId: String,
            productGlobalId: String
        ): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select GlobalId,Title,Price from Service_Product Where ServiceGlobalId in ('${
                        serviceGlobalId.replace(
                            ",",
                            "', '"
                        )
                    }') and GlobalId in ('${productGlobalId.replace(",", "', '")}')", null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getServicesProductDetails(serviceProductGlobalId: String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Type,CreationDate,ServiceProductId,ServiceProductGlobalId from Service_Product_Detail Where ServiceProductGlobalId='$serviceProductGlobalId'",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getServicesProductInputDetails(serviceProductGlobalId: String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Type,CreationDate,ServiceProductId,ServiceProductGlobalId from Service_Product_Detail Where ServiceProductGlobalId='$serviceProductGlobalId' And Type = 3",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getCityOf(id: Int): String {
            var tbl: DataTable? = null
            try {
                openConnection(1)
                val c = sqlite.rawQuery(
                    "Select cname || ', ' || state_code || ', ' || country_code  from cities where id = $id",
                    null
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }

            var CityName = ""
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            else
                CityName = tbl.Rows[0][0]

            return CityName
        }

        fun getDonations():DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId,Motive,Description,Amount,UserId,UserGlobalId,CreatedOn,PaymentStatus from Donation", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        fun getUsers():DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Uname, Email, Id, GlobalId, Mobile from Users Where GlobalId != '${GlobalData.getUserGlobalId()}'", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        fun getUsersForTurn(userIds:String):DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId, Uname, Email from Users Where GlobalId in ('${userIds.replace(",","','")}','${GlobalData.getUserGlobalId()}')", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        fun getService(userIds:String):DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id,GlobalId,Title from Service Where UserGlobalId in ('${userIds.replace(",","','")}','${GlobalData.getUserGlobalId()}')", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        fun getServiceProduct(serviceIds:String):DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id,GlobalId,Title,ServiceGlobalId from Service_Product Where ServiceGlobalId in ('${serviceIds.replace(",","','")}')", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        fun getServiceProductByProductId(productIds:String):DataTable{
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id,GlobalId,Title from Service_Product Where GlobalId in ('${productIds.replace(",","','")}')", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null) {
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf(mutableListOf("Error")),
                    "No Values"
                )
            }
            return tbl
        }

        internal fun getSamagriEvents(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
                val c = sqlite.query(
                    "Table1",
                    listOf("id", "F03").toTypedArray(),
                    null,
                    null,
                    "F03",
                    null,
                    "id"
                )
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        internal fun getSamagriEventsWise(EventId:Int): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
                val c = sqlite.rawQuery("Select T1.Id EId,T3.Id SID,T1.F03 EName,T3.F03 SName from Table1 t1 join Table2 t2 on t1.Id = t2.T1id join Table3 T3 on t3.id = t2.T3ID Where T1.Id in ($EventId)",null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(
                    mutableListOf("Error"),
                    mutableListOf<MutableList<String>>(mutableListOf("Error")),
                    "Error"
                )
            return tbl
        }

        // region Utility

        private fun getDataTableFromCursor(c: Cursor): DataTable {
            val columns = ArrayList<String>()
            val row = mutableListOf<MutableList<String>>()
            for (col in c.columnNames)
                columns.add(col)
            while (c.moveToNext()) {
                val data = ArrayList<String>()
                for (i in 0..columns.size - 1) {
                    if (c.getString(i) != null)
                        data.add(
                            c.getString(i).replace("पी एम", "PM").replace("ए एम", "AM")
                                .replace("\\", "")
                        )
                    else
                        data.add(c.getString(i))
                }
                row.add(data)
            }
            return DataTable(columns, row, "")
        }

        internal fun getRowCount(tbl: String, col: String, con: String): Int {
            var rowCount = -1
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Count(id) from $tbl Where $col = '$con'", null)
                rowCount = getDataTableFromCursor(c).Rows[0][0].toInt()
                c.close()
            } catch (ex: Exception) {
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
                rowCount = -1
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return rowCount
        }

        internal fun getRowCount(tbl: String, col: String, con: Int): Int {
            var rowCount = 0
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Count(id) from $tbl Where $col = $con", null)
                rowCount = getDataTableFromCursor(c).Rows[0][0].toInt()
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
                LogManagement.Log(ex.message+" "+ex.stackTraceToString(),"Error")
            } finally {
                closeConnection()
            }
            return rowCount
        }

        // endregion
    }
}