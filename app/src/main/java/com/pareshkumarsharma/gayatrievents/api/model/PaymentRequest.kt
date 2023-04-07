package com.pareshkumarsharma.gayatrievents.api.model

data class PaymentRequest(
    val Note:String,
    val RefId:String,
    val Amount:Float,
    val PaymentMode:Int,
    val PaymentType:Char
)
