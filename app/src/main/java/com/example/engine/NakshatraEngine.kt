package com.example.engine

import com.example.model.CityLocation
import com.example.model.NakshatraDetail
import java.time.LocalDate
import kotlin.math.floor

/**
 * 27 நட்சத்திரங்களின் முழுமையான ஆரம்பம், முடிவு நேரம், தினசரி உதயம்,
 * அதிபதி கிரகம், அதிதேவதை மற்றும் பாகை விபரங்களைக் கணக்கிடும் பிரத்யேகக் கோப்பு.
 */
object NakshatraEngine {

    /**
     * 27 நட்சத்திரங்களின் நிலையான தரவுகள் (Static Astronomical & Astrological Data for 27 Stars)
     */
    data class NakshatraStaticData(
        val index: Int,
        val nameTa: String,
        val nameEn: String,
        val lordTa: String,
        val lordEn: String,
        val deityTa: String,
        val deityEn: String,
        val symbolTa: String,
        val symbolEn: String,
        val ganaTa: String,
        val ganaEn: String
    )

    val ALL_27_NAKSHATRAS = listOf(
        NakshatraStaticData(1, "அஸ்வினி", "Ashwini", "கேது", "Ketu", "அஸ்வினி குமாரர்கள்", "Ashwini Kumaras", "குதிரை தலை", "Horse Head", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(2, "பரணி", "Bharani", "சுக்கிரன்", "Venus", "எமன்", "Yama", "யோனி / முக்கோணம்", "Yoni / Triangle", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(3, "கார்த்திகை", "Krittika", "சூரியன்", "Sun", "அக்னி", "Agni", "கத்தி / வாள்", "Razor / Flame", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(4, "ரோகிணி", "Rohini", "சந்திரன்", "Moon", "பிரம்மன்", "Brahma", "தேர் / வண்டி", "Chariot / Cart", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(5, "மிருகசீரிஷம்", "Mrigashirsha", "செவ்வாய்", "Mars", "சந்திரன் / சோமன்", "Soma", "மான் தலை", "Deer Head", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(6, "திருவாதிரை", "Ardra", "ராகு", "Rahu", "ருத்ரன் (சிவன்)", "Rudra (Shiva)", "கண்ணீர்த்துளி / ரத்தினம்", "Teardrop / Jewel", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(7, "புனர்பூசம்", "Punarvasu", "குரு (வியாழன்)", "Jupiter", "அதிதி", "Aditi", "வில் / அம்புக்கூடு", "Bow and Quiver", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(8, "பூசம்", "Pushya", "சனி", "Saturn", "பிருஹஸ்பதி", "Brihaspati", "தாமரை மலர் / பசுவின் மடி", "Lotus / Cow Udder", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(9, "ஆயில்யம்", "Ashlesha", "புதன்", "Mercury", "சர்ப்பம் (நாகர்)", "Nagas", "சுருண்ட பாம்பு", "Coiled Serpent", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(10, "மகம்", "Magha", "கேது", "Ketu", "பித்ருக்கள்", "Pitris", "பல்லக்கு / சிம்மாசனம்", "Palanquin / Throne", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(11, "பூரம்", "Purva Phalguni", "சுக்கிரன்", "Venus", "பகன்", "Bhaga", "ஊஞ்சல் / கட்டில் கால்கள்", "Hammock / Bed legs", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(12, "உத்திரம்", "Uttara Phalguni", "சூரியன்", "Sun", "அரியமான்", "Aryaman", "கட்டில் / மெத்தை", "Bed / Mattress", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(13, "அஸ்தம்", "Hasta", "சந்திரன்", "Moon", "சாவித்ரி (சூரியன்)", "Savitr", "கை உள்ளங்கை", "Open Hand / Palm", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(14, "சித்திரை", "Chitra", "செவ்வாய்", "Mars", "விஸ்வகர்மா", "Tvashtar / Vishwakarma", "பளபளக்கும் ரத்தினம்", "Bright Jewel", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(15, "சுவாதி", "Swati", "ராகு", "Rahu", "வாயு பகவான்", "Vayu", "பவள மணி / இளம் தளிர்", "Coral / Young Sprout", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(16, "விசாகம்", "Vishakha", "குரு (வியாழன்)", "Jupiter", "இந்திராக்கினி", "Indra-Agni", "தோரணம் / குயவர் சக்கரம்", "Triumphal Arch / Wheel", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(17, "அனுஷம்", "Anuradha", "சனி", "Saturn", "மித்ரன்", "Mitra", "தாமரை / குடை", "Lotus / Umbrella", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(18, "கேட்டை", "Jyeshtha", "புதன்", "Mercury", "இந்திரன்", "Indra", "குடை / காதணி / வட்டவடிவம்", "Umbrella / Earring", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(19, "மூலம்", "Mula", "கேது", "Ketu", "நிருதி", "Nirriti", "மரத்தின் வேர்க்கொத்து", "Tied Roots", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(20, "பூராடம்", "Purva Ashadha", "சுக்கிரன்", "Venus", "ஆபஸ் (நீர் தெய்வம்)", "Apas", "முறம் / யானை தந்தம்", "Winnowing Fan / Tusk", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(21, "உத்திராடம்", "Uttara Ashadha", "சூரியன்", "Sun", "விஸ்வதேவர்கள்", "Vishvadevas", "யானை தந்தம் / சிறு பலகை", "Elephant Tusk / Cot", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(22, "திருவோணம்", "Shravana", "சந்திரன்", "Moon", "மகாவிஷ்ணு", "Vishnu", "மூன்று பாதச்சுவடுகள் / காது", "Three Footprints / Ear", "தேவ கணம்", "Deva Gana"),
        NakshatraStaticData(23, "அவிட்டம்", "Dhanishta", "செவ்வாய்", "Mars", "அஷ்ட வசுக்கள்", "Eight Vasus", "மிருதங்கம் / உடுக்கை", "Drum / Flute", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(24, "சதயம்", "Shatabhisha", "ராகு", "Rahu", "வருணன்", "Varuna", "நூறு நட்சத்திரங்கள் / வட்டம்", "100 Stars / Empty Circle", "ராட்சச கணம்", "Rakshasa Gana"),
        NakshatraStaticData(25, "பூரட்டாதி", "Purva Bhadrapada", "குரு (வியாழன்)", "Jupiter", "அஜைகபாதன்", "Aja Ekapada", "இரட்டை முகம் / வாள்", "Two-faced Man / Sword", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(26, "உத்திரட்டாதி", "Uttara Bhadrapada", "சனி", "Saturn", "அகிர்புத்னியன்", "Ahirbudhnya", "பாம்பு / ஆழ்கடல் சுருள்", "Serpent of Deep / Bed", "மனித கணம்", "Manushya Gana"),
        NakshatraStaticData(27, "ரேவதி", "Revati", "புதன்", "Mercury", "பூஷா (வழிகாட்டி)", "Pushan", "மீன் ஜோடி / முரசு", "Pair of Fish / Drum", "தேவ கணம்", "Deva Gana")
    )

    private val RASIS = listOf(
        Pair("மேஷம்", "Mesha (Aries)"),
        Pair("ரிஷபம்", "Vrishabha (Taurus)"),
        Pair("மிதுனம்", "Mithuna (Gemini)"),
        Pair("கடகம்", "Kataka (Cancer)"),
        Pair("சிம்மம்", "Simha (Leo)"),
        Pair("கன்னி", "Kanya (Virgo)"),
        Pair("துலாம்", "Thula (Libra)"),
        Pair("விருச்சிகம்", "Vrishchika (Scorpio)"),
        Pair("தனுசு", "Dhanus (Sagittarius)"),
        Pair("மகரம்", "Makara (Capricorn)"),
        Pair("கும்பம்", "Kumbha (Aquarius)"),
        Pair("மீனம்", "Meena (Pisces)")
    )

    /**
     * கொடுக்கப்பட்ட தேதி மற்றும் ஊருக்கு அன்றைய நட்சத்திரத்தின் முழுமையான
     * ஆரம்ப நேரம், முடிவு நேரம், பாதம், ராசி, மற்றும் அடுத்த நட்சத்திர விபரங்களைக் கணக்கிடுகிறது.
     */
    fun calculateDailyNakshatra(date: LocalDate, city: CityLocation): NakshatraDetail {
        val jdSunrise = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (360.0 - 330.0) / 1440.0
        val moonSidereal = calculateMoonSidereal(jdSunrise)

        val nakshatraSpan = 360.0 / 27.0 // 13.333333°
        val nakshatraIndex = (moonSidereal / nakshatraSpan).toInt().coerceIn(0, 26)
        val pada = ((moonSidereal % nakshatraSpan) / (nakshatraSpan / 4.0)).toInt().coerceIn(0, 3) + 1
        val moonRasiIndex = (moonSidereal / 30.0).toInt().coerceIn(0, 11)

        val staticData = ALL_27_NAKSHATRAS[nakshatraIndex]
        val nextStaticData = ALL_27_NAKSHATRAS[(nakshatraIndex + 1) % 27]

        val sunriseMinutes = 360 + (city.longitude - 80.0) * -2.0 + (date.monthValue % 3) * 3

        // Find Start Time (when Moon crossed the start boundary of current Nakshatra)
        val startTimeStr = findNakshatraStartTimeStr(jdSunrise, sunriseMinutes.toInt(), nakshatraIndex)

        // Find End Time (when Moon crosses the end boundary of current Nakshatra)
        val endTimeMinutes = findNakshatraEndTimeMinutes(jdSunrise, sunriseMinutes.toInt(), nakshatraIndex)
        val endTimeStr = formatTransitionTime(endTimeMinutes)

        val startDegree = nakshatraIndex * nakshatraSpan
        val endDegree = (nakshatraIndex + 1) * nakshatraSpan

        val isToday = date == LocalDate.now()
        val currentMinutesNow = if (isToday) {
            val now = java.time.LocalTime.now()
            now.hour * 60 + now.minute
        } else {
            sunriseMinutes.toInt()
        }

        val isFinished = isToday && currentMinutesNow >= endTimeMinutes

        val liveIdx = if (isFinished) {
            val jdNow = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (currentMinutesNow - 330.0) / 1440.0
            val moonNow = calculateMoonSidereal(jdNow)
            (moonNow / nakshatraSpan).toInt().coerceIn(0, 26)
        } else {
            nakshatraIndex
        }

        val livePada = if (isFinished) {
            val jdNow = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (currentMinutesNow - 330.0) / 1440.0
            val moonNow = calculateMoonSidereal(jdNow)
            ((moonNow % nakshatraSpan) / (nakshatraSpan / 4.0)).toInt().coerceIn(0, 3) + 1
        } else {
            pada
        }

        val liveRasiIndex = if (isFinished) {
            val jdNow = computeJulianDay(date.year, date.monthValue, date.dayOfMonth) + (currentMinutesNow - 330.0) / 1440.0
            val moonNow = calculateMoonSidereal(jdNow)
            (moonNow / 30.0).toInt().coerceIn(0, 11)
        } else {
            moonRasiIndex
        }

        val liveStaticData = ALL_27_NAKSHATRAS[liveIdx]

        return NakshatraDetail(
            index = nakshatraIndex + 1,
            nameTamil = staticData.nameTa,
            nameEnglish = staticData.nameEn,
            startDegree = startDegree,
            endDegree = endDegree,
            pada = pada,
            rasiTamil = RASIS[moonRasiIndex].first,
            rasiEnglish = RASIS[moonRasiIndex].second,
            rulingPlanetTamil = staticData.lordTa,
            rulingPlanetEnglish = staticData.lordEn,
            deityTamil = staticData.deityTa,
            deityEnglish = staticData.deityEn,
            symbolTamil = staticData.symbolTa,
            symbolEnglish = staticData.symbolEn,
            ganaTamil = staticData.ganaTa,
            ganaEnglish = staticData.ganaEn,
            startTime = startTimeStr,
            endTime = endTimeStr,
            durationHours = 24.0,
            nextNakshatraTamil = nextStaticData.nameTa,
            nextNakshatraEnglish = nextStaticData.nameEn,
            isFinished = isFinished,
            currentLiveNameTamil = liveStaticData.nameTa,
            currentLiveNameEnglish = liveStaticData.nameEn,
            currentLivePada = livePada,
            currentLiveRasiTamil = RASIS[liveRasiIndex].first,
            currentLiveRasiEnglish = RASIS[liveRasiIndex].second,
            currentLiveRulingPlanetTamil = liveStaticData.lordTa,
            currentLiveRulingPlanetEnglish = liveStaticData.lordEn
        )
    }

    /**
     * ஒரு குறிப்பிட்ட மாதத்திற்கான அனைத்து 27 நட்சத்திரங்களின் தினசரி அட்டவணையைப் பெற
     */
    fun getMonthlyNakshatraSchedule(year: Int, month: Int, city: CityLocation): List<Pair<LocalDate, NakshatraDetail>> {
        val daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth()
        val schedule = mutableListOf<Pair<LocalDate, NakshatraDetail>>()
        for (day in 1..daysInMonth) {
            val d = LocalDate.of(year, month, day)
            schedule.add(Pair(d, calculateDailyNakshatra(d, city)))
        }
        return schedule
    }

    // --- Astronomical Root Finding ---

    private fun findNakshatraStartTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): String {
        val nakshatraSpan = 360.0 / 27.0
        val targetAngle = currentNakshatraIndex * nakshatraSpan
        var lowHours = -30.0
        var highHours = 0.0

        for (step in 0..50) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            var moonMid = calculateMoonSidereal(jdMid)

            if (currentNakshatraIndex == 0 && moonMid > 180.0) {
                moonMid -= 360.0
            }

            if (moonMid >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val startMinutesFromMidnight = sunriseMinutes + highHours * 60.0
        return formatTransitionTime(startMinutesFromMidnight.toInt())
    }

    private fun findNakshatraEndTimeMinutes(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): Int {
        val nakshatraSpan = 360.0 / 27.0
        val targetAngle = (currentNakshatraIndex + 1) * nakshatraSpan
        var lowHours = 0.0
        var highHours = 30.0

        for (step in 0..50) {
            val midHours = (lowHours + highHours) / 2.0
            val jdMid = jdSunrise + (midHours / 24.0)
            var moonMid = calculateMoonSidereal(jdMid)

            if (currentNakshatraIndex == 26 && moonMid < 180.0) {
                moonMid += 360.0
            }

            if (moonMid >= targetAngle) {
                highHours = midHours
            } else {
                lowHours = midHours
            }
        }

        val endMinutesFromMidnight = sunriseMinutes + highHours * 60.0
        return endMinutesFromMidnight.toInt()
    }

    private fun findNakshatraEndTimeStr(jdSunrise: Double, sunriseMinutes: Int, currentNakshatraIndex: Int): String {
        return formatTransitionTime(findNakshatraEndTimeMinutes(jdSunrise, sunriseMinutes, currentNakshatraIndex))
    }

    private fun calculateMoonSidereal(jd: Double): Double {
        val d = jd - 2451545.0
        val L = (218.316 + 13.176396 * d) % 360.0
        val M = (134.963 + 13.064993 * d) % 360.0
        val F = (93.272 + 13.229350 * d) % 360.0

        val Mrad = Math.toRadians(M)
        val Frad = Math.toRadians(F)

        var lon = L + 6.289 * Math.sin(Mrad) - 1.274 * Math.sin(Mrad - 2 * Frad) + 0.658 * Math.sin(2 * Frad)
        lon = (lon % 360.0 + 360.0) % 360.0

        val ayanamsa = calculateLahiriAyanamsa(jd)
        return (lon - ayanamsa + 360.0) % 360.0
    }

    private fun calculateLahiriAyanamsa(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.85 + 1.396 * t
    }

    private fun computeJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun formatTransitionTime(totalMinutesFromMidnight: Int): String {
        var m = totalMinutesFromMidnight
        var prefix = ""
        if (m < 0) {
            m += 1440
            prefix = "நேற்று இரவு "
        } else if (m >= 1440) {
            m -= 1440
            prefix = "நாளை விடியற்காலை "
        }
        val hour24 = m / 60
        val minute = m % 60
        val period = if (hour24 < 12) "AM" else "PM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        return String.format("%s%02d:%02d %s", prefix, hour12, minute, period)
    }
}
