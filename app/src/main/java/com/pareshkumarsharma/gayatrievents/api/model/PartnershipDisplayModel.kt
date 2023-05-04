package com.pareshkumarsharma.gayatrievents.api.model

data class PartnershipDisplayModel(
    val GlobalId:String,
    val UserGlobalId:String,
    val PartnerUserGlobalIdList:String,
    val PartnershipShare:Float,
    val PatnershipType: Int,
    val Product:List<PartnershipProductDisplayModel>,
    val CreatedOn:String,
    val Approval:Int,
    val RequestStatus:Int
)
