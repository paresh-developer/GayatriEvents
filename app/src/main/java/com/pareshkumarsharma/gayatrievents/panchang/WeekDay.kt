package com.pareshkumarsharma.gayatrievents.panchang

internal enum class WeekDay(val d:Int) {
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
            for(v in WeekDay.values()){
                if(v.d == obj){
                    str = v.name
                    break
                }
            }
            return str
        }
    }
}