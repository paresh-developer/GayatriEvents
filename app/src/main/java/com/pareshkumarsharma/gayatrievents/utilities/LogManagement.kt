package com.pareshkumarsharma.gayatrievents.utilities

import java.text.SimpleDateFormat
import java.util.*

internal class LogManagement {
    val PackagePath = "/data/data/com.pareshkumarsharma.gayatrievents/"
    val FilePath = "Logs/Log ${SimpleDateFormat("yyyy-MM-dd").format(Date())}.log"

}