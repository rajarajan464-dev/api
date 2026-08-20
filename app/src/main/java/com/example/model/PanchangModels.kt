package com.example.model

import java.time.LocalDate

data class CityLocation(
    val id: String,
    val nameTamil: String,
    val nameEnglish: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
) {
    companion object {
        val DEFAULT_CITIES = listOf(
            CityLocation("ariyalur", "அரியலூர்", "Ariyalur", 11.1401, 79.0786, 5.5),
            CityLocation("chengalpattu", "செங்கல்பட்டு", "Chengalpattu", 12.6939, 79.9757, 5.5),
            CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5),
            CityLocation("coimbatore", "கோயம்புத்தூர்", "Coimbatore", 11.0168, 76.9558, 5.5),
            CityLocation("cuddalore", "கடலூர்", "Cuddalore", 11.7480, 79.7714, 5.5),
            CityLocation("dharmapuri", "தர்மபுரி", "Dharmapuri", 12.1211, 78.1582, 5.5),
            CityLocation("dindigul", "திண்டுக்கல்", "Dindigul", 10.3673, 77.9803, 5.5),
            CityLocation("erode", "ஈரோடு", "Erode", 11.3410, 77.7172, 5.5),
            CityLocation("kallakurichi", "கள்ளக்குறிச்சி", "Kallakurichi", 11.7384, 78.9639, 5.5),
            CityLocation("kanchipuram", "காஞ்சீபுரம்", "Kanchipuram", 12.8342, 79.7036, 5.5),
            CityLocation("kanyakumari", "கன்னியாகுமரி (நாகர்கோவில்)", "Kanyakumari / Nagercoil", 8.1833, 77.4119, 5.5),
            CityLocation("karur", "கரூர்", "Karur", 10.9601, 78.0766, 5.5),
            CityLocation("krishnagiri", "கிருஷ்ணகிரி", "Krishnagiri", 12.5186, 78.2137, 5.5),
            CityLocation("madurai", "மதுரை", "Madurai", 9.9252, 78.1198, 5.5),
            CityLocation("mayiladuthurai", "மயிலாடுதுறை", "Mayiladuthurai", 11.1018, 79.6521, 5.5),
            CityLocation("nagapattinam", "நாகப்பட்டினம்", "Nagapattinam", 10.7672, 79.8449, 5.5),
            CityLocation("namakkal", "நாமக்கல்", "Namakkal", 11.2189, 78.1674, 5.5),
            CityLocation("nilgiris", "நீலகிரி (உதகமண்டலம்)", "Nilgiris (Ooty)", 11.4102, 76.6950, 5.5),
            CityLocation("perambalur", "பெரம்பலூர்", "Perambalur", 11.2333, 78.8833, 5.5),
            CityLocation("pudukkottai", "புதுக்கோட்டை", "Pudukkottai", 10.3797, 78.8202, 5.5),
            CityLocation("ramanathapuram", "இராமநாதபுரம்", "Ramanathapuram", 9.3639, 78.8395, 5.5),
            CityLocation("ranipet", "ராணிப்பேட்டை", "Ranipet", 12.9272, 79.3323, 5.5),
            CityLocation("salem", "சேலம்", "Salem", 11.6643, 78.1460, 5.5),
            CityLocation("sivaganga", "சிவகங்கை", "Sivaganga", 9.8433, 78.4809, 5.5),
            CityLocation("tenkasi", "தென்காசி", "Tenkasi", 8.9593, 77.3150, 5.5),
            CityLocation("thanjavur", "தஞ்சாவூர்", "Thanjavur", 10.7870, 79.1378, 5.5),
            CityLocation("theni", "தேனி", "Theni", 10.0104, 77.4768, 5.5),
            CityLocation("thoothukudi", "தூத்துக்குடி", "Thoothukudi", 8.7642, 78.1348, 5.5),
            CityLocation("trichy", "திருச்சிராப்பள்ளி", "Tiruchirappalli", 10.7905, 78.7047, 5.5),
            CityLocation("tirunelveli", "திருநெல்வேலி", "Tirunelveli", 8.7139, 77.7567, 5.5),
            CityLocation("tirupathur", "திருப்பத்தூர்", "Tirupathur", 12.4920, 78.5678, 5.5),
            CityLocation("tiruppur", "திருப்பூர்", "Tiruppur", 11.1085, 77.3411, 5.5),
            CityLocation("tiruvallur", "திருவள்ளூர்", "Tiruvallur", 13.1432, 79.9082, 5.5),
            CityLocation("tiruvannamalai", "திருவண்ணாமலை", "Tiruvannamalai", 12.2253, 79.0747, 5.5),
            CityLocation("tiruvarur", "திருவாரூர்", "Tiruvarur", 10.7727, 79.6365, 5.5),
            CityLocation("vellore", "வேலூர்", "Vellore", 12.9165, 79.1325, 5.5),
            CityLocation("viluppuram", "விழுப்புரம்", "Viluppuram", 11.9401, 79.4861, 5.5),
            CityLocation("virudhunagar", "விருதுநகர்", "Virudhunagar", 9.5680, 77.9624, 5.5),
            CityLocation("puducherry", "புதுச்சேரி", "Puducherry", 11.9416, 79.8083, 5.5),
            CityLocation("karaikal", "காரைக்கால்", "Karaikal", 10.9254, 79.8380, 5.5),
            CityLocation("jaffna", "யாழ்ப்பாணம்", "Jaffna", 9.6615, 80.0255, 5.5),
            CityLocation("singapore", "சிங்கப்பூர்", "Singapore", 1.3521, 103.8198, 8.0),
            CityLocation("kl", "கோலாலம்பூர்", "Kuala Lumpur", 3.1390, 101.6869, 8.0),
            CityLocation("london", "லண்டன்", "London", 51.5074, -0.1278, 1.0),
            CityLocation("newyork", "நியூயார்க்", "New York", 40.7128, -74.0060, -4.0)
        )
    }
}

data class RasiPalanItem(
    val rasiIndex: Int,
    val nameTamil: String,
    val nameEnglish: String,
    val symbol: String,
    val lordTamil: String,
    val lordEnglish: String,
    val generalPredictionTa: String,
    val generalPredictionEn: String,
    val financeCareerTa: String,
    val financeCareerEn: String,
    val familyHealthTa: String,
    val familyHealthEn: String,
    val luckyNumber: String,
    val luckyColorTa: String,
    val luckyColorEn: String,
    val remedyTa: String,
    val remedyEn: String,
    val isChandrashtama: Boolean = false,
    val ratingStars: Int = 4
)

data class TithiInfo(
    val index: Int, // 1 to 30
    val nameTamil: String,
    val nameEnglish: String,
    val pakshaTamil: String, // வளர்பிறை / தேய்பிறை
    val pakshaEnglish: String, // Sukla Paksha / Krishna Paksha
    val endTime: String,
    val deityTamil: String,
    val deityEnglish: String,
    val startTime: String = "",
    val durationHours: Double = 0.0,
    val endTimeMinutes: Int = 0,
    val isFinished: Boolean = false,
    val nextNameTamil: String = "",
    val nextNameEnglish: String = "",
    val nextPakshaTamil: String = "",
    val nextPakshaEnglish: String = "",
    val currentLiveNameTamil: String = nameTamil,
    val currentLiveNameEnglish: String = nameEnglish,
    val currentLivePakshaTamil: String = pakshaTamil,
    val currentLivePakshaEnglish: String = pakshaEnglish
)

data class NakshatraInfo(
    val index: Int, // 1 to 27
    val nameTamil: String,
    val nameEnglish: String,
    val pada: Int, // 1 to 4
    val rasiTamil: String,
    val rasiEnglish: String,
    val endTime: String,
    val rulingPlanetTamil: String,
    val rulingPlanetEnglish: String,
    val startTime: String = "",
    val durationHours: Double = 0.0,
    val endTimeMinutes: Int = 0,
    val isFinished: Boolean = false,
    val nextNameTamil: String = "",
    val nextNameEnglish: String = "",
    val nextPada: Int = 1,
    val nextRasiTamil: String = "",
    val nextRasiEnglish: String = "",
    val nextRulingPlanetTamil: String = "",
    val nextRulingPlanetEnglish: String = "",
    val currentLiveNameTamil: String = nameTamil,
    val currentLiveNameEnglish: String = nameEnglish,
    val currentLivePada: Int = pada,
    val currentLiveRasiTamil: String = rasiTamil,
    val currentLiveRasiEnglish: String = rasiEnglish,
    val currentLiveRulingPlanetTamil: String = rulingPlanetTamil,
    val currentLiveRulingPlanetEnglish: String = rulingPlanetEnglish
)

data class YogaInfo(
    val index: Int, // 1 to 27
    val nameTamil: String,
    val nameEnglish: String,
    val natureTamil: String, // சுபம் / அசுபம் / சமம்
    val natureEnglish: String, // Auspicious / Inauspicious / Neutral
    val endTime: String
)

data class KaranaInfo(
    val index: Int, // 1 to 11
    val nameTamil: String,
    val nameEnglish: String,
    val typeTamil: String,
    val typeEnglish: String
)

data class VaaraInfo(
    val dayOfWeek: Int, // 1 = Sunday, 7 = Saturday
    val nameTamil: String,
    val nameEnglish: String,
    val planetTamil: String,
    val planetEnglish: String
)

data class TimeRange(
    val startTime: String,
    val endTime: String,
    val formatted: String
)

data class TimeRangePair(
    val morning: TimeRange,
    val evening: TimeRange
)

data class GowriPeriod(
    val periodIndex: Int,
    val timeSlot: String,
    val nameTamil: String,
    val nameEnglish: String,
    val qualityTamil: String, // அமிழ்தம், லாபம், சுகம், விஷம், ரோகம், சோரம், காலம், தனம்
    val qualityEnglish: String,
    val isGood: Boolean,
    val startMinutes: Int = 0,
    val endMinutes: Int = 0
)

data class HoraiPeriod(
    val periodIndex: Int,
    val timeSlot: String,
    val planetTamil: String,
    val planetEnglish: String,
    val qualityTamil: String, // உத்தமம், மத்திமம், அதமம்
    val qualityEnglish: String,
    val isGood: Boolean,
    val startMinutes: Int = 0,
    val endMinutes: Int = 0
)

data class PanchangResult(
    val dateIso: String, // YYYY-MM-DD
    val dateDisplayTamil: String,
    val dateDisplayEnglish: String,
    val gregorianDateDisplayTamil: String = "",
    val gregorianDateDisplayEnglish: String = "",
    val city: CityLocation,
    val tamilYearTamil: String,
    val tamilYearEnglish: String,
    val tamilMonthTamil: String,
    val tamilMonthEnglish: String,
    val tamilDay: Int,
    val ayanaTamil: String, // உத்தராயணம் / தட்சிணாயணம்
    val ayanaEnglish: String,
    val rituTamil: String, // இளவேனில், முதலிய ருதுக்கள்
    val rituEnglish: String,
    
    // 5 Angam
    val tithi: TithiInfo,
    val nakshatra: NakshatraInfo,
    val yoga: YogaInfo,
    val karana: KaranaInfo,
    val vaara: VaaraInfo,
    
    // Sun & Moon
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonSignTamil: String,
    val moonSignEnglish: String,
    val sunSignTamil: String,
    val sunSignEnglish: String,
    
    // Timings
    val nallaNeram: TimeRangePair,
    val gowriNallaNeram: TimeRangePair,
    val rahuKalam: TimeRange,
    val yamagandam: TimeRange,
    val kuligai: TimeRange,
    val durmuhurtham: TimeRange,
    
    // Astrological details
    val chandrashtamaStarTamil: String,
    val chandrashtamaStarEnglish: String,
    val chandrashtamaRasiTamil: String,
    val chandrashtamaRasiEnglish: String,
    val nethiram: Int, // 0, 1, 2
    val jeevan: String, // "1/2", "1", "0"
    val gowriDayList: List<GowriPeriod>,
    val gowriNightList: List<GowriPeriod> = emptyList(),
    val horaiDayList: List<HoraiPeriod>,
    val horaiNightList: List<HoraiPeriod> = emptyList(),
    
    // Festivals & Special Day Flags
    val specialEventsTamil: List<String>,
    val specialEventsEnglish: List<String>,
    val isSubhaMuhurtham: Boolean,
    val isAmavasya: Boolean,
    val isPurnima: Boolean,
    val isPradosham: Boolean,
    val isEkadashi: Boolean,
    val isKarthigai: Boolean,
    val isChaturthi: Boolean = false,
    val isSashti: Boolean = false
) {
    val isFastingDay: Boolean
        get() = isAmavasya || isPurnima || isPradosham || isEkadashi || isKarthigai || isChaturthi || isSashti
}
