package com.pareshkumarsharma.gayatrievents.panchang

internal enum class WeekDayHindi(val d:Int) {
    Raviwara(1),
    Somawara(2),
    Mangalawara(3),
    Budhawara(4),
    Guruwara(5),
    Shukrawara(6),
    Shaniwara(7);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            when(obj){
                1 -> str = "रविवार"
                2 -> str = "सोमवार"
                3 -> str = "मंगलवार"
                4 -> str = "बुधवार"
                5 -> str = "गुरुवार"
                6 -> str = "शुक्रवार"
                7 -> str = "शनिवार"
            }
            return str
        }
    }
}