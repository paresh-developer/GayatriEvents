package com.pareshkumarsharma.gayatrievents.panchang

enum class Weekday_Chogadiya_Muhurat_Night(d:Int) {
    Amrut(1),
    Chal(2),
    Rog(3),
    Kal(4),
    Labh(5),
    Udveg(6),
    Shubh(7);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            when(obj){
                1 -> str = "अमृत"
                2 -> str = "चल"
                3 -> str = "रोग"
                4 -> str = "काल"
                5 -> str = "लाभ"
                6 -> str = "उद्वेग"
                7 -> str = "शुभ"
            }
            return str
        }
    }
}