package com.pareshkumarsharma.gayatrievents

import android.content.ContentValues
import android.content.SharedPreferences
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
                    "Select EventDate,Festivals " +
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
    }
}