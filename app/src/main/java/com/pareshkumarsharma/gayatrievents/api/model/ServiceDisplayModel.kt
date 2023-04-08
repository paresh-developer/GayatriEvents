package com.pareshkumarsharma.gayatrievents.api.model

internal data class ServiceDisplayModel(
    val GlobalId:String,
    val Owner:String,
    val Title:String,
    val Desc:String,
    val ServiceType:Int,
    val Address: String,
    val City: Int,
    val Approved: Boolean,
    val ApprovalTime: String,
    val RequestStatus: Boolean,
    val UserGlobalId:String
)
