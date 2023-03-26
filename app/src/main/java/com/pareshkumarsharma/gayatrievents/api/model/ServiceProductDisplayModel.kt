package com.pareshkumarsharma.gayatrievents.api.model

internal data class ServiceProductDisplayModel(
    val GlobalId:String,
    val ServiceGlobalId:String,
    val Title:String,
    val Desc:String,
    val Price: Float,
    val CreationDate: String
)
