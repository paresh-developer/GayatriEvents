package com.pareshkumarsharma.gayatrievents.panchang

internal enum class Paksha(val p:Int) {

    Shukla(1),
    Krishna(2);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            for(v in Paksha.values()){
                if(v.p == obj){
                    str = v.name
                    break
                }
            }
            return str
        }
    }
}