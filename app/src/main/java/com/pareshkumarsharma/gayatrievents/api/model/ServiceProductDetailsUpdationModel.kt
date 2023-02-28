package com.pareshkumarsharma.gayatrievents.api.model

data class ServiceProductDetailsUpdationModel(
    val GlobalId:String,
    val Title:String,
    val Desc:String,
    val DetailType: Int,
    val ServiceProductGlobalId:String
)
