package com.pareshkumarsharma.gayatrievents.utilities

import android.content.SharedPreferences

internal class GlobalData {
    companion object{
        private var UserGlobalId:String = ""
        fun getUserGlobalId():String{
            return UserGlobalId
        }

        fun setUserGlobalId(data:SharedPreferences){
//            .putString("token", "")
//                .putString("expires", "")
//                .putString("LLUname", "")
//                .putString("LLMobile", "")
//                .putString("LLPassword", "")
//                .putBoolean("LLDone", false)
            val uname = data.getString("LLUname","").toString()
            val mobile = data.getString("LLMobile","").toString()
            val password = data.getString("LLPassword","").toString()
            UserGlobalId = Database.getUserGlobalId(uname,mobile,password)
        }
    }
}