package com.pareshkumarsharma.gayatrievents.api.model

data class PaymentRequest(
    val Note:String,
    val EventId:String,
    val Amount:Float,
    val PaymentMode:Int,
    val PaymentType:Char
)
