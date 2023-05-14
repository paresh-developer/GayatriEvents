package com.pareshkumarsharma.gayatrievents.api.model

data class PartnershipRequestModel(
    val PartnerUserGlobalIdList: String,
    val PartnershipShare: Float,
    val PartnershipType: Int = 0, // 0 means turn 1 by 1 if turn is taken by another user then 50% amount will get by both
    val Product: List<PartnershipProductRequestModel>,
    val CreatedOn: String
)