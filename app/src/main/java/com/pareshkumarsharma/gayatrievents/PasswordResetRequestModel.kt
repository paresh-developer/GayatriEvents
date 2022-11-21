package com.pareshkumarsharma.gayatrievents

data class PasswordResetRequestModel(
    val User_Email: String,
    val User_Mobile: String,
    val User_Password: String,
    val IsResetFromEmail: Boolean,
    val Secure_Code: String,
    val Password_Type: Int
)
