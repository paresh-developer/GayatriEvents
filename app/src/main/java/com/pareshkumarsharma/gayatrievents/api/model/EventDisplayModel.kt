package com.pareshkumarsharma.gayatrievents.api.model

data class EventDisplayModel(
    val EventGlobalId: String,
    val EventName: String,
    val EventDateFixed: Boolean,
    val EventDateStart: String,
    val EventDateEnd: String,
    val EventPriceList: String,
    val ServiceGlobalIdList: String,
    val ServiceProductGlobalIdList: String,
    val EventDetails: String,
    val Approved: Boolean,
    val Approval_Time: String,
    val UserGlobalId: String,
    val CreationTime:String,
    val Reason:String
)
