package com.pareshkumarsharma.gayatrievents.panchang

internal enum class Month(val m:Int) {
    Chaitra(1),
    Vaishakha(2),
    Jyeshtha(3),
    Ashadha(4),
    Shravana(5),
    Bhadrapada(6),
    Ashwina(7),
    Kartika(8),
    Margashirsha(9),
    Pausha(10),
    Magha(11),
    Phalguna(12),
    Adik(13);
    companion object {
        fun get(obj: Int): String {
            var str = ""
            for(v in Month.values()){
                if(v.m == obj){
                    str = v.name
                    break
                }
            }
            return str
        }
    }
}