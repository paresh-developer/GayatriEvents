package com.pareshkumarsharma.gayatrievents.api.model

data class EventRegistrationModel(
    val EventPriceList: String,
    val ServiceGlobalIdList: String,
    val ServiceProductGlobalIdList: String,
    val InputFields:String,
    val InputFieldValues:String
)