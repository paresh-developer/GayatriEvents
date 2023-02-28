package com.pareshkumarsharma.gayatrievents.panchang

internal enum class EnWeekDay(val d:Int) {
    Sunday(1),
    Monday(2),
    Tuesday(3),
    Wednesday(4),
    Thursday(5),
    Friday(6),
    Saturday(7);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            for(v in EnWeekDay.values()){
                if(v.d == obj){
                    str = v.name
                    break
                }
            }
            return str
        }
    }
}