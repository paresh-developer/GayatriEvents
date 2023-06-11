package com.pareshkumarsharma.gayatrievents.api.model

data class EventDisplayModel(
    val EventGlobalId: String,
    val EventPriceList: String,
    val ServiceGlobalIdList: String,
    val ServiceProductGlobalIdList: String,
    val Approved: Boolean,
    val Approval_Time: String,
    val UserGlobalId: String,
    val CreationTime:String,
    val Reason:String,
    val PaymentStatus: Short,
    val RequestStatus:Short,
    val UserTurn:String,
    val OrderRead:Short,
    val ClientMobile: String
)
