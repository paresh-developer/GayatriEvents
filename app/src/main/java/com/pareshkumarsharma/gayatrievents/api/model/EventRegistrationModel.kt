package com.pareshkumarsharma.gayatrievents.api.model

data class EventRegistrationModel(
    val EventName: String,
    val EventDateFixed: Boolean,
    val EventDateStart: String,
    val EventDateEnd: String,
    val EventPrice: Double,
    val ServiceGlobalId: String,
    val ServiceProductGlobalId: String,
    val EventDetails: String,
)
