package com.pareshkumarsharma.gayatrievents.utilities

internal data class DataTable(
    val Columns: MutableList<String>,
    var Rows: MutableList<MutableList<String>>,
    val Error: String
)
