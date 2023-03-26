package com.pareshkumarsharma.gayatrievents.panchang

internal enum class PakshaHindi(val p:Int) {

    Shukla(1),
    Krishna(2);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            when(obj){
                1 -> str ="शुक्ल"
                2 -> str ="कृष्ण"
            }
            return str
        }
    }
}