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
                                ");"
                    )
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
                                "RequestStatus int" +
                                ");"
                    )
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
                                ");"
                    )
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
                                ");"
                    )
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
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text," +
                                "RequestStatus int," +
                                "PaymentStatus int" +
                                ");"
                    )
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
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text," +
                                "RequestStatus int," +
                                "PaymentStatus int" +
                                ");"
                    )
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
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "PriceList text," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
                                ");"
                    )
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
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
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
                                "PaymentStatus datetime" +
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

        internal fun getEvents(): DataTable {
            var tbl = DataTable(mutableListOf(), mutableListOf(mutableListOf()), "")
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id,GlobalId,ServiceProductGlobalIdList,ServiceProductIdList,ServiceGlobalIdList,ServiceIdList,Title,Details,DateFixed,DateStart,DateEnd,PriceList,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason,RequestStatus,PaymentStatus from Events",
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

        internal fun getClientRequests(): DataTable {
            var tbl = DataTable(mutableListOf(), mutableListOf(mutableListOf()), "")
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id,GlobalId,ServiceProductGlobalIdList,ServiceProductIdList,ServiceGlobalIdList,ServiceIdList,Title,Details,DateFixed,DateStart,DateEnd,PriceList,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason,RequestStatus,PaymentStatus from Client_EVENTS_Request",
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

        internal fun getServices(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery(
                    "Select Id, GlobalId,Title,SmallDesc,Owner,ApprovalTime,(Select Service_Type_Name from Service_Type Where Id=ServiceType)ServiceType,City,SAddress,Approved,RequestStatus from Service",
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