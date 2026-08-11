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
            CityLocation("chennai", "சென்னை", "Chennai", 13.0827, 80.2707, 5.5),
            CityLocation("madurai", "மதுரை", "Madurai", 9.9252, 78.1198, 5.5),
            CityLocation("coimbatore", "கோயம்புத்தூர்", "Coimbatore", 11.0168, 76.9558, 5.5),
            CityLocation("trichy", "திருச்சிராப்பள்ளி", "Tiruchirappalli", 10.7905, 78.7047, 5.5),
            CityLocation("jaffna", "யாழ்ப்பாணம்", "Jaffna", 9.6615, 80.0255, 5.5),
            CityLocation("singapore", "சிங்கப்பூர்", "Singapore", 1.3521, 103.8198, 8.0),
            CityLocation("kl", "கோலாலம்பூர்", "Kuala Lumpur", 3.1390, 101.6869, 8.0),
            CityLocation("london", "லண்டன்", "London", 51.5074, -0.1278, 1.0),
            CityLocation("newyork", "நியூயார்க்", "New York", 40.7128, -74.0060, -4.0)
        )
    }
}

data class TithiInfo(
    val index: Int, // 1 to 30
    val nameTamil: String,
    val nameEnglish: String,
    val pakshaTamil: String, // வளர்பிறை / தேய்பிறை
    val pakshaEnglish: String, // Sukla Paksha / Krishna Paksha
    val endTime: String,
    val deityTamil: String,
    val deityEnglish: String
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
    val rulingPlanetEnglish: String
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
    val isGood: Boolean
)

data class HoraiPeriod(
    val periodIndex: Int,
    val timeSlot: String,
    val planetTamil: String,
    val planetEnglish: String,
    val qualityTamil: String, // உத்தமம், மத்திமம், அதமம்
    val qualityEnglish: String,
    val isGood: Boolean
)

data class PanchangResult(
    val dateIso: String, // YYYY-MM-DD
    val dateDisplayTamil: String,
    val dateDisplayEnglish: String,
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
    val horaiDayList: List<HoraiPeriod>,
    
    // Festivals & Special Day Flags
    val specialEventsTamil: List<String>,
    val specialEventsEnglish: List<String>,
    val isSubhaMuhurtham: Boolean,
    val isAmavasya: Boolean,
    val isPurnima: Boolean,
    val isPradosham: Boolean,
    val isEkadashi: Boolean,
    val isKarthigai: Boolean
)
