package com.pareshkumarsharma.gayatrievents.utilities

data class DataTable(
    val Columns: List<String>,
    var Rows: MutableList<MutableList<String>>,
    val Error: String
)
