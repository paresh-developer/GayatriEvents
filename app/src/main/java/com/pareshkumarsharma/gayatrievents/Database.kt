package com.pareshkumarsharma.gayatrievents

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class Database {
    companion object{
        internal val DBPATH = "/data/data/com.pareshkumarsharma.gayatrievents/main.db"
        private val sqlite = SQLiteDatabase.openOrCreateDatabase(DBPATH,null)
        private lateinit var cur : Cursor
        internal var lastError = ""

        internal fun query(query:String): DataTable{
            var errorstr = ""
            val columns = ArrayList<String>()
            val data = ArrayList<String>()
            val row = ArrayList<ArrayList<String>>()
            try {
                cur = sqlite.rawQuery(query, null)
                for (col in cur.columnNames)
                    columns.add(col)
                while (cur.moveToNext()) {
                    for (i in 0..columns.size - 1)
                        data.add(cur.getString(i))
                    row.add(data)
                }
            }
            catch (Ex:java.lang.Exception){
                errorstr += Ex.message
            }
            finally{
                cur.close()
            }
            return DataTable(columns,row,errorstr)
        }

        private fun checkTableExists(tableName: String = ""):Boolean{
            var flag = false
            try {
                if(tableName.trim().length == 0)
                    if(query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and not name = 'sqlite_sequence' and not name = 'android_metadata';").Rows[0][0].toString().toInt()>0)
                        flag = true
                else
                    if(query("SELECT count(rootpage) FROM sqlite_master WHERE type='table' and name = '$tableName';").Rows[0][0].toString().toInt()>0)
                        flag = true
            }
            catch (ex:java.lang.Exception)
            {
                lastError = ex.message.toString()
            }
            return flag
        }

        internal fun checkDatabaseSetup(){
            // required setup
            // user table
            if(checkTableExists()){
                // some table exists
                if(!checkTableExists("USERS")){
                    //User table not exists
                    sqlite.execSQL("Create table USERS (" +
                                "Id int PRIMARY KEY," +
                                "Email text," +
                                "Mobile text," +
                                "User_Password text," +
                                "User_Type int" +
                            ");")
                }

                if(!checkTableExists("USER_TYPE")){
                    //User table not exists
                    sqlite.execSQL("Create table USER_TYPE (" +
                                "Id int PRIMARY KEY," +
                                "Type_Name text"+
                            ");")
                }
            }
            else
            {

            }
        }
    }
}