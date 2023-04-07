package com.pareshkumarsharma.gayatrievents.utilities

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class LogManagement {
    companion object{
        val PackagePath = "/data/data/com.pareshkumarsharma.gayatrievents/Logs"
        val FilePath = "/Log ${SimpleDateFormat("yyyy-MM-dd").format(Date())}.log"
        fun Log(txt:String, ltype:String){
            val f = File(PackagePath+FilePath)
            if(!f.exists()) {
                File(PackagePath).mkdir()
                f.createNewFile()
            }
            f.appendText("\n"+SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())+" lType : "+ltype + " "+txt+"\n")
        }

        fun ReadLogs():String{
            val f = File(PackagePath+FilePath)
            if(f.exists())
                return f.readText()
            else return "File not exists"
        }
    }
}