package com.pareshkumarsharma.gayatrievents.api.model

data class PaymentUpdateModel(
    val Status:String,
    val TxnRef:String,
    val ApprovalRefNo:String,
    val TxnId:String,
    val ResponseCode:Short,
    val Response:String,
    val PaymentStatus:Short
)
