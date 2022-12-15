package com.pareshkumarsharma.gayatrievents.api.model

data class ServiceDisplayModel(
    val GlobalId:String,
    val Owner:String,
    val Title:String,
    val Desc:String,
    val ServiceType:Int,
    val Address: String,
    val City: Int,
    val ApprovalTime: String
)
