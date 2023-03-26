package com.pareshkumarsharma.gayatrievents.api.model

data class EventRegistrationModel(
    val EventName: String,
    val EventDateFixed: Boolean,
    val EventDateStart: String,
    val EventDateEnd: String,
    val EventPriceList: String,
    val ServiceGlobalIdList: String,
    val ServiceProductGlobalIdList: String,
    val EventDetails: String,
)