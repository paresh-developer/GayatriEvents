package com.pareshkumarsharma.gayatrievents.api.model

data class DonationDisplayModel(
    val Motive:String,
    val GlobalId:String,
    val UserGlobalId:String,
    val Desc:String,
    val Amount:Float,
    val PaymentStatus:Short,
    val CreatedOn:String,
)
