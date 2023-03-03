package com.pareshkumarsharma.gayatrievents.utilities

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class Database {
    internal companion object {
        internal val DBPATH = "/data/data/com.pareshkumarsharma.gayatrievents/main.db"
        internal val DBPATH_PANCHANG = "/data/data/com.pareshkumarsharma.gayatrievents/Panchang.db"
        private lateinit var sqlite: SQLiteDatabase
        private lateinit var cur: Cursor
        internal var lastError = ""

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
                                "RequestStatus int"+
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
                                "CreationDate datetime"+
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
                                "CreationDate datetime"+
                                ");"
                    )
                }

                if (!checkTableExists("EVENTS")) {
                    sqlite.execSQL(
                        "Create table EVENTS (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceProductGlobalId text," +
                                "ServiceProductId int," +
                                "ServiceGlobalId text," +
                                "ServiceId int," +
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "Price Numeric," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
                                ");"
                    )
                }
                if (!checkTableExists("Client_EVENTS_Request")) {
                    sqlite.execSQL(
                        "Create table Client_EVENTS_Request (" +
                                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "GlobalId text," +
                                "ServiceProductGlobalId text," +
                                "ServiceProductId int," +
                                "ServiceGlobalId text," +
                                "ServiceId int," +
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "Price Numeric," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
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
                                "ServiceProductGlobalId text," +
                                "ServiceProductId int," +
                                "ServiceGlobalId text," +
                                "ServiceId int," +
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "Price Numeric," +
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
                                "ServiceProductGlobalId text," +
                                "ServiceProductId int," +
                                "ServiceGlobalId text," +
                                "ServiceId int," +
                                "Title text," +
                                "Details text," +
                                "DateFixed int," +
                                "DateStart datetime," +
                                "DateEnd datetime," +
                                "Price Numeric," +
                                "Approved int," +
                                "Approval_Time datetime," +
                                "UserId int," +
                                "UserGlobalId text," +
                                "CreationDate datetime," +
                                "Reason text" +
                                ");"
                    )
                }
            } catch (ex: java.lang.Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
        }

        internal fun insertTo(tableName: String, values: ContentValues, colmnHack: String) {
            try {
                openConnection()
                sqlite.insert(tableName, colmnHack, values)
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
        }

        internal fun updateTo(tableName: String, values: ContentValues, whereStr: String,whereArg:Array<String>) {
            try {
                openConnection()
                sqlite.update(tableName, values,whereStr,whereArg)
            } catch (ex: Exception) {
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
                    if(adikmas == -1 || adikmas == tmpTbl.Rows.size - 1){
                         adikmas = tmpTbl.Rows.first()[0].toString().toInt()
                    }
                    else{
                        adikmas = tmpTbl.Rows[adikmas+1][0].toString().toInt()
                    }
                    tbl.Rows[0][tbl.Columns.indexOf("AmantMonth")] = (adikmas+12).toString()
                }

                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
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
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
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
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
            return tbl
        }

        internal fun getCities(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection(1)
//                val c = sqlite.query("Cities","Select id,name,state_code,country_code from Cities where country_code='IN' and state_code='GJ' order by id", null)
                val c = sqlite.query("Cities", listOf("id","name","state_code","country_code").toTypedArray(),"country_code=? and state_code=?",
                    listOf("IN","GJ").toTypedArray(),null,null,"id")
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
            return tbl
        }

        internal fun getEvents(): DataTable {
            var tbl = DataTable(listOf(), mutableListOf(mutableListOf()),"")
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id,GlobalId,ServiceProductGlobalId,ServiceProductId,ServiceGlobalId,ServiceId,Title,Details,DateFixed,DateStart,DateEnd,Price,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason from Events", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return tbl
        }

        internal fun getClientRequests(): DataTable {
            var tbl = DataTable(listOf(), mutableListOf(mutableListOf()),"")
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id,GlobalId,ServiceProductGlobalId,ServiceProductId,ServiceGlobalId,ServiceId,Title,Details,DateFixed,DateStart,DateEnd,Price,Approved,Approval_Time,UserId,UserGlobalId,CreationDate,Reason from Client_EVENTS_Request", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
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
                val c = sqlite.rawQuery("Select Id, GlobalId,Title,SmallDesc,Owner,ApprovalTime,(Select Service_Type_Name from Service_Type Where Id=ServiceType)ServiceType,City,SAddress,Approved,RequestStatus from Service", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
            else
            {
                for (row in tbl.Rows){
                    row[tbl.Columns.indexOf("City")] = getCityOf(row[tbl.Columns.indexOf("City")].toInt())
                }
            }
            return tbl
        }

        internal fun getServicesForEvent(): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId,Title,SmallDesc,Owner,ApprovalTime,(Select Service_Type_Name from Service_Type Where Id=ServiceType)ServiceType,City,SAddress,Approved,RequestStatus from Service Where Approved = 1 and RequestStatus = 1", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
            else
            {
                for (row in tbl.Rows){
                    row[tbl.Columns.indexOf("City")] = getCityOf(row[tbl.Columns.indexOf("City")].toInt())
                }
            }
            return tbl
        }

        internal fun getServicesProduct(serviceGlobalId:String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId,Title,SmallDesc,Price,CreationDate,ServiceId,ServiceGlobalId from Service_Product Where ServiceGlobalId='$serviceGlobalId'", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if(tbl==null){
                tbl = DataTable(listOf("Error"), mutableListOf(mutableListOf("Error")),"No Values")
            }
            return tbl
        }

        internal fun getServicesProductByServiceList(serviceGlobalId:String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId,Title,SmallDesc,Price,CreationDate,ServiceId,ServiceGlobalId from Service_Product Where ServiceGlobalId in ('${serviceGlobalId.replace(",","', '")}')", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if(tbl==null){
                tbl = DataTable(listOf("Error"), mutableListOf(mutableListOf("Error")),"No Values")
            }
            return tbl
        }

        internal fun getServicesProductDetails(serviceProductGlobalId:String): DataTable {
            var tbl: DataTable? = null
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Id, GlobalId,Title,SmallDesc,Type,CreationDate,ServiceProductId,ServiceProductGlobalId from Service_Product_Detail Where ServiceProductGlobalId='$serviceProductGlobalId'", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            if(tbl==null){
                tbl = DataTable(listOf("Error"), mutableListOf(mutableListOf("Error")),"No Values")
            }
            return tbl
        }

        internal fun getCityOf(id:Int):String{
            var tbl: DataTable? = null
            try {
                openConnection(1)
                val c = sqlite.rawQuery("Select name || ', ' || state_code || ', ' || country_code  from cities where id = $id", null)
                tbl = getDataTableFromCursor(c)
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }

            var CityName = ""
            if (tbl == null)
                tbl = DataTable(listOf("Error"), mutableListOf<MutableList<String>>(mutableListOf("Error")), "Error")
            else
                CityName = tbl.Rows[0][0]

            return CityName
        }

        // region Utility

        private fun getDataTableFromCursor(c: Cursor): DataTable {
            val columns = ArrayList<String>()
            val row = mutableListOf<MutableList<String>>()
            for (col in c.columnNames)
                columns.add(col)
            while (c.moveToNext()) {
                val data = ArrayList<String>()
                for (i in 0..columns.size - 1)
                    data.add(c.getString(i))
                row.add(data)
            }
            return DataTable(columns, row, "")
        }

        internal fun getRowCount(tbl:String,col:String,con:String): Int{
            var rowCount = 0
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Count(id) from $tbl Where $col = '$con'", null)
                rowCount = getDataTableFromCursor(c).Rows[0][0].toInt()
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return rowCount
        }

        internal fun getRowCount(tbl:String,col:String,con:Int): Int{
            var rowCount = 0
            try {
                openConnection()
                val c = sqlite.rawQuery("Select Count(id) from $tbl Where $col = $con", null)
                rowCount = getDataTableFromCursor(c).Rows[0][0].toInt()
                c.close()
            } catch (ex: Exception) {
                lastError = ex.message.toString()
            } finally {
                closeConnection()
            }
            return rowCount
        }

        // endregion
    }
}