package com.pareshkumarsharma.gayatrievents

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class Database {
    companion object {
        internal val DBPATH = "/data/data/com.pareshkumarsharma.gayatrievents/main.db"
        private lateinit var sqlite : SQLiteDatabase
        private lateinit var cur: Cursor
        internal var lastError = ""

        internal val SHAREDFILE = "shared_pref"

        internal fun openConnection(){
            sqlite = SQLiteDatabase.openOrCreateDatabase(DBPATH, null)
        }

        internal fun closeConnection(){
            sqlite.close()
        }

        internal fun query(query: String): DataTable {
            var errorstr = ""
            val columns = ArrayList<String>()
            val data = ArrayList<String>()
            val row = ArrayList<ArrayList<String>>()
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
                                "Uname text,"+
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
                    sqlite.execSQL("Insert into USER_TYPE (Type_Name) Values ('Client'),('Brahman'),('Service Provider')")
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
            }
            catch (ex:java.lang.Exception){
                lastError = ex.message.toString()
            }
            finally {
                closeConnection()
            }
        }

        internal fun insertTo(tableName:String,values:ContentValues,colmnHack:String){
            try {
                openConnection()
                sqlite.insert(tableName,colmnHack,values)
            }
            catch (ex:Exception){
                lastError = ex.message.toString()
            }
            finally {
                closeConnection()
            }
        }
    }
}