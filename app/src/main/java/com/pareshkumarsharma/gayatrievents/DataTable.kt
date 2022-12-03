package com.pareshkumarsharma.gayatrievents

data class DataTable(
    val Columns: List<String>,
    var Rows: MutableList<MutableList<String>>,
    val Error: String
)
