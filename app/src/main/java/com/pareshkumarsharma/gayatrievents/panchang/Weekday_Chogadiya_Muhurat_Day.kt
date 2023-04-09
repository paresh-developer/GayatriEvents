package com.pareshkumarsharma.gayatrievents.panchang

enum class Weekday_Chogadiya_Muhurat_Day(d:Int) {
    Amrut(1),
    Kal(2),
    Shubh(3),
    Rog(4),
    Udveg(5),
    Chal(6),
    Labh(7);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            when(obj){
                1 -> str = "अमृत"
                6 -> str = "चल"
                4 -> str = "रोग"
                2 -> str = "काल"
                7 -> str = "लाभ"
                5 -> str = "उद्वेग"
                3 -> str = "शुभ"
            }
            return str
        }
    }
}