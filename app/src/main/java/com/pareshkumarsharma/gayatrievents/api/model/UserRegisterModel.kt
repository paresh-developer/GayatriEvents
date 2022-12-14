package com.pareshkumarsharma.gayatrievents.api.model

data class UserRegisterModel(
    val User_Name:String,
    val User_Mobile:String,
    val User_Email:String,
    var User_Password:String,
    val User_Type: Int
)