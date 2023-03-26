package com.pareshkumarsharma.gayatrievents.api.model

internal data class ServiceUpdationRequestModel(
    val GlobalId:String,
    val Title:String,
    val Desc:String,
    val ServiceType:Int,
    val Address: String,
    val City: Int
)
