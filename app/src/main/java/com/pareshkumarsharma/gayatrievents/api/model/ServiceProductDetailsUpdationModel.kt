package com.pareshkumarsharma.gayatrievents.api.model

internal data class ServiceProductDetailsUpdationModel(
    val GlobalId:String,
    val Title:String,
    val Desc:String,
    val DetailType: Int,
    val ServiceProductGlobalId:String
)
