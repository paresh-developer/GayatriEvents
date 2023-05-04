package com.pareshkumarsharma.gayatrievents.api.model

data class NewPatnershipRequestModel(
    val PartnerUserGlobalIdList:String,
    val PartnershipShare:Float = 50.0f,
    val Product:List<NewPatnershipProductRequestModel>
)

data class NewPatnershipProductRequestModel(
    val ServiceGlobalId:String,
    val ProductGlobalId:String,
    val Turn:Int
)
