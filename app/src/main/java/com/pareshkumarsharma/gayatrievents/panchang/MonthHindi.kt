package com.pareshkumarsharma.gayatrievents.panchang

internal enum class MonthHindi(val m:Int) {
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
            when(obj){
                1 -> str = "चैत्र"
                2 -> str = "वैशाख"
                3 -> str = "ज्येष्ठ"
                4 -> str = "आषाढ़"
                5 -> str = "श्रावण"
                6 -> str = "भाद्रपद"
                7 -> str = "आश्विन"
                8 -> str = "कार्तिक"
                9 -> str = "मार्गशीर्ष"
                10 -> str = "पौष"
                11 -> str = "माघ"
                12 -> str = "फाल्गुन"
                13 -> str = "श्रावण (अधिक)"
            }
            return str
        }
    }
}